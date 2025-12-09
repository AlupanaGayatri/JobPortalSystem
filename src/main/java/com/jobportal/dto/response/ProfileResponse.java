package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for profile responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {

    private Long id;
    private String headline;
    private String summary;
    private String phone;
    private String address;
    private String githubUrl;
    private String linkedinUrl;
    private String profilePhoto;
    private String resumeFile;
    private Integer experience;
    private Integer skillsCount;
    private String currentRole;

    /**
     * User information (embedded)
     */
    private UserResponse user;

    /**
     * Profile completion percentage
     */
    private Integer completeness;
}
