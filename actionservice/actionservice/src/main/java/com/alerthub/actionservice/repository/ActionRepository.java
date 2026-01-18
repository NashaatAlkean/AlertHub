package com.alerthub.actionservice.repository;
import com.alerthub.actionservice.enums.RunOnDay;
import com.alerthub.actionservice.model.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActionRepository extends JpaRepository<Action, UUID> {

    // Return all actions that are not soft-deleted
    List<Action> findByDeletedFalse();

    // Return all actions that are active + enabled
    List<Action> findByDeletedFalseAndEnabledTrue();

    // Fetch single action by id only if not deleted
    Optional<Action> findByIdAndDeletedFalse(UUID id);

    // Useful for user actions screen
    List<Action> findByUserIdAndDeletedFalse(Integer userId);

    List<Action> findByUserId(Integer userId);
    // ===== SCHEDULING QUERY =====

    @Query("""
        SELECT a FROM Action a
        WHERE a.enabled = true
          AND a.deleted = false
          AND a.runOnTime = :time
          AND (a.runonday = :day OR a.runonday = 'ALL')
    """)
    List<Action> findActionsToRun(
            @Param("time") LocalTime time,
            @Param("day") RunOnDay day
    );

}
