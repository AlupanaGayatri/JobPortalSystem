package com.jobportal.dto.mapper;

import com.jobportal.dto.request.JobApplicationRequest;
import com.jobportal.dto.response.JobApplicationResponse;
import com.jobportal.model.JobApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper for JobApplication entity and DTOs
 */
@Component
public class JobApplicationMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private ProfileMapper profileMapper;

    /**
     * Convert JobApplication entity to JobApplicationResponse DTO
     */
    public JobApplicationResponse toResponse(JobApplication application) {
        if (application == null) {
            return null;
        }

        JobApplicationResponse response = JobApplicationResponse.builder()
                .id(application.getId())
                .coverLetter(application.getResumeText())
                .status(application.getStatus())
                .appliedAt(application.getAppliedDate())
                .build();

        // Add job information if available
        if (application.getJob() != null) {
            response.setJob(jobMapper.toResponse(application.getJob()));
        }

        // Add applicant information if available
        if (application.getUser() != null) {
            response.setApplicant(userMapper.toResponse(application.getUser()));
        }

        return response;
    }

    /**
     * Convert JobApplication entity to JobApplicationResponse with profile
     */
    public JobApplicationResponse toResponseWithProfile(JobApplication application) {
        if (application == null) {
            return null;
        }

        JobApplicationResponse response = toResponse(application);

        // Add profile information if available (for recruiter view)
        if (application.getUser() != null && application.getUser().getId() != null) {
            // Profile will be loaded separately by service layer
        }

        return response;
    }

    /**
     * Convert JobApplicationRequest to JobApplication entity
     */
    public JobApplication toEntity(JobApplicationRequest request) {
        if (request == null) {
            return null;
        }

        JobApplication application = new JobApplication();
        application.setResumeText(request.getCoverLetter());
        application.setStatus("PENDING");
        application.setAppliedDate(java.time.LocalDateTime.now());

        return application;
    }
}
