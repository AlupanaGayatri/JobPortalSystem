package com.jobportal.controller.api;

import com.jobportal.dto.mapper.UserMapper;
import com.jobportal.dto.request.AuthenticationRequest;
import com.jobportal.dto.request.UserRegistrationRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.AuthenticationResponse;
import com.jobportal.dto.response.UserResponse;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.InvalidRequestException;
import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.jwt.JwtTokenProvider;
import com.jobportal.service.AuditLogService;
import com.jobportal.service.LoginAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller for Authentication
 * Handles login, registration, token refresh with security features
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthenticationController {

        private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtTokenProvider tokenProvider;

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private LoginAttemptService loginAttemptService;

        @Autowired
        private AuditLogService auditLogService;

        @Value("${jwt.expiration:86400000}")
        private long jwtExpirationMs;

        /**
         * Authenticate user and return JWT tokens
         * POST /api/v1/auth/login
         */
        @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT access and refresh tokens")
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
                        @Valid @RequestBody AuthenticationRequest request) {

                logger.info("Login attempt for user: {}", request.getEmail());

                // Check if account is locked due to failed attempts
                loginAttemptService.checkAccountLockout(request.getEmail());

                try {
                        // Authenticate user
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Get user details
                        User user = userRepository.findByEmail(request.getEmail())
                                        .orElseThrow(() -> new InvalidRequestException("User not found"));

                        // Generate tokens
                        String accessToken = tokenProvider.generateAccessToken(authentication);
                        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

                        // Build response
                        UserResponse userResponse = userMapper.toResponse(user);
                        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .expiresIn(jwtExpirationMs)
                                        .user(userResponse)
                                        .build();

                        // Record successful login
                        loginAttemptService.recordLoginAttempt(request.getEmail(), true);
                        auditLogService.logAction("LOGIN", "USER", user.getId(),
                                        "User logged in successfully");

                        logger.info("User logged in successfully: {}", request.getEmail());

                        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));

                } catch (Exception e) {
                        // Record failed login attempt
                        loginAttemptService.recordLoginAttempt(request.getEmail(), false);
                        auditLogService.logFailedAction("LOGIN", "USER",
                                        "Failed login attempt for: " + request.getEmail(), e.getMessage());
                        throw e;
                }
        }

        /**
         * Register new user
         * POST /api/v1/auth/register
         */
        @PostMapping("/register")
        public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
                        @Valid @RequestBody UserRegistrationRequest request) {

                logger.info("Registration attempt for email: {}", request.getEmail());

                // Check if user already exists
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new DuplicateResourceException("User", "email", request.getEmail());
                }

                // Create new user
                User user = userMapper.toEntity(request);
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                // Set default role if not provided
                if (user.getRole() == null || user.getRole().isEmpty()) {
                        user.setRole("USER");
                }

                User savedUser = userRepository.save(user);
                logger.info("User registered successfully: {}", savedUser.getEmail());

                // Log registration
                auditLogService.logAction("REGISTER", "USER", savedUser.getId(),
                                "New user registered: " + savedUser.getEmail());

                // Generate tokens
                String accessToken = tokenProvider.generateAccessToken(
                                savedUser.getEmail(),
                                "ROLE_" + savedUser.getRole());
                String refreshToken = tokenProvider.generateRefreshToken(savedUser.getEmail());

                // Build response
                UserResponse userResponse = userMapper.toResponse(savedUser);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .expiresIn(jwtExpirationMs)
                                .user(userResponse)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Registration successful", authResponse));
        }

        /**
         * Refresh access token using refresh token
         * POST /api/v1/auth/refresh
         */
        @PostMapping("/refresh")
        public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
                        @RequestHeader("Authorization") String authHeader) {

                logger.info("Token refresh attempt");

                // Extract refresh token
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new InvalidRequestException("Invalid authorization header");
                }

                String refreshToken = authHeader.substring(7);

                // Validate refresh token
                if (!tokenProvider.validateToken(refreshToken)) {
                        throw new InvalidRequestException("Invalid or expired refresh token");
                }

                if (!tokenProvider.isRefreshToken(refreshToken)) {
                        throw new InvalidRequestException("Token is not a refresh token");
                }

                // Get user from token
                String email = tokenProvider.getUsernameFromToken(refreshToken);
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new InvalidRequestException("User not found"));

                // Generate new access token
                String newAccessToken = tokenProvider.generateAccessToken(
                                user.getEmail(),
                                "ROLE_" + user.getRole());

                // Build response
                UserResponse userResponse = userMapper.toResponse(user);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken) // Return same refresh token
                                .expiresIn(jwtExpirationMs)
                                .user(userResponse)
                                .build();

                logger.info("Token refreshed successfully for user: {}", email);

                return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
        }

        /**
         * Get current authenticated user
         * GET /api/v1/auth/me
         */
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new InvalidRequestException("User not found"));

                UserResponse userResponse = userMapper.toResponse(user);
                return ResponseEntity.ok(ApiResponse.success(userResponse));
        }
}
