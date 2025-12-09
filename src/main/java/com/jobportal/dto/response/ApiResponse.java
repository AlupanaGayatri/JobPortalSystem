package com.jobportal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized API Response wrapper for all REST endpoints
 * Provides consistent response structure across the application
 * 
 * @param <T> The type of data being returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates if the operation was successful
     */
    private boolean success;

    /**
     * Human-readable message about the operation result
     */
    private String message;

    /**
     * The actual data payload (null if operation failed)
     */
    private T data;

    /**
     * Timestamp when the response was generated
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * List of validation or business errors (only present on failure)
     */
    private List<ErrorDetail> errors;

    /**
     * Pagination metadata (only present for paginated responses)
     */
    private PageMetadata pagination;

    // Convenience factory methods

    /**
     * Create a successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(data)
                .build();
    }

    /**
     * Create a successful response with custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a failure response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Create a failure response with errors
     */
    public static <T> ApiResponse<T> error(String message, List<ErrorDetail> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * Create a paginated success response
     */
    public static <T> ApiResponse<T> success(T data, PageMetadata pagination) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(data)
                .pagination(pagination)
                .build();
    }
}
