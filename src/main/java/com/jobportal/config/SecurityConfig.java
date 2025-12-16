package com.jobportal.config;

import com.jobportal.security.CustomUserDetailsService;
import com.jobportal.security.jwt.JwtAuthenticationEntryPoint;
import com.jobportal.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security Configuration with JWT support
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Autowired
        private com.jobportal.security.CustomOidcUserService customOidcUserService;

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        private CorsConfigurationSource corsConfigurationSource;

        @Autowired
        private com.jobportal.security.CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                // Enable CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                                // Disable CSRF for stateless JWT authentication
                                .csrf(csrf -> csrf.disable())

                                // Configure exception handling
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                                // Set session management to allow sessions for web UI
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                                // Configure authorization
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                .requestMatchers("/register", "/login", "/css/**", "/js/**",
                                                                "/images/**", "/oauth2/**", "/cleanup/**",
                                                                "/jobs", "/api/jobs", "/api/jobs/**",
                                                                "/manifest.json", "/sw.js", "/images/icons/**")
                                                .permitAll()

                                                // Swagger/OpenAPI endpoints
                                                .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                                                "/api-docs/**", "/v3/api-docs/**")
                                                .permitAll()

                                                // REST API v1 - Public endpoints
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .requestMatchers("/api/v1/jobs", "/api/v1/jobs/**",
                                                                "/api/v1/jobs/search", "/api/v1/jobs/active")
                                                .permitAll()

                                                // REST API v1 - Admin endpoints
                                                .requestMatchers("/api/v1/admin/**", "/api/admin/**").hasRole("ADMIN")

                                                // REST API v1 - Recruiter/Admin job management
                                                .requestMatchers("/api/v1/jobs/add", "/api/v1/jobs/update/**",
                                                                "/api/v1/jobs/delete/**",
                                                                "/api/jobs/add", "/api/jobs/update/**",
                                                                "/api/jobs/delete/**",
                                                                "/post-job")
                                                .hasAnyRole("RECRUITER", "ADMIN")

                                                // All other requests require authentication
                                                .anyRequest().authenticated())

                                // Form login for web interface
                                .formLogin(login -> login
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .permitAll())

                                // OAuth2 login
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .successHandler(customAuthenticationSuccessHandler)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .oidcUserService(customOidcUserService)))

                                // Logout
                                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll());

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
