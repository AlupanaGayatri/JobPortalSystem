package com.jobportal.repository;

import com.jobportal.model.Job;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Job entity with optimized queries
 */
public interface JobRepository extends JpaRepository<Job, Long> {

        /**
         * Find jobs by status with recruiter data (optimized with EntityGraph)
         */
        /**
         * Find jobs by status
         */
        @Query("SELECT j FROM Job j WHERE j.status = :status ORDER BY j.postedDate DESC")
        Page<Job> findByStatusWithRecruiter(@Param("status") String status, Pageable pageable);

        /**
         * Find active jobs (cached for 10 minutes)
         */
        @Cacheable(value = "jobs", key = "'active'")
        @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' ORDER BY j.postedDate DESC")
        List<Job> findActiveJobs();

        /**
         * Search jobs by title
         */
        Page<Job> findByStatusAndTitleContainingIgnoreCase(String status, String title, Pageable pageable);

        /**
         * Find jobs by title or company name (case insensitive)
         */
        Page<Job> findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(String title, String companyName,
                        Pageable pageable);

        /**
         * Find jobs by title
         */
        List<Job> findByTitleContainingIgnoreCase(String title);

        /**
         * Find jobs by location
         */
        Page<Job> findByLocationContainingIgnoreCaseAndStatus(String location, String status, Pageable pageable);

        /**
         * Find job by ID with recruiter (avoid N+1)
         */
        /**
         * Find job by ID
         */
        @Override
        Optional<Job> findById(Long id);

        /**
         * Find jobs by recruiter ID
         */
        List<Job> findByRecruiterId(Long recruiterId);

        /**
         * Find jobs by recruiter ID with pagination
         */
        Page<Job> findByRecruiterId(Long recruiterId, Pageable pageable);

        /**
         * Find jobs by status
         */
        Page<Job> findByStatus(String status, Pageable pageable);

        /**
         * Advanced Search with Filters
         */
        @Query("SELECT j FROM Job j WHERE " +
                        "(:status IS NULL OR j.status = :status) AND " +
                        "(:title IS NULL OR LOWER(j.title) LIKE :title OR LOWER(j.companyName) LIKE :title) AND " +
                        "(:location IS NULL OR LOWER(j.location) LIKE :location) AND " +
                        "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
                        "(:jobType IS NULL OR j.jobType = :jobType) AND " +
                        "(:minSalary IS NULL OR j.minSalary >= :minSalary) AND " +
                        "(:maxSalary IS NULL OR j.maxSalary <= :maxSalary)")
        Page<Job> findByFilters(
                        @Param("status") String status,
                        @Param("title") String title,
                        @Param("location") String location,
                        @Param("experienceLevel") String experienceLevel,
                        @Param("jobType") String jobType,
                        @Param("minSalary") Double minSalary,
                        @Param("maxSalary") Double maxSalary,
                        Pageable pageable);
}
