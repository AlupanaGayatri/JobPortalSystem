package com.jobportal.controller;

import com.jobportal.model.Profile;
import com.jobportal.model.User;
import com.jobportal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.UUID;

@Controller
public class ProfileController {

    private final Path UPLOAD_DIR = Paths.get("uploads");

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private SkillService skillService;

    public ProfileController() throws IOException {
        // ensure upload dir exists
        if (!Files.exists(UPLOAD_DIR)) {
            Files.createDirectories(UPLOAD_DIR);
        }
    }

    @GetMapping("/profile")
    public String profileForm(Model model, Principal principal) {
        User user = userService.findByPrincipal(principal);
        Profile profile = (user != null) ? profileService.getProfileByUserId(user.getId()) : null;
        if (profile == null) {
            profile = new Profile();
        }
        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute Profile profile,
            @RequestParam(value = "profilePhotoFile", required = false) MultipartFile profilePhotoFile,
            @RequestParam(value = "resumeFileUpload", required = false) MultipartFile resumeFileUpload,
            Principal principal,
            Model model) {

        try {
            // Get user
            User user = userService.findByPrincipal(principal);
            if (user == null) {
                model.addAttribute("error", "User not found.");
                model.addAttribute("user", null);
                model.addAttribute("profile", new Profile());
                return "profile";
            }

            // Check if profile already exists for this user
            Profile existingProfile = profileService.getProfileByUserId(user.getId());

            if (existingProfile != null) {
                // Update existing profile - copy ID to preserve the relationship
                profile.setId(existingProfile.getId());
                profile.setUser(user);

                // Preserve existing file names if new files are not uploaded
                if (profilePhotoFile == null || profilePhotoFile.isEmpty()) {
                    profile.setProfilePhoto(existingProfile.getProfilePhoto());
                }
                if (resumeFileUpload == null || resumeFileUpload.isEmpty()) {
                    profile.setResumeFile(existingProfile.getResumeFile());
                }
            } else {
                // New profile
                profile.setUser(user);
            }

            // Handle profile photo upload
            if (profilePhotoFile != null && !profilePhotoFile.isEmpty()) {
                String orig = StringUtils.cleanPath(profilePhotoFile.getOriginalFilename());
                String filename = UUID.randomUUID() + "_" + orig;
                Path profileDir = UPLOAD_DIR.resolve("profile");
                if (!Files.exists(profileDir)) {
                    Files.createDirectories(profileDir);
                }
                Path target = profileDir.resolve(filename);
                Files.copy(profilePhotoFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                profile.setProfilePhoto("profile/" + filename);
            }

            // Handle resume file upload
            if (resumeFileUpload != null && !resumeFileUpload.isEmpty()) {
                String orig = StringUtils.cleanPath(resumeFileUpload.getOriginalFilename());
                String filename = UUID.randomUUID() + "_" + orig;
                Path resumeDir = UPLOAD_DIR.resolve("resume");
                if (!Files.exists(resumeDir)) {
                    Files.createDirectories(resumeDir);
                }
                Path target = resumeDir.resolve(filename);
                Files.copy(resumeFileUpload.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                profile.setResumeFile("resume/" + filename);
            }

            // Set defaults if null
            if (profile.getSkillsCount() == null)
                profile.setSkillsCount(0);
            if (profile.getExperience() == null)
                profile.setExperience(0);

            // Save profile
            profileService.save(profile);

            return "redirect:/profile/view";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "File upload failed: " + e.getMessage());
            return "profile";
        }
    }

    @GetMapping("/profile/view")
    public String viewProfile(Model model, Principal principal) {
        User user = userService.findByPrincipal(principal);
        Profile profile = (user != null) ? profileService.getProfileByUserId(user.getId()) : null;

        // Get all related data
        if (user != null) {
            model.addAttribute("educationList", educationService.getByUser(user));
            model.addAttribute("experienceList", experienceService.getByUser(user));
            model.addAttribute("skillsList", skillService.getByUser(user));
        }

        model.addAttribute("profile", profile);
        model.addAttribute("user", user);
        return "profile-view";
    }

}
