package com.jobportal.repository;

import com.jobportal.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog entity
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by username
     */
    Page<AuditLog> findByUsername(String username, Pageable pageable);

    /**
     * Find audit logs by action
     */
    Page<AuditLog> findByAction(String action, Pageable pageable);

    /**
     * Find audit logs by entity type
     */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    /**
     * Find audit logs within date range
     */
    Page<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find audit logs by username and date range
     */
    List<AuditLog> findByUsernameAndTimestampBetween(
            String username, LocalDateTime start, LocalDateTime end);
}
