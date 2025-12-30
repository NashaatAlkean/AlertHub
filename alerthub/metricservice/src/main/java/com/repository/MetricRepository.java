package com.alerthub.metric.repository;

import com.alerthub.metric.enums.LabelType;
import com.alerthub.metric.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetricRepository extends JpaRepository<Metric, UUID> {

    // Find all metrics by user ID
    List<Metric> findByUserId(Integer userId);

    // Find metrics by label type
    List<Metric> findByLabel(LabelType label);

    // Find metric by name
    Optional<Metric> findByName(String name);

    // Find metrics by user and label
    List<Metric> findByUserIdAndLabel(Integer userId, LabelType label);

    // Check if metric name already exists for a user
    boolean existsByUserIdAndName(Integer userId, String name);

    // Custom query example: Find metrics with threshold above certain value
    @Query("SELECT m FROM Metric m WHERE m.threshold >= :threshold")
    List<Metric> findMetricsWithHighThreshold(@Param("threshold") Integer threshold);

    // Find metrics within specific time frame range
    @Query("SELECT m FROM Metric m WHERE m.timeFrameHours BETWEEN :minHours AND :maxHours")
    List<Metric> findByTimeFrameRange(
            @Param("minHours") Integer minHours,
            @Param("maxHours") Integer maxHours
    );
}