package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for job application submission
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotBlank(message = "Cover letter is required")
    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;

    /**
     * Expected salary (optional)
     */
    private String expectedSalary;

    /**
     * Availability to join (optional)
     */
    private String availability;
}
