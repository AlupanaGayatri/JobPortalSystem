package com.jobportal.controller.api;

import com.jobportal.dto.mapper.JobApplicationMapper;
import com.jobportal.dto.request.JobApplicationRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobApplicationResponse;
import com.jobportal.dto.response.PageMetadata;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.InvalidRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST API Controller for Job Application management
 */
@RestController
@RequestMapping("/api/v1/applications")
public class JobApplicationApiController {

        private static final Logger logger = LoggerFactory.getLogger(JobApplicationApiController.class);

        @Autowired
        private JobApplicationRepository applicationRepository;

        @Autowired
        private JobRepository jobRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JobApplicationMapper applicationMapper;

        /**
         * Get all applications (admin only)
         * GET /api/v1/applications?page=0&size=10
         */
        @GetMapping
        public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getAllApplications(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                logger.info("Fetching all applications - page: {}, size: {}", page, size);

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedDate"));
                Page<JobApplication> applicationPage = applicationRepository.findAll(pageable);

                List<JobApplicationResponse> responses = applicationPage.getContent().stream()
                                .map(applicationMapper::toResponse)
                                .collect(Collectors.toList());

                PageMetadata pageMetadata = PageMetadata.from(applicationPage);

                return ResponseEntity.ok(ApiResponse.success(responses, pageMetadata));
        }

        /**
         * Get current user's applications
         * GET /api/v1/applications/my
         */
        @GetMapping("/my")
        public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                logger.info("Fetching current user's applications");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedDate"));
                Page<JobApplication> applicationPage = applicationRepository.findByUserId(user.getId(), pageable);

                List<JobApplicationResponse> responses = applicationPage.getContent().stream()
                                .map(applicationMapper::toResponse)
                                .collect(Collectors.toList());

                PageMetadata pageMetadata = PageMetadata.from(applicationPage);

                return ResponseEntity.ok(ApiResponse.success(responses, pageMetadata));
        }

        /**
         * Get applications for a specific job (recruiter/admin only)
         * GET /api/v1/applications/job/{jobId}
         */
        @GetMapping("/job/{jobId}")
        public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getApplicationsByJob(
                        @PathVariable Long jobId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                logger.info("Fetching applications for job ID: {}", jobId);

                // Verify job exists
                jobRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedDate"));
                Page<JobApplication> applicationPage = applicationRepository.findByJobId(jobId, pageable);

                List<JobApplicationResponse> responses = applicationPage.getContent().stream()
                                .map(applicationMapper::toResponse)
                                .collect(Collectors.toList());

                PageMetadata pageMetadata = PageMetadata.from(applicationPage);

                return ResponseEntity.ok(ApiResponse.success(responses, pageMetadata));
        }

        /**
         * Get application by ID
         * GET /api/v1/applications/{id}
         */
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<JobApplicationResponse>> getApplicationById(@PathVariable Long id) {
                logger.info("Fetching application with ID: {}", id);

                JobApplication application = applicationRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", id));

                JobApplicationResponse response = applicationMapper.toResponse(application);
                return ResponseEntity.ok(ApiResponse.success(response));
        }

        /**
         * Submit job application
         * POST /api/v1/applications
         */
        @PostMapping
        public ResponseEntity<ApiResponse<JobApplicationResponse>> submitApplication(
                        @Valid @RequestBody JobApplicationRequest request) {

                logger.info("Submitting application for job ID: {}", request.getJobId());

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                Job job = jobRepository.findById(request.getJobId())
                                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", request.getJobId()));

                // Check if job is active
                if (!"ACTIVE".equals(job.getStatus())) {
                        throw new InvalidRequestException("Cannot apply to a closed job");
                }

                // Check if user already applied
                Optional<JobApplication> existingApplication = applicationRepository.findByUserIdAndJobId(user.getId(),
                                job.getId());

                if (existingApplication.isPresent()) {
                        throw new DuplicateResourceException("You have already applied to this job");
                }

                // Create application
                JobApplication application = applicationMapper.toEntity(request);
                application.setUser(user);
                application.setJob(job);

                JobApplication savedApplication = applicationRepository.save(application);
                logger.info("Application submitted successfully with ID: {}", savedApplication.getId());

                JobApplicationResponse response = applicationMapper.toResponse(savedApplication);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.<JobApplicationResponse>success("Application submitted successfully",
                                                response));
        }

        /**
         * Update application status (recruiter/admin only)
         * PATCH /api/v1/applications/{id}/status
         */
        @PatchMapping("/{id}/status")
        public ResponseEntity<ApiResponse<JobApplicationResponse>> updateApplicationStatus(
                        @PathVariable Long id,
                        @RequestParam String status) {

                logger.info("Updating application {} status to: {}", id, status);

                JobApplication application = applicationRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", id));

                // Validate status
                if (!List.of("PENDING", "APPROVED", "REJECTED", "WITHDRAWN").contains(status)) {
                        throw new InvalidRequestException("Invalid status: " + status);
                }

                application.setStatus(status);
                JobApplication updatedApplication = applicationRepository.save(application);

                logger.info("Application status updated successfully");

                JobApplicationResponse response = applicationMapper.toResponse(updatedApplication);
                return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
        }

        /**
         * Withdraw application (user only)
         * DELETE /api/v1/applications/{id}
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> withdrawApplication(@PathVariable Long id) {
                logger.info("Withdrawing application with ID: {}", id);

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                JobApplication application = applicationRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", id));

                // Verify ownership
                if (!application.getUser().getId().equals(user.getId())) {
                        throw new InvalidRequestException("You can only withdraw your own applications");
                }

                application.setStatus("WITHDRAWN");
                applicationRepository.save(application);

                logger.info("Application withdrawn successfully");

                return ResponseEntity.ok(ApiResponse.<Void>success("Application withdrawn successfully", null));
        }
}
