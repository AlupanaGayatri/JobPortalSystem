package com.jobportal.dto.mapper;

import com.jobportal.dto.request.ProfileRequest;
import com.jobportal.dto.response.ProfileResponse;
import com.jobportal.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper for Profile entity and DTOs
 */
@Component
public class ProfileMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Convert Profile entity to ProfileResponse DTO
     */
    public ProfileResponse toResponse(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileResponse response = ProfileResponse.builder()
                .id(profile.getId())
                .headline(profile.getHeadline())
                .summary(profile.getSummary())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .githubUrl(profile.getGithubUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .profilePhoto(profile.getProfilePhoto())
                .resumeFile(profile.getResumeFile())
                .experience(profile.getExperience())
                .skillsCount(profile.getSkillsCount())
                .currentRole(profile.getCurrentRole())
                .build();

        // Add user information if available
        if (profile.getUser() != null) {
            response.setUser(userMapper.toResponse(profile.getUser()));
        }

        // Calculate profile completeness
        response.setCompleteness(calculateCompleteness(profile));

        return response;
    }

    /**
     * Convert ProfileRequest to Profile entity
     */
    public Profile toEntity(ProfileRequest request) {
        if (request == null) {
            return null;
        }

        Profile profile = new Profile();
        updateEntity(profile, request);

        return profile;
    }

    /**
     * Update Profile entity from ProfileRequest
     */
    public void updateEntity(Profile profile, ProfileRequest request) {
        if (profile == null || request == null) {
            return;
        }

        profile.setHeadline(request.getHeadline());
        profile.setSummary(request.getSummary());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setExperience(request.getExperience());
        profile.setCurrentRole(request.getCurrentRole());
    }

    /**
     * Calculate profile completeness percentage
     */
    private Integer calculateCompleteness(Profile profile) {
        if (profile == null) {
            return 0;
        }

        int totalFields = 11;
        int filledFields = 0;

        if (profile.getHeadline() != null && !profile.getHeadline().isEmpty())
            filledFields++;
        if (profile.getSummary() != null && !profile.getSummary().isEmpty())
            filledFields++;
        if (profile.getPhone() != null && !profile.getPhone().isEmpty())
            filledFields++;
        if (profile.getAddress() != null && !profile.getAddress().isEmpty())
            filledFields++;
        if (profile.getGithubUrl() != null && !profile.getGithubUrl().isEmpty())
            filledFields++;
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isEmpty())
            filledFields++;
        if (profile.getProfilePhoto() != null && !profile.getProfilePhoto().isEmpty())
            filledFields++;
        if (profile.getResumeFile() != null && !profile.getResumeFile().isEmpty())
            filledFields++;
        if (profile.getExperience() != null)
            filledFields++;
        if (profile.getSkillsCount() != null && profile.getSkillsCount() > 0)
            filledFields++;
        if (profile.getCurrentRole() != null && !profile.getCurrentRole().isEmpty())
            filledFields++;

        return (int) Math.round((filledFields * 100.0) / totalFields);
    }
}
