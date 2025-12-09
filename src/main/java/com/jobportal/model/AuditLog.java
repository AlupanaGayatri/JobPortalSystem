package com.jobportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Audit Log entity for tracking user actions
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who performed the action
     */
    @Column(nullable = false)
    private String username;

    /**
     * Action performed (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.)
     */
    @Column(nullable = false, length = 50)
    private String action;

    /**
     * Entity type affected (USER, JOB, APPLICATION, etc.)
     */
    @Column(length = 50)
    private String entityType;

    /**
     * ID of the affected entity
     */
    private Long entityId;

    /**
     * Detailed description of the action
     */
    @Column(length = 500)
    private String description;

    /**
     * IP address of the user
     */
    @Column(length = 45)
    private String ipAddress;

    /**
     * User agent (browser/device info)
     */
    @Column(length = 255)
    private String userAgent;

    /**
     * Timestamp of the action
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Status of the action (SUCCESS, FAILURE)
     */
    @Column(length = 20)
    private String status;

    /**
     * Additional details in JSON format
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
