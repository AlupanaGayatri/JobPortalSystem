package com.jobportal.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Check for redirect parameter in session or request
        String redirectUrl = request.getParameter("redirect");

        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        // Default behavior
        setDefaultTargetUrl("/dashboard");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
