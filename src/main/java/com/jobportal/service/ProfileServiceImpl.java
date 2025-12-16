package com.jobportal.service;

import com.jobportal.model.Profile;
import com.jobportal.repository.ProfileRepository;
import com.jobportal.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public Profile getProfileByUserId(Long userId) {
        Optional<Profile> opt = profileRepository.findByUserId(userId);
        return opt.orElse(null);
    }

    @Override
    public java.util.List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
}
