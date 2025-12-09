package com.jobportal.dto.mapper;

import com.jobportal.dto.request.UserRegistrationRequest;
import com.jobportal.dto.request.UserUpdateRequest;
import com.jobportal.dto.response.UserResponse;
import com.jobportal.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for User entity and DTOs
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    /**
     * Convert User entity to UserResponse with profile information
     */
    public UserResponse toResponse(User user, boolean hasProfile, Integer profileCompleteness) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .hasProfile(hasProfile)
                .profileCompleteness(profileCompleteness)
                .build();
    }

    /**
     * Convert UserRegistrationRequest to User entity
     */
    public User toEntity(UserRegistrationRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        // Password will be set separately after encoding

        return user;
    }

    /**
     * Update User entity from UserUpdateRequest
     */
    public void updateEntity(User user, UserUpdateRequest request) {
        if (user == null || request == null) {
            return;
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        // Password update handled separately with encoding
    }
}
