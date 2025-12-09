package com.jobportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:F:/JobPortalSystem/uploads/profile/");

        registry.addResourceHandler("/uploads/resume/**")
                .addResourceLocations("file:F:/JobPortalSystem/uploads/resume/");
    }
}
