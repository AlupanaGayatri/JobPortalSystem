package com.jobportal.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point
 * Handles authentication errors and returns JSON responses
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        String path = request.getServletPath();

        // If it's an API request or an AJAX request, return JSON 401
        if (path.startsWith("/api/") || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            logger.error("Unauthorized error: {}", authException.getMessage());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", "Unauthorized: " + authException.getMessage());
            body.put("error", "Authentication Failed");
            body.put("path", path);
            body.put("timestamp", System.currentTimeMillis());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        } else {
            // For regular web pages, redirect to login
            response.sendRedirect("/login");
        }
    }
}
