package com.jobportal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Provides interactive API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI jobPortalOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Job Portal System API")
                                                .description("Enterprise-grade REST API for Job Portal System with JWT authentication, "
                                                                +
                                                                "rate limiting, caching, and comprehensive security features")
                                                .version("v1.0")
                                                .contact(new Contact()
                                                                .name("Job Portal Team")
                                                                .email("support@jobportal.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:9090")
                                                                .description("Development Server"),
                                                new Server()
                                                                .url("https://api.jobportal.com")
                                                                .description("Production Server")))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new Components()
                                                .addSecuritySchemes("Bearer Authentication",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Enter JWT token obtained from /api/v1/auth/login")));
        }
}
