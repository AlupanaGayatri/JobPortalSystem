package com.jobportal.dto.mapper;

import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.model.Job;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapper for Job entity and DTOs
 */
@Component
public class JobMapper {

    /**
     * Convert Job entity to JobResponse DTO
     */
    public JobResponse toResponse(Job job) {
        if (job == null) {
            return null;
        }

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .description(job.getDescription())
                .skillsRequired(job.getSkillsRequired())
                .postedDate(job.getPostedDate())
                .status(job.getStatus())
                .recruiterId(job.getRecruiterId())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .experienceLevel(job.getExperienceLevel())
                .jobType(job.getJobType())
                .build();
    }

    /**
     * Convert Job entity to JobResponse with additional information
     */
    public JobResponse toResponse(Job job, String recruiterName, Integer applicationCount, Boolean hasApplied) {
        if (job == null) {
            return null;
        }

        JobResponse response = toResponse(job);
        response.setRecruiterName(recruiterName);
        response.setApplicationCount(applicationCount);
        response.setHasApplied(hasApplied);

        return response;
    }

    /**
     * Convert JobRequest to Job entity
     */
    public Job toEntity(JobRequest request) {
        if (request == null) {
            return null;
        }

        Job job = new Job();
        updateEntity(job, request);
        job.setPostedDate(LocalDate.now());
        job.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        return job;
    }

    /**
     * Update Job entity from JobRequest
     */
    public void updateEntity(Job job, JobRequest request) {
        if (job == null || request == null) {
            return;
        }

        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setDescription(request.getDescription());
        job.setSkillsRequired(request.getSkillsRequired());

        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }
    }
}
