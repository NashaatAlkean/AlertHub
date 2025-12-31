package org.loader.repository;

import org.loader.model.PlatformInformation;
import org.loader.model.enums.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

//Repository interface for PlatformInformation entity.

@Repository
public interface PlatformInformationRepository extends JpaRepository<PlatformInformation, Long> {

    /**
     * Find all records by developer ID
     */
    List<PlatformInformation> findByDeveloperId(String developerId);

    /**
     * Find all records by label
     */
    List<PlatformInformation> findByLabel(Label label);

    /**
     * Find all records by project
     */
    List<PlatformInformation> findByProject(String project);

    /**
     * Find all records by owner ID
     */
    List<PlatformInformation> findByOwnerId(String ownerId);

    /**
     * Find records within a time range
     */
    List<PlatformInformation> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find records by developer and time range
     */
    List<PlatformInformation> findByDeveloperIdAndTimestampAfter(String developerId, LocalDateTime after);

    /**
     * Find records by label and time range
     */
    List<PlatformInformation> findByLabelAndTimestampAfter(Label label, LocalDateTime after);

    /**
     * Count records by developer ID
     */
    Long countByDeveloperId(String developerId);

    /**
     * Count records by label
     */
    Long countByLabel(Label label);

    /**
     * Count records by developer ID and label within a time range
     */
    @Query("SELECT COUNT(p) FROM PlatformInformation p " +
            "WHERE p.developerId = :developerId " +
            "AND p.label = :label " +
            "AND p.timestamp >= :since")
    Long countByDeveloperAndLabelSince(
            @Param("developerId") String developerId,
            @Param("label") Label label,
            @Param("since") LocalDateTime since
    );

    /**
     * Find records by developer ID within a time range
     */
    @Query("SELECT p FROM PlatformInformation p " +
            "WHERE p.developerId = :developerId " +
            "AND p.timestamp >= :since " +
            "ORDER BY p.timestamp DESC")
    List<PlatformInformation> findByDeveloperSince(
            @Param("developerId") String developerId,
            @Param("since") LocalDateTime since
    );

    /**
     * Find records by label within a time range
     */
    @Query("SELECT p FROM PlatformInformation p " +
            "WHERE p.label = :label " +
            "AND p.timestamp >= :since " +
            "ORDER BY p.timestamp DESC")
    List<PlatformInformation> findByLabelSince(
            @Param("label") Label label,
            @Param("since") LocalDateTime since
    );
}