package com.jobportal.controller.api;

import com.jobportal.dto.mapper.JobMapper;
import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PageMetadata;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.model.Job;
import com.jobportal.model.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Job management
 * Supports pagination, filtering, and search
 */
@RestController
@RequestMapping("/api/v1/jobs")
public class JobApiController {

        private static final Logger logger = LoggerFactory.getLogger(JobApiController.class);

        @Autowired
        private JobRepository jobRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JobMapper jobMapper;

        @Autowired
        private com.jobportal.service.JobService jobService;

        /**
         * Get all jobs with pagination and filters
         * GET /api/v1/jobs?page=0&size=10&sort=postedDate,desc&title=developer
         */
        @GetMapping
        public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "postedDate,desc") String[] sort,
                        @RequestParam(required = false) String title,
                        @RequestParam(required = false) String location,
                        @RequestParam(required = false) String experienceLevel,
                        @RequestParam(required = false) String jobType,
                        @RequestParam(required = false) Double minSalary,
                        @RequestParam(required = false) Double maxSalary,
                        @RequestParam(required = false) String status) {

                logger.info("Fetching jobs - page: {}, size: {}, filters: [title={}, loc={}]", page, size, title,
                                location);

                // Parse sort parameters
                Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC;
                String sortField = sort[0];

                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

                Page<Job> jobPage = jobService.searchJobs(status, title, location, experienceLevel, jobType, minSalary,
                                maxSalary, pageable);

                List<JobResponse> jobResponses = jobPage.getContent().stream()
                                .map(jobMapper::toResponse)
                                .collect(Collectors.toList());

                PageMetadata pageMetadata = PageMetadata.from(jobPage);

                return ResponseEntity.ok(ApiResponse.success(jobResponses, pageMetadata));
        }

        /**
         * Get job by ID
         * GET /api/v1/jobs/{id}
         */
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
                logger.info("Fetching job with ID: {}", id);

                Job job = jobRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

                JobResponse response = jobMapper.toResponse(job);
                return ResponseEntity.ok(ApiResponse.success(response));
        }

        /**
         * Create new job (RECRUITER/ADMIN only)
         * POST /api/v1/jobs
         */
        @PostMapping
        public ResponseEntity<ApiResponse<JobResponse>> createJob(
                        @Valid @RequestBody JobRequest request) {

                logger.info("Creating new job: {}", request.getTitle());

                // Get current authenticated user
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User recruiter = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                // Check if user is RECRUITER or ADMIN
                if (!recruiter.getRole().equals("RECRUITER") && !recruiter.getRole().equals("ADMIN")) {
                        throw new UnauthorizedException("Only recruiters and admins can create jobs");
                }

                // Convert DTO to entity
                Job job = jobMapper.toEntity(request);
                job.setRecruiterId(recruiter.getId());
                job.setPostedDate(LocalDate.now());

                // Save job
                Job savedJob = jobRepository.save(job);
                logger.info("Job created successfully with ID: {}", savedJob.getId());

                JobResponse response = jobMapper.toResponse(savedJob);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Job created successfully", response));
        }

        /**
         * Update job (RECRUITER/ADMIN only)
         * PUT /api/v1/jobs/{id}
         */
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<JobResponse>> updateJob(
                        @PathVariable Long id,
                        @Valid @RequestBody JobRequest request) {

                logger.info("Updating job with ID: {}", id);

                Job job = jobRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

                // Update entity from DTO
                jobMapper.updateEntity(job, request);

                Job updatedJob = jobRepository.save(job);
                logger.info("Job updated successfully with ID: {}", updatedJob.getId());

                JobResponse response = jobMapper.toResponse(updatedJob);
                return ResponseEntity.ok(ApiResponse.success("Job updated successfully", response));
        }

        /**
         * Delete job (RECRUITER/ADMIN only)
         * DELETE /api/v1/jobs/{id}
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
                logger.info("Deleting job with ID: {}", id);

                Job job = jobRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

                jobRepository.delete(job);
                logger.info("Job deleted successfully with ID: {}", id);

                return ResponseEntity.ok(ApiResponse.<Void>success("Job deleted successfully", null));
        }

        /**
         * Search jobs by title or company
         * GET /api/v1/jobs/search?query=developer
         */
        @GetMapping("/search")
        public ResponseEntity<ApiResponse<List<JobResponse>>> searchJobs(
                        @RequestParam String query,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                logger.info("Searching jobs with query: {}", query);

                Pageable pageable = PageRequest.of(page, size);
                Page<Job> jobPage = jobRepository.findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(
                                query, query, pageable);

                List<JobResponse> jobResponses = jobPage.getContent().stream()
                                .map(jobMapper::toResponse)
                                .collect(Collectors.toList());

                PageMetadata pageMetadata = PageMetadata.from(jobPage);

                return ResponseEntity.ok(ApiResponse.success(jobResponses, pageMetadata));
        }

        /**
         * Get active jobs only
         * GET /api/v1/jobs/active
         */
        @GetMapping("/active")
        public ResponseEntity<ApiResponse<List<JobResponse>>> getActiveJobs(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                logger.info("Fetching active jobs");

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
                Page<Job> jobPage = jobRepository.findByStatus("ACTIVE", pageable);

                List<JobResponse> jobResponses = jobPage.getContent().stream()
                                .map(jobMapper::toResponse)
                                .collect(Collectors.toList());

                PageMetadata pageMetadata = PageMetadata.from(jobPage);

                return ResponseEntity.ok(ApiResponse.success(jobResponses, pageMetadata));
        }
}
