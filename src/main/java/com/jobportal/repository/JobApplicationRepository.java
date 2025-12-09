package com.jobportal.repository;

import com.jobportal.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for JobApplication entity with optimized queries
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    /**
     * Find applications by user with job and recruiter data (avoid N+1)
     */
    /**
     * Find applications by user with job data (avoid N+1)
     */
    @EntityGraph(attributePaths = { "job", "user" })
    Page<JobApplication> findByUserId(Long userId, Pageable pageable);

    /**
     * Find applications by user (List version)
     */
    @EntityGraph(attributePaths = { "job", "user" })
    java.util.List<JobApplication> findByUserId(Long userId);

    /**
     * Find applications by job with user data (avoid N+1)
     */
    @EntityGraph(attributePaths = { "user", "job" })
    Page<JobApplication> findByJobId(Long jobId, Pageable pageable);

    /**
     * Find applications by job (List version)
     */
    @EntityGraph(attributePaths = { "user", "job" })
    java.util.List<JobApplication> findByJobId(Long jobId);

    /**
     * Check if user already applied to job (excluding withdrawn)
     */
    @Query("SELECT CASE WHEN COUNT(ja) > 0 THEN true ELSE false END " +
            "FROM JobApplication ja WHERE ja.user.id = :userId AND ja.job.id = :jobId AND ja.status != 'WITHDRAWN'")
    boolean existsByUserIdAndJobId(@Param("userId") Long userId, @Param("jobId") Long jobId);

    /**
     * Delete all applications for a user
     */
    void deleteByUserId(Long userId);

    /**
     * Find application by user and job
     */
    Optional<JobApplication> findByUserIdAndJobId(Long userId, Long jobId);

    // Find by status
    Page<JobApplication> findByStatus(String status, Pageable pageable);

    /**
     * Count applications for jobs posted by a specific recruiter
     */
    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.recruiterId = :recruiterId")
    long countByJobRecruiterId(@Param("recruiterId") Long recruiterId);
}
