package org.loader.service;

import org.loader.config.LoaderConfig;
import org.loader.exception.FileAlreadyProcessedException;
import org.loader.exception.FileParsingException;
import org.loader.model.FileTracking;
import org.loader.model.PlatformInformation;
import org.loader.model.enums.ProcessStatus;
import org.loader.model.enums.Provider;
import org.loader.repository.FileTrackingRepository;
import org.loader.repository.PlatformInformationRepository;
import org.loader.service.parser.DataParser;
import org.loader.util.FileScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for loading and processing provider data files.
 * Orchestrates file scanning, parsing, transformation, and storage.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoaderService {

    private final FileScanner fileScanner;
    private final PlatformInformationRepository platformRepo;
    private final FileTrackingRepository fileTrackingRepo;
    private final List<DataParser> parsers;
    private final LoaderConfig loaderConfig;

    // Map of Provider → Parser for quick lookup
    private Map<Provider, DataParser> parserMap;

    /**
     * Initialize parser map on first use (lazy initialization).
     */
    private Map<Provider, DataParser> getParserMap() {
        if (parserMap == null) {
            parserMap = parsers.stream()
                    .collect(Collectors.toMap(DataParser::getProvider, Function.identity()));
        }
        return parserMap;
    }

    /**
     * Process a single file from a provider.
     *
     * @param file the file to process
     * @param provider the provider type
     * @return number of records processed
     */
    @Transactional
    public int processFile(File file, Provider provider) {
        String filename = file.getName();
        log.info("Processing {} file: {}", provider.getValue(), filename);

        // Check if already processed
        if (fileTrackingRepo.existsByProviderAndFilenameAndSuccess(provider, filename)) {
            String message = String.format("File already processed successfully: %s", filename);
            log.warn(message);
            throw new FileAlreadyProcessedException(message, provider, filename);
        }

        // Create tracking record with PROCESSING status
        FileTracking tracking = FileTracking.builder()
                .provider(provider)
                .filename(filename)
                .status(ProcessStatus.PROCESSING)
                .processedAt(LocalDateTime.now())
                .build();
        fileTrackingRepo.save(tracking);

        try {
            // Get appropriate parser
            DataParser parser = getParserMap().get(provider);
            if (parser == null) {
                throw new IllegalStateException("No parser found for provider: " + provider);
            }

            // Parse file
            List<PlatformInformation> entities = parser.parse(file);

            // Check max records limit
            if (entities.size() > loaderConfig.getMaxRecordsPerFile()) {
                log.warn("File {} contains {} records, exceeding limit of {}. Processing first {} records.",
                        filename, entities.size(), loaderConfig.getMaxRecordsPerFile(),
                        loaderConfig.getMaxRecordsPerFile());
                entities = entities.subList(0, loaderConfig.getMaxRecordsPerFile());
            }

            // Save to database
            List<PlatformInformation> saved = platformRepo.saveAll(entities);

            // Update tracking record - SUCCESS
            tracking.setStatus(ProcessStatus.SUCCESS);
            tracking.setRecordsProcessed(saved.size());
            tracking.setProcessedAt(LocalDateTime.now());
            fileTrackingRepo.save(tracking);

            log.info("Successfully processed {} file: {} ({} records)",
                    provider.getValue(), filename, saved.size());

            return saved.size();

        } catch (FileParsingException e) {
            // Update tracking record - FAILED
            tracking.setStatus(ProcessStatus.FAILED);
            tracking.setErrorMessage(e.getMessage());
            tracking.setProcessedAt(LocalDateTime.now());
            fileTrackingRepo.save(tracking);

            log.error("Failed to process {} file: {}", provider.getValue(), filename, e);

            if (loaderConfig.isSkipOnError()) {
                log.warn("Skipping failed file due to skipOnError=true: {}", filename);
                return 0;
            } else {
                throw e;
            }

        } catch (Exception e) {
            // Update tracking record - FAILED
            tracking.setStatus(ProcessStatus.FAILED);
            tracking.setErrorMessage(e.getMessage());
            tracking.setProcessedAt(LocalDateTime.now());
            fileTrackingRepo.save(tracking);

            log.error("Unexpected error processing {} file: {}", provider.getValue(), filename, e);

            if (loaderConfig.isSkipOnError()) {
                log.warn("Skipping failed file due to skipOnError=true: {}", filename);
                return 0;
            } else {
                throw new RuntimeException("Failed to process file: " + filename, e);
            }
        }
    }

    /**
     * Scan and process all new files for a specific provider.
     *
     * @param provider the provider to process
     * @return total number of records processed across all files
     */
    @Transactional
    public int scanAndProcessProvider(Provider provider) {
        log.info("Scanning and processing {} files", provider.getValue());

        List<File> newFiles = fileScanner.scanForNewFiles(provider);

        if (newFiles.isEmpty()) {
            log.info("No new {} files to process", provider.getValue());
            return 0;
        }

        int totalRecords = 0;
        int successCount = 0;
        int failCount = 0;

        for (File file : newFiles) {
            try {
                int recordsProcessed = processFile(file, provider);
                totalRecords += recordsProcessed;
                successCount++;
            } catch (FileAlreadyProcessedException e) {
                log.debug("Skipping already processed file: {}", file.getName());
            } catch (Exception e) {
                failCount++;
                if (!loaderConfig.isSkipOnError()) {
                    throw e; // Fail fast if skipOnError is false
                }
            }
        }

        log.info("Completed {} processing: {} files processed, {} succeeded, {} failed, {} total records",
                provider.getValue(), newFiles.size(), successCount, failCount, totalRecords);

        return totalRecords;
    }

    /**
     * Scan and process all new files from all providers.
     *
     * @return total number of records processed across all providers
     */
    @Transactional
    public int scanAndProcessAll() {
        log.info("Scanning and processing all providers");

        int totalRecords = 0;

        for (Provider provider : Provider.values()) {
            try {
                int providerRecords = scanAndProcessProvider(provider);
                totalRecords += providerRecords;
            } catch (Exception e) {
                log.error("Error processing provider: {}", provider.getValue(), e);
                if (!loaderConfig.isSkipOnError()) {
                    throw e;
                }
            }
        }

        log.info("Completed processing all providers: {} total records", totalRecords);

        return totalRecords;
    }

    /**
     * Get processing statistics for a provider.
     *
     * @param provider the provider
     * @return map of status counts
     */
    public Map<ProcessStatus, Long> getProviderStatistics(Provider provider) {
        return Map.of(
                ProcessStatus.SUCCESS, fileTrackingRepo.countByProviderAndStatus(provider, ProcessStatus.SUCCESS),
                ProcessStatus.FAILED, fileTrackingRepo.countByProviderAndStatus(provider, ProcessStatus.FAILED),
                ProcessStatus.PROCESSING, fileTrackingRepo.countByProviderAndStatus(provider, ProcessStatus.PROCESSING)
        );
    }

    /**
     * Get the latest processing information for a provider.
     *
     * @param provider the provider
     * @return FileTracking record, or null if none found
     */
    public FileTracking getLatestProcessing(Provider provider) {
        return fileTrackingRepo.findLatestByProvider(provider).orElse(null);
    }
}