package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for job responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {

    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String salary;
    private String description;
    private String skillsRequired;
    private LocalDate postedDate;
    private String status;
    private Double minSalary;
    private Double maxSalary;
    private String experienceLevel;
    private String jobType;

    /**
     * Recruiter who posted the job
     */
    private Long recruiterId;
    private String recruiterName;

    /**
     * Number of applications received
     */
    private Integer applicationCount;

    /**
     * Whether current user has applied (for authenticated users)
     */
    private Boolean hasApplied;
}
