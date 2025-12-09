package com.jobportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for API versioning
 * Supports versioned API endpoints (e.g., /api/v1/users)
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    /**
     * Configure path matching for API versioning
     * This allows us to have multiple versions of the same endpoint
     */
    // @Override
    // public void configurePathMatching(PathMatchConfigurer configurer) {
    // // Enable trailing slash matching
    // configurer.setUseTrailingSlashMatch(true);
    // }
}
