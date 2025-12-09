package com.jobportal.service;

import com.jobportal.exception.InvalidRequestException;
import com.jobportal.model.LoginAttempt;
import com.jobportal.repository.LoginAttemptRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Service for managing login attempts and account lockout
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    /**
     * Record a login attempt
     */
    public void recordLoginAttempt(String username, boolean successful) {
        String ipAddress = getClientIP();

        LoginAttempt attempt = LoginAttempt.builder()
                .username(username)
                .ipAddress(ipAddress)
                .successful(successful)
                .build();

        loginAttemptRepository.save(attempt);
    }

    /**
     * Check if account is locked due to failed login attempts
     */
    public void checkAccountLockout(String username) {
        LocalDateTime lockoutThreshold = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsSince(username, lockoutThreshold);

        if (failedAttempts >= MAX_ATTEMPTS) {
            throw new InvalidRequestException(
                    String.format("Account is temporarily locked due to too many failed login attempts. " +
                            "Please try again after %d minutes.", LOCKOUT_DURATION_MINUTES));
        }
    }

    /**
     * Reset login attempts for a user (after successful login)
     */
    public void resetLoginAttempts(String username) {
        // Attempts are automatically cleared after the lockout duration
        // No need to delete them explicitly
    }

    /**
     * Get client IP address
     */
    private String getClientIP() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader == null) {
                return request.getRemoteAddr();
            }
            return xfHeader.split(",")[0];
        }

        return "unknown";
    }
}
