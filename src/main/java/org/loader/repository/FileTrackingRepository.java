package org.loader.repository;

import org.loader.model.FileTracking;
import org.loader.model.enums.ProcessStatus;
import org.loader.model.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


//Repository interface for FileTracking entity.

@Repository
public interface FileTrackingRepository extends JpaRepository<FileTracking, Long> {

    /**
     * Check if a file has already been processed (regardless of status)
     */
    boolean existsByProviderAndFilename(Provider provider, String filename);

    /**
     * Check if a file has been successfully processed
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FileTracking f " +
            "WHERE f.provider = :provider " +
            "AND f.filename = :filename " +
            "AND f.status = 'SUCCESS'")
    boolean existsByProviderAndFilenameAndSuccess(
            @Param("provider") Provider provider,
            @Param("filename") String filename
    );

    /**
     * Find file tracking record by provider and filename
     */
    Optional<FileTracking> findByProviderAndFilename(Provider provider, String filename);

    /**
     * Find all files processed by a specific provider
     */
    List<FileTracking> findByProvider(Provider provider);

    /**
     * Find all files with a specific status
     */
    List<FileTracking> findByStatus(ProcessStatus status);

    /**
     * Find all files processed by a provider with a specific status
     */
    List<FileTracking> findByProviderAndStatus(Provider provider, ProcessStatus status);

    /**
     * Find all files processed within a time range
     */
    List<FileTracking> findByProcessedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find the most recent successful processing for each provider
     */
    @Query("SELECT f FROM FileTracking f " +
            "WHERE f.status = 'SUCCESS' " +
            "AND f.processedAt = (" +
            "  SELECT MAX(f2.processedAt) " +
            "  FROM FileTracking f2 " +
            "  WHERE f2.provider = f.provider " +
            "  AND f2.status = 'SUCCESS'" +
            ") " +
            "ORDER BY f.provider")
    List<FileTracking> findLatestSuccessfulByProvider();

    /**
     * Find the most recent processing (any status) for a provider
     */
    @Query("SELECT f FROM FileTracking f " +
            "WHERE f.provider = :provider " +
            "ORDER BY f.processedAt DESC " +
            "LIMIT 1")
    Optional<FileTracking> findLatestByProvider(@Param("provider") Provider provider);

    /**
     * Count successful processings by provider
     */
    Long countByProviderAndStatus(Provider provider, ProcessStatus status);

    /**
     * Find failed processings that can be retried
     */
    @Query("SELECT f FROM FileTracking f " +
            "WHERE f.status = 'FAILED' " +
            "ORDER BY f.processedAt DESC")
    List<FileTracking> findFailedProcessings();
}