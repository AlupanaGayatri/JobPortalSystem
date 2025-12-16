package com.jobportal.service;

import com.jobportal.model.Profile;

public interface ProfileService {
    Profile save(Profile profile);

    Profile getProfileByUserId(Long userId);

    java.util.List<Profile> getAllProfiles();
}
