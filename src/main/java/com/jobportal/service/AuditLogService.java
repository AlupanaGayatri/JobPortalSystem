package com.jobportal.service;

import com.jobportal.model.AuditLog;
import com.jobportal.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service for creating audit log entries
 */
@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Log user action asynchronously
     */
    @Async
    public void logAction(String action, String entityType, Long entityId, String description) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "anonymous";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            String ipAddress = null;
            String userAgent = null;

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = getClientIP(request);
                userAgent = request.getHeader("User-Agent");
            }

            AuditLog auditLog = AuditLog.builder()
                    .username(username)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .status("SUCCESS")
                    .build();

            auditLogRepository.save(auditLog);
            logger.debug("Audit log created: {} - {}", action, description);

        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
        }
    }

    /**
     * Log failed action
     */
    @Async
    public void logFailedAction(String action, String entityType, String description, String errorDetails) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "anonymous";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            String ipAddress = null;
            String userAgent = null;

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = getClientIP(request);
                userAgent = request.getHeader("User-Agent");
            }

            AuditLog auditLog = AuditLog.builder()
                    .username(username)
                    .action(action)
                    .entityType(entityType)
                    .description(description)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .status("FAILURE")
                    .details(errorDetails)
                    .build();

            auditLogRepository.save(auditLog);
            logger.debug("Failed action logged: {} - {}", action, description);

        } catch (Exception e) {
            logger.error("Failed to create audit log for failed action", e);
        }
    }

    /**
     * Extract client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
