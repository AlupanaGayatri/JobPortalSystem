package com.jobportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for tracking login attempts and account lockout
 */
@Entity
@Table(name = "login_attempts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email/username of the user
     */
    @Column(nullable = false)
    private String username;

    /**
     * IP address of the attempt
     */
    @Column(length = 45)
    private String ipAddress;

    /**
     * Timestamp of the attempt
     */
    @Column(nullable = false)
    private LocalDateTime attemptTime;

    /**
     * Whether the attempt was successful
     */
    @Column(nullable = false)
    private Boolean successful;

    @PrePersist
    protected void onCreate() {
        attemptTime = LocalDateTime.now();
    }
}
