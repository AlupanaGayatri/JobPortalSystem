package com.jobportal.controller.api;

import com.jobportal.dto.mapper.UserMapper;
import com.jobportal.dto.request.UserRegistrationRequest;
import com.jobportal.dto.request.UserUpdateRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.UserResponse;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for User management
 * Demonstrates enterprise-grade API design with DTOs
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {

    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Get all users
     * GET /api/v1/users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        logger.info("Fetching all users");

        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(userResponses));
    }

    /**
     * Get user by ID
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        logger.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Create new user
     * POST /api/v1/users
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        logger.info("Creating new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Convert DTO to entity
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        UserResponse response = userMapper.toResponse(savedUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    /**
     * Update user
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        logger.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update entity from DTO
        userMapper.updateEntity(user, request);

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());

        UserResponse response = userMapper.toResponse(updatedUser);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    /**
     * Delete user
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);
        logger.info("User deleted successfully with ID: {}", id);

        return ResponseEntity.ok(ApiResponse.<Void>success("User deleted successfully", null));
    }

    /**
     * Get user by email
     * GET /api/v1/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        logger.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
