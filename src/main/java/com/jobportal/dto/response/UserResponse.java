package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user responses
 * Excludes sensitive information like passwords
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String role;

    /**
     * Indicates if user has completed their profile
     */
    private Boolean hasProfile;

    /**
     * Profile completion percentage (0-100)
     */
    private Integer profileCompleteness;
}
