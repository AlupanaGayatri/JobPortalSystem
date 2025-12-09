package com.jobportal.repository;

import com.jobportal.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for LoginAttempt entity
 */
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    /**
     * Find failed login attempts for a user within a time window
     */
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.username = :username " +
            "AND la.successful = false AND la.attemptTime > :since")
    long countFailedAttemptsSince(@Param("username") String username,
            @Param("since") LocalDateTime since);

    /**
     * Find all login attempts for a user
     */
    List<LoginAttempt> findByUsernameOrderByAttemptTimeDesc(String username);

    /**
     * Delete old login attempts (cleanup)
     */
    void deleteByAttemptTimeBefore(LocalDateTime before);
}
