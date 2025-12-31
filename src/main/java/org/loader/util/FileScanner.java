package org.loader.util;

import org.loader.config.LoaderConfig;
import org.loader.model.enums.Provider;
import org.loader.repository.FileTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class FileScanner {

    private final LoaderConfig loaderConfig;
    private final FileTrackingRepository fileTrackingRepository;

    // Pattern to match valid file names: provider_yyyy_MM_ddTHH_mm_ss
    private static final Pattern FILE_NAME_PATTERN =
            Pattern.compile("^(github|jira|clickup)_\\d{4}_\\d{2}_\\d{2}T\\d{2}_\\d{2}_\\d{2}$");

    /**
     * Scan for new files from a specific provider.
     *
     * @param provider the provider to scan for
     * @return list of unprocessed files
     */
    public List<File> scanForNewFiles(Provider provider) {
        log.info("Scanning for new {} files", provider.getValue());

        String providerDir = loaderConfig.getProviderDirectory(provider);
        File directory = new File(providerDir);

        // Check if directory exists
        if (!directory.exists() || !directory.isDirectory()) {
            log.warn("Provider directory does not exist: {}", providerDir);
            return new ArrayList<>();
        }

        // Get all files in directory
        File[] allFiles = directory.listFiles();
        if (allFiles == null || allFiles.length == 0) {
            log.info("No files found in directory: {}", providerDir);
            return new ArrayList<>();
        }

        // Filter to valid, unprocessed files
        List<File> newFiles = Arrays.stream(allFiles)
                .filter(File::isFile)
                .filter(this::isValidFileName)
                .filter(file -> !isAlreadyProcessed(provider, file.getName()))
                .sorted((f1, f2) -> f1.getName().compareTo(f2.getName())) // Sort by name (chronological)
                .collect(Collectors.toList());

        log.info("Found {} new {} files to process", newFiles.size(), provider.getValue());

        return newFiles;
    }

    /**
     * Scan for new files from all providers.
     *
     * @return map of provider to list of files
     */
    public List<File> scanAllProviders() {
        log.info("Scanning all providers for new files");

        List<File> allFiles = new ArrayList<>();

        for (Provider provider : Provider.values()) {
            List<File> providerFiles = scanForNewFiles(provider);
            allFiles.addAll(providerFiles);
        }

        log.info("Found {} total new files across all providers", allFiles.size());

        return allFiles;
    }

    /**
     * Check if filename matches the expected pattern.
     * Pattern: {provider}_yyyy_MM_dd'T'HH_mm_ss
     *
     * @param file the file to check
     * @return true if filename is valid
     */
    private boolean isValidFileName(File file) {
        String fileName = file.getName();
        boolean isValid = FILE_NAME_PATTERN.matcher(fileName).matches();

        if (!isValid) {
            log.debug("Skipping invalid filename: {}", fileName);
        }

        return isValid;
    }

    /**
     * Check if a file has already been successfully processed.
     *
     * @param provider the provider
     * @param filename the filename
     * @return true if already processed successfully
     */
    private boolean isAlreadyProcessed(Provider provider, String filename) {
        boolean exists = fileTrackingRepository.existsByProviderAndFilenameAndSuccess(provider, filename);

        if (exists) {
            log.debug("File already processed: {}", filename);
        }

        return exists;
    }

    /**
     * Extract timestamp from filename.
     * Pattern: {provider}_yyyy_MM_dd'T'HH_mm_ss
     *
     * @param filename the filename
     * @return LocalDateTime extracted from filename, or null if invalid
     */
    public LocalDateTime extractTimestampFromFilename(String filename) {
        try {
            // Remove provider prefix
            // Example: github_2024_08_22T13_30_00 -> 2024_08_22T13_30_00
            String[] parts = filename.split("_", 2);
            if (parts.length < 2) {
                return null;
            }

            String timestampPart = parts[1]; // 2024_08_22T13_30_00

            // Parse manually: yyyy_MM_dd'T'HH_mm_ss
            // Split by 'T' to get date and time parts
            String[] dateParts = timestampPart.split("T");
            if (dateParts.length != 2) {
                return null;
            }

            // Replace underscores with hyphens in date: 2024_08_22 -> 2024-08-22
            String datePart = dateParts[0].replace("_", "-");

            // Replace underscores with colons in time: 13_30_00 -> 13:30:00
            String timePart = dateParts[1].replace("_", ":");

            // Combine: 2024-08-22T13:30:00
            String formatted = datePart + "T" + timePart;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            return LocalDateTime.parse(formatted, formatter);

        } catch (Exception e) {
            log.warn("Failed to extract timestamp from filename: {}", filename, e);
            return null;
        }
    }

    /**
     * Get the latest file for a provider (by filename timestamp).
     *
     * @param provider the provider
     * @return the latest file, or null if none found
     */
    public File getLatestFile(Provider provider) {
        List<File> files = scanForNewFiles(provider);

        if (files.isEmpty()) {
            return null;
        }

        // Files are already sorted chronologically by name
        return files.get(files.size() - 1);
    }
}