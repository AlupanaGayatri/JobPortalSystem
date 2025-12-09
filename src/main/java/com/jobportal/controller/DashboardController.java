package com.jobportal.controller;

import com.jobportal.model.Profile;
import com.jobportal.model.User;
import com.jobportal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private com.jobportal.repository.JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobService jobService;

    @GetMapping({ "/dashboard", "/dashboard.html" })
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByPrincipal(principal);
        if (user == null) {
            return "redirect:/login?error=user_not_found";
        }
        Profile profile = profileService.getProfileByUserId(user.getId());

        // Calculate statistics
        int skillsCount = (user != null) ? skillService.getByUser(user).size() : 0;
        int experienceCount = (user != null) ? experienceService.getByUser(user).size() : 0;
        int educationCount = (user != null) ? educationService.getByUser(user).size() : 0;

        // Calculate profile completeness and determine missing fields
        int profilePercentage = 0;
        String incompleteSection = null;
        String missingFields = "";

        if (profile == null) {
            // No profile exists
            incompleteSection = "/profile";
            missingFields = "Complete your profile";
        } else {
            int fieldsCompleted = 0;
            // Only count essential fields (making photo and resume optional, and experience
            // can be 0)
            int totalFields = 6; // headline, summary, phone, address, currentRole, experience (0 is valid)
            int missingCount = 0;
            StringBuilder missingList = new StringBuilder();

            if (profile.getHeadline() == null || profile.getHeadline().trim().isEmpty()) {
                missingCount++;
                missingList.append("Headline, ");
            } else
                fieldsCompleted++;

            if (profile.getSummary() == null || profile.getSummary().trim().isEmpty()) {
                missingCount++;
                missingList.append("Summary, ");
            } else
                fieldsCompleted++;

            if (profile.getPhone() == null || profile.getPhone().trim().isEmpty()) {
                missingCount++;
                missingList.append("Phone, ");
            } else
                fieldsCompleted++;

            if (profile.getAddress() == null || profile.getAddress().trim().isEmpty()) {
                missingCount++;
                missingList.append("Address, ");
            } else
                fieldsCompleted++;

            if (profile.getCurrentRole() == null || profile.getCurrentRole().trim().isEmpty()) {
                missingCount++;
                missingList.append("Current Role, ");
            } else
                fieldsCompleted++;

            // Experience: 0 is valid (for freshers), only null is missing
            if (profile.getExperience() == null) {
                missingCount++;
                missingList.append("Experience, ");
            } else
                fieldsCompleted++;

            // Profile photo and resume are optional - don't count them in completeness
            // But we can show them as recommendations

            profilePercentage = (int) Math.round((double) fieldsCompleted / totalFields * 100);

            // Determine which section to redirect to based on missing data
            if (missingCount > 0) {
                incompleteSection = "/profile";
                missingFields = missingCount + " field" + (missingCount > 1 ? "s" : "") + " incomplete";
            } else if (educationCount == 0) {
                incompleteSection = "/education";
                missingFields = "Add your education";
            } else if (experienceCount == 0) {
                incompleteSection = "/experience";
                missingFields = "Add work experience";
            } else if (skillsCount == 0) {
                incompleteSection = "/skills";
                missingFields = "Add your skills";
            } else {
                incompleteSection = "/profile/view";
                missingFields = "Profile is complete!";
            }
        }

        if ("RECRUITER".equals(user.getRole())) {
            // Recruiter Dashboard Logic
            long jobsPostedCount = jobService.getJobsByRecruiter(user.getId()).size();
            long activeJobsCount = jobService.getJobsByRecruiter(user.getId()).stream()
                    .filter(job -> "ACTIVE".equals(job.getStatus()))
                    .count();

            // We need to inject JobApplicationRepository to get this count efficiently
            // For now, we'll use a placeholder or add the repository dependency
            // Assuming we add the repository:
            // long totalApplicants =
            // jobApplicationRepository.countByJobRecruiterId(user.getId());

            // Since we don't have the repository injected yet, let's add it to the
            // controller fields first
            // But we can't easily add a field in this replace block without replacing the
            // whole class
            // So we will rely on a separate edit to add the repository field, or use the
            // service if available.
            // JobApplicationService doesn't have a count method exposed yet.

            // Let's stick to the plan: We will update the controller to inject the
            // repository in a separate step if needed,
            // or better, let's just use the service to get all applications for jobs posted
            // by this user.
            // But JobApplicationService.getApplicationsByJob takes a jobId.

            // Let's use a workaround for now:
            // We'll update the controller class structure in the next step to include
            // JobApplicationRepository.
            // For this block, we will just set the values.

            model.addAttribute("jobsPostedCount", jobsPostedCount);
            model.addAttribute("activeJobsCount", activeJobsCount);
            // model.addAttribute("totalApplicants", totalApplicants); // Will add this
            // after injecting repo
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("skillsCount", skillsCount);
        model.addAttribute("experienceCount", experienceCount);
        model.addAttribute("educationCount", educationCount);
        model.addAttribute("profilePercentage", profilePercentage);
        model.addAttribute("incompleteSection", incompleteSection);
        model.addAttribute("missingFields", missingFields);

        return "dashboard";
    }
}
