package com.alerthub.repository;

import com.alerthub.entity.PlatformInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlatformInformationRepository extends JpaRepository<PlatformInformation, Long> {

    /**
     * Find developer with most occurrences of a specific label within a time frame
     */
    @Query("SELECT p.developerId, COUNT(p) as count FROM PlatformInformation p " +
            "WHERE p.label = :label AND p.timestamp >= :since " +
            "GROUP BY p.developerId ORDER BY count DESC LIMIT 1")
    List<Object[]> findDeveloperWithMostLabel(@Param("label") String label,
                                              @Param("since") LocalDateTime since);

    /**
     * Count tasks by label for a specific developer within a time frame
     */
    @Query("SELECT p.label, COUNT(p) FROM PlatformInformation p " +
            "WHERE p.developerId = :developerId AND p.timestamp >= :since " +
            "GROUP BY p.label")
    List<Object[]> findLabelAggregateByDeveloper(@Param("developerId") String developerId,
                                                 @Param("since") LocalDateTime since);

    /**
     * Count total tasks for a specific developer within a time frame
     */
    @Query("SELECT COUNT(p) FROM PlatformInformation p " +
            "WHERE p.developerId = :developerId AND p.timestamp >= :since")
    Long countTasksByDeveloper(@Param("developerId") String developerId,
                               @Param("since") LocalDateTime since);

    /**
     * Find all tasks for a developer within a time frame
     */
    List<PlatformInformation> findByDeveloperIdAndTimestampAfter(String developerId,
                                                                 LocalDateTime since);

    /**
     * Find all tasks with a specific label within a time frame
     */
    List<PlatformInformation> findByLabelAndTimestampAfter(String label, LocalDateTime since);
}