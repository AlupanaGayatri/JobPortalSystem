package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single error detail in API responses
 * Used for validation errors and business rule violations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {

    /**
     * Field name that caused the error (for validation errors)
     */
    private String field;

    /**
     * Error message
     */
    private String message;

    /**
     * Error code for programmatic handling
     */
    private String code;

    /**
     * Rejected value (optional, for debugging)
     */
    private Object rejectedValue;

    /**
     * Create a simple error detail with just a message
     */
    public static ErrorDetail of(String message) {
        return ErrorDetail.builder()
                .message(message)
                .build();
    }

    /**
     * Create an error detail for a specific field
     */
    public static ErrorDetail of(String field, String message) {
        return ErrorDetail.builder()
                .field(field)
                .message(message)
                .build();
    }

    /**
     * Create a detailed error with code
     */
    public static ErrorDetail of(String field, String message, String code) {
        return ErrorDetail.builder()
                .field(field)
                .message(message)
                .code(code)
                .build();
    }
}
