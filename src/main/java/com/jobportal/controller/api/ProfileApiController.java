package com.jobportal.controller.api;

import com.jobportal.dto.mapper.ProfileMapper;
import com.jobportal.dto.request.ProfileRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ProfileResponse;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.Profile;
import com.jobportal.model.User;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller for Profile management
 */
@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileApiController {

        private static final Logger logger = LoggerFactory.getLogger(ProfileApiController.class);

        @Autowired
        private ProfileRepository profileRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProfileMapper profileMapper;

        /**
         * Get current user's profile
         * GET /api/v1/profiles/me
         */
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile() {
                logger.info("Fetching current user's profile");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                Profile profile = profileRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profile not found for user: " + email));

                ProfileResponse response = profileMapper.toResponse(profile);
                return ResponseEntity.ok(ApiResponse.success(response));
        }

        /**
         * Get profile by user ID
         * GET /api/v1/profiles/user/{userId}
         */
        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByUserId(@PathVariable Long userId) {
                logger.info("Fetching profile for user ID: {}", userId);

                Profile profile = profileRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

                ProfileResponse response = profileMapper.toResponse(profile);
                return ResponseEntity.ok(ApiResponse.success(response));
        }

        /**
         * Create or update current user's profile
         * POST /api/v1/profiles
         */
        @PostMapping
        public ResponseEntity<ApiResponse<ProfileResponse>> createOrUpdateProfile(
                        @Valid @RequestBody ProfileRequest request) {

                logger.info("Creating/updating profile");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = auth.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                // Check if profile already exists
                Profile profile = profileRepository.findByUserId(user.getId())
                                .orElse(new Profile());

                boolean isNew = profile.getId() == null;

                // Update profile from request
                profileMapper.updateEntity(profile, request);
                profile.setUser(user);

                Profile savedProfile = profileRepository.save(profile);
                logger.info("Profile {} successfully for user: {}", isNew ? "created" : "updated", email);

                ProfileResponse response = profileMapper.toResponse(savedProfile);
                String message = isNew ? "Profile created successfully" : "Profile updated successfully";

                return ResponseEntity
                                .status(isNew ? HttpStatus.CREATED : HttpStatus.OK)
                                .body(ApiResponse.<ProfileResponse>success(message, response));
        }

        /**
         * Update profile by ID (admin only)
         * PUT /api/v1/profiles/{id}
         */
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
                        @PathVariable Long id,
                        @Valid @RequestBody ProfileRequest request) {

                logger.info("Updating profile with ID: {}", id);

                Profile profile = profileRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));

                profileMapper.updateEntity(profile, request);

                Profile updatedProfile = profileRepository.save(profile);
                logger.info("Profile updated successfully with ID: {}", updatedProfile.getId());

                ProfileResponse response = profileMapper.toResponse(updatedProfile);
                return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
        }

        /**
         * Delete profile
         * DELETE /api/v1/profiles/{id}
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable Long id) {
                logger.info("Deleting profile with ID: {}", id);

                Profile profile = profileRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));

                profileRepository.delete(profile);
                logger.info("Profile deleted successfully with ID: {}", id);

                return ResponseEntity.ok(ApiResponse.<Void>success("Profile deleted successfully", null));
        }
}
