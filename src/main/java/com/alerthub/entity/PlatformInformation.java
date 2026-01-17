package com.alerthub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "project")
    private String project;

    @Column(name = "tag")
    private String tag;

    @Column(name = "label")
    private String label;

    @Column(name = "developer_id")
    private String developerId;

    @Column(name = "task_number")
    private String taskNumber;

    @Column(name = "environment")
    private String environment;

    @Column(name = "user_story")
    private String userStory;

    @Column(name = "task_point")
    private Integer taskPoint;

    @Column(name = "sprint")
    private String sprint;
}