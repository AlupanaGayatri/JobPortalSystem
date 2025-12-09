package com.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for profile creation and update requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Headline is required")
    @Size(max = 200, message = "Headline must not exceed 200 characters")
    private String headline;

    @Size(max = 2000, message = "Summary must not exceed 2000 characters")
    private String summary;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Pattern(regexp = "^(https?://)?(www\\.)?github\\.com/[a-zA-Z0-9_-]+/?$", message = "Invalid GitHub URL format", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String githubUrl;

    @Pattern(regexp = "^(https?://)?(www\\.)?linkedin\\.com/in/[a-zA-Z0-9_-]+/?$", message = "Invalid LinkedIn URL format", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String linkedinUrl;

    private Integer experience;

    @Size(max = 100, message = "Current role must not exceed 100 characters")
    private String currentRole;
}
