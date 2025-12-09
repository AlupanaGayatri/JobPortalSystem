package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for job application responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobApplicationResponse {

    private Long id;
    private String coverLetter;
    private String expectedSalary;
    private String availability;
    private String status;
    private LocalDateTime appliedAt;

    /**
     * Job information (embedded)
     */
    private JobResponse job;

    /**
     * Applicant information (embedded)
     */
    private UserResponse applicant;

    /**
     * Profile information (for recruiter view)
     */
    private ProfileResponse applicantProfile;
}
