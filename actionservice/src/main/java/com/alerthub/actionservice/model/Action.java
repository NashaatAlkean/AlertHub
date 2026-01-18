package com.alerthub.actionservice.model;

import com.alerthub.actionservice.enums.ActionType;
import com.alerthub.actionservice.enums.RunOnDay;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "actions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    /**
     * Condition matrix as JSON string.
     * Example: [[1,2],[3]]
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String condition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    /**
     * Email address or phone number
     */
    @Column(name = "to", nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String message;

    /**
     * Run time (only full or half hour allowed)
     */
    @Column(name = "run_on_time", nullable = false)
    private LocalTime runOnTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "run_on_day", nullable = false)
    private RunOnDay runonday;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    @Column
    private LocalDateTime lastRun;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.lastUpdate = this.createDate;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }
}
