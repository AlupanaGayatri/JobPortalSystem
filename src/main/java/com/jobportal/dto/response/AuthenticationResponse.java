package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses containing JWT tokens
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    /**
     * JWT access token
     */
    private String accessToken;

    /**
     * JWT refresh token
     */
    private String refreshToken;

    /**
     * Token type (always "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access token expiration time in milliseconds
     */
    private Long expiresIn;

    /**
     * User information
     */
    private UserResponse user;
}
