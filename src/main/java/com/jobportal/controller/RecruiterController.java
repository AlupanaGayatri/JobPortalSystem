package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private com.jobportal.service.EducationService educationService;

    @Autowired
    private com.jobportal.service.ExperienceService experienceService;

    @Autowired
    private com.jobportal.service.SkillService skillService;

    @Autowired
    private com.jobportal.service.EmailService emailService;

    @Autowired
    private com.jobportal.service.CompanyService companyService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Helper to get authenticated recruiter
    private User getRecruiter(Principal principal) {
        if (principal == null)
            return null;
        User user = userService.findByPrincipal(principal);
        if (user != null && ("RECRUITER".equals(user.getRole()) || "ADMIN".equals(user.getRole()))) {
            return user;
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        long jobsPostedCount = jobService.getJobsByRecruiter(user.getId()).size();
        long activeJobsCount = jobService.getJobsByRecruiter(user.getId()).stream()
                .filter(job -> "ACTIVE".equals(job.getStatus()))
                .count();

        // Fetch all applications for this recruiter
        List<com.jobportal.model.JobApplication> applications = jobApplicationService
                .getApplicationsByRecruiter(user.getId());
        long totalApplicants = applications.size();

        // Calculate KPI Counts
        java.time.LocalDateTime twentyFourHoursAgo = java.time.LocalDateTime.now().minusHours(24);
        long newApplicationsCount = applications.stream()
                .filter(a -> a.getAppliedDate() != null && a.getAppliedDate().isAfter(twentyFourHoursAgo))
                .count();

        // Since we don't have 'interviewDate' or 'hiredDate' yet, we use proxies or
        // total active counts
        long interviewsTodayCount = applications.stream()
                .filter(a -> "INTERVIEW".equals(a.getStatus()))
                .count(); // Showing total active interviews as proxy

        java.time.LocalDateTime startOfMonth = java.time.LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long hiresThisMonthCount = applications.stream()
                .filter(a -> "HIRED".equals(a.getStatus()) && a.getAppliedDate() != null
                        && a.getAppliedDate().isAfter(startOfMonth))
                .count(); // Proxy: Hired applicants who applied this month

        // Funnel Data
        long funnelApplied = totalApplicants;
        long funnelShortlisted = applications.stream().filter(a -> "SHORTLISTED".equals(a.getStatus())).count();
        long funnelInterview = applications.stream().filter(a -> "INTERVIEW".equals(a.getStatus())).count();
        long funnelOffer = applications.stream().filter(a -> "OFFER".equals(a.getStatus())).count(); // Assuming 'OFFER'
                                                                                                     // status exists or
                                                                                                     // will be added
        long funnelHired = applications.stream().filter(a -> "HIRED".equals(a.getStatus())).count();

        model.addAttribute("newApplicationsCount", newApplicationsCount);
        model.addAttribute("interviewsTodayCount", interviewsTodayCount);
        model.addAttribute("hiresThisMonthCount", hiresThisMonthCount);

        model.addAttribute("funnelApplied", funnelApplied);
        model.addAttribute("funnelShortlisted", funnelShortlisted);
        model.addAttribute("funnelInterview", funnelInterview);
        model.addAttribute("funnelOffer", funnelOffer);
        model.addAttribute("funnelHired", funnelHired);

        // Recent Activity (Top 5 recent applications)
        List<com.jobportal.model.JobApplication> recentApplications = applications.stream()
                .filter(a -> a.getAppliedDate() != null)
                .sorted((a1, a2) -> a2.getAppliedDate().compareTo(a1.getAppliedDate()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("recentApplications", recentApplications);

        // Suggested Candidates Logic
        // 1. Collect all required skills from Recruiter's Active Jobs
        java.util.Set<String> requiredSkills = jobService.getJobsByRecruiter(user.getId()).stream()
                .filter(job -> "ACTIVE".equals(job.getStatus()) && job.getSkillsRequired() != null)
                .flatMap(job -> java.util.Arrays.stream(job.getSkillsRequired().split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());

        // 2. Fetch all candidates (profiles)
        List<com.jobportal.model.Profile> allProfiles = profileService.getAllProfiles();

        // 3. Score each candidate
        List<Map<String, Object>> suggestedCandidates = new java.util.ArrayList<>();

        for (com.jobportal.model.Profile p : allProfiles) {
            if (p.getUser() == null || p.getUser().getId().equals(user.getId()))
                continue; // Skip self/invalid

            List<String> candidateSkills = skillService.getByUser(p.getUser()).stream()
                    .map(com.jobportal.model.Skill::getName)
                    .map(String::toLowerCase)
                    .collect(java.util.stream.Collectors.toList());

            long matchCount = candidateSkills.stream()
                    .filter(requiredSkills::contains)
                    .count();

            int matchPercentage = 0;
            if (!requiredSkills.isEmpty()) {
                matchPercentage = (int) ((double) matchCount / requiredSkills.size() * 100);
            } else if (!candidateSkills.isEmpty()) {
                // If no specific job skills, just verify they have *some* skills (Active
                // candidate)
                matchPercentage = 50;
            }

            // Boost for recent activity or completeness (Mock logic for now)
            if (p.getHeadline() != null)
                matchPercentage += 10;

            if (matchPercentage > 100)
                matchPercentage = 100;

            if (matchPercentage > 0) {
                Map<String, Object> candidateMap = new java.util.HashMap<>();
                candidateMap.put("profile", p);
                candidateMap.put("user", p.getUser());
                candidateMap.put("matchPercentage", matchPercentage);
                candidateMap.put("topSkills",
                        candidateSkills.stream().limit(3).collect(java.util.stream.Collectors.toList()));
                candidateMap.put("remainingSkills", Math.max(0, candidateSkills.size() - 3));

                suggestedCandidates.add(candidateMap);
            }
        }

        // 4. Sort by Match Percentage DESC
        suggestedCandidates
                .sort((m1, m2) -> ((Integer) m2.get("matchPercentage")).compareTo((Integer) m1.get("matchPercentage")));

        model.addAttribute("suggestedCandidates",
                suggestedCandidates.stream().limit(5).collect(java.util.stream.Collectors.toList()));

        model.addAttribute("user", user);
        model.addAttribute("jobsPostedCount", jobsPostedCount);
        model.addAttribute("activeJobsCount", activeJobsCount);
        model.addAttribute("totalApplicants", totalApplicants);
        model.addAttribute("currentPage", "dashboard"); // For highlighting sidebar

        return "recruiter/dashboard";
    }

    @GetMapping("/jobs")
    public String myJobs(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("jobs", jobService.getJobsByRecruiter(user.getId()));
        return "recruiter/my-jobs";
    }

    @GetMapping("/jobs/{jobId}/applications")
    public String jobApplications(@PathVariable Long jobId, Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        // Verify job belongs to recruiter (TODO: Move this check to service or add
        // here)
        // For now, assuming if they have the ID, they can view (in a real app, strictly
        // check ownership)

        model.addAttribute("user", user);
        model.addAttribute("jobId", jobId);
        // We need 'job' object too for display
        model.addAttribute("job", jobService.getById(jobId));
        model.addAttribute("applications", jobApplicationService.getApplicationsByJob(jobId));

        return "recruiter/applications";
    }

    @GetMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam String status, Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        com.jobportal.model.JobApplication app = jobApplicationService.getApplicationById(id);

        if (app != null && app.getJob().getRecruiterId().equals(user.getId())) {
            jobApplicationService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Application status updated to " + status);
        } else {
            redirectAttributes.addFlashAttribute("error", "Unauthorized or application not found.");
        }

        // Redirect back to the specific job's application list
        return "redirect:/recruiter/jobs/" + app.getJob().getId() + "/applications";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        com.jobportal.model.Profile profile = profileService.getProfileByUserId(user.getId());
        if (profile == null) {
            profile = new com.jobportal.model.Profile();
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("currentPage", "profile"); // Sidebar highlight
        return "recruiter/profile-view";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        com.jobportal.model.Profile profile = profileService.getProfileByUserId(user.getId());
        if (profile == null) {
            profile = new com.jobportal.model.Profile();
        }

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        return "recruiter/profile"; // Reuses the form template
    }

    @org.springframework.web.bind.annotation.PostMapping("/profile")
    public String saveProfile(
            @org.springframework.web.bind.annotation.ModelAttribute com.jobportal.model.Profile profile,
            @org.springframework.web.bind.annotation.RequestParam(value = "profilePhotoFile", required = false) org.springframework.web.multipart.MultipartFile profilePhotoFile,
            @org.springframework.web.bind.annotation.RequestParam(value = "email", required = false) String email,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        try {
            // Update User Email if provided and different
            if (email != null && !email.trim().isEmpty() && !email.equals(user.getEmail())) {
                user.setEmail(email);
                userService.save(user); // Assuming UserService has a plain save method (or use repository directly if
                                        // service logic prevents simple update)
                // Note: In a real app, we'd check for duplicates here.
            }

            com.jobportal.model.Profile existingProfile = profileService.getProfileByUserId(user.getId());

            if (existingProfile != null) {
                profile.setId(existingProfile.getId());
                profile.setUser(user);
                if (profilePhotoFile == null || profilePhotoFile.isEmpty()) {
                    profile.setProfilePhoto(existingProfile.getProfilePhoto());
                }
                profile.setResumeFile(existingProfile.getResumeFile());
                profile.setSkillsCount(existingProfile.getSkillsCount());
                profile.setExperience(existingProfile.getExperience());
            } else {
                profile.setUser(user);
                profile.setSkillsCount(0);
                profile.setExperience(0);
            }

            if (profilePhotoFile != null && !profilePhotoFile.isEmpty()
                    && profilePhotoFile.getOriginalFilename() != null) {
                String filename = java.util.UUID.randomUUID() + "_"
                        + org.springframework.util.StringUtils.cleanPath(profilePhotoFile.getOriginalFilename());
                java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads/profile");
                if (!java.nio.file.Files.exists(uploadDir)) {
                    java.nio.file.Files.createDirectories(uploadDir);
                }
                java.nio.file.Files.copy(profilePhotoFile.getInputStream(), uploadDir.resolve(filename),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                profile.setProfilePhoto("profile/" + filename);
            }

            profileService.save(profile);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/recruiter/profile/edit";
        }

        return "redirect:/recruiter/profile"; // Redirect to View page
    }

    @GetMapping("/candidates")
    public String candidates(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        List<com.jobportal.model.JobApplication> applications = jobApplicationService
                .getApplicationsByRecruiter(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", "candidates");
        return "recruiter/candidates";
    }

    @GetMapping("/candidates/add")
    public String addCandidate(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);
        return "recruiter/add-candidate";
    }

    @org.springframework.web.bind.annotation.PostMapping("/candidates/add")
    public String saveCandidate(
            @org.springframework.web.bind.annotation.RequestParam String firstName,
            @org.springframework.web.bind.annotation.RequestParam String lastName,
            @org.springframework.web.bind.annotation.RequestParam String email,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        // TODO: Implement actual user creation/resume parsing logic here
        // For now, we simulate success
        redirectAttributes.addFlashAttribute("success",
                "Candidate " + firstName + " " + lastName + " added to your pool.");

        return "redirect:/recruiter/candidates";
    }

    @GetMapping("/candidates/profile/{userId}")
    public String viewCandidateProfile(@PathVariable Long userId, Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        // Ideally check if this candidate has applied to any of the recruiter's jobs
        // For MVP, allow viewing (open profiles)

        com.jobportal.model.Profile profile = profileService.getProfileByUserId(userId);
        User candidate = userService.getUserById(userId).orElse(null); // Changed from findById to getUserById

        if (candidate == null || profile == null) {
            return "redirect:/recruiter/candidates"; // Or 404
        }

        model.addAttribute("user", user); // The recruiter
        model.addAttribute("candidate", candidate); // The candidate being viewed
        model.addAttribute("profile", profile);

        // Fetch detailed education and experience
        model.addAttribute("educationList", educationService.getByUser(candidate));
        model.addAttribute("experienceList", experienceService.getByUser(candidate));

        // AUTO-UPDATE STATUS: Mark applications as "REVIEWED" if they are still
        // "APPLIED" or "PENDING"
        List<com.jobportal.model.JobApplication> apps = jobApplicationService.getApplicationsByRecruiter(user.getId());
        for (com.jobportal.model.JobApplication app : apps) {
            if (app.getUser().getId().equals(userId) &&
                    ("PENDING".equalsIgnoreCase(app.getStatus()) || "APPLIED".equalsIgnoreCase(app.getStatus()))) {
                jobApplicationService.updateStatus(app.getId(), "REVIEWED");
            }
        }

        return "recruiter/candidate-profile";
    }

    @GetMapping("/pipeline")
    public String pipeline(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        List<com.jobportal.model.JobApplication> applications = jobApplicationService
                .getApplicationsByRecruiter(user.getId());

        // Calculate counts for badges
        long appliedCount = applications.stream().filter(a -> "APPLIED".equals(a.getStatus())
                || "PENDING".equals(a.getStatus()) || "REVIEWED".equals(a.getStatus())).count();
        long shortlistedCount = applications.stream().filter(a -> "SHORTLISTED".equals(a.getStatus())).count();
        long interviewCount = applications.stream().filter(a -> "INTERVIEW".equals(a.getStatus())).count();
        long hiredCount = applications.stream().filter(a -> "HIRED".equals(a.getStatus())).count();

        model.addAttribute("user", user);
        model.addAttribute("applications", applications);
        model.addAttribute("appliedCount", appliedCount);
        model.addAttribute("shortlistedCount", shortlistedCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("hiredCount", hiredCount);
        model.addAttribute("currentPage", "pipeline");
        return "recruiter/pipeline";
    }

    @GetMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable Long id, Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        com.jobportal.model.Job job = jobService.getById(id);
        if (job != null && job.getRecruiterId().equals(user.getId())) {
            jobService.deleteJob(id);
            redirectAttributes.addFlashAttribute("success", "Job deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unauthorized or job not found.");
        }

        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/shortlists")
    public String shortlists(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        List<com.jobportal.model.JobApplication> applications = jobApplicationService
                .getApplicationsByRecruiter(user.getId()).stream()
                .filter(a -> "SHORTLISTED".equals(a.getStatus()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", "shortlists");
        return "recruiter/shortlists";
    }

    @GetMapping("/interviews")
    public String interviews(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        List<com.jobportal.model.JobApplication> applications = jobApplicationService
                .getApplicationsByRecruiter(user.getId()).stream()
                .filter(a -> "INTERVIEW".equals(a.getStatus()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", "interviews");
        return "recruiter/interviews";
    }

    @org.springframework.web.bind.annotation.PostMapping("/send-email")
    public String sendEmail(@org.springframework.web.bind.annotation.RequestParam String to,
            @org.springframework.web.bind.annotation.RequestParam String subject,
            @org.springframework.web.bind.annotation.RequestParam String message,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String sourcePage,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        try {
            // Append Recruiter signature
            String fullMessage = message + "\n\nRegards,\n" + user.getFullName();
            emailService.sendSimpleMessage(to, subject, fullMessage);
            redirectAttributes.addFlashAttribute("success", "Email sent successfully to " + to);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to send email: " + e.getMessage());
        }

        if ("messages".equals(sourcePage)) {
            return "redirect:/recruiter/messages";
        }

        return "redirect:/recruiter/candidates";
    }

    @GetMapping("/messages")
    public String messages(@org.springframework.web.bind.annotation.RequestParam(required = false) Long candidateId,
            Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        // Fetch candidates (unique applicants)
        List<com.jobportal.model.JobApplication> apps = jobApplicationService.getApplicationsByRecruiter(user.getId());
        List<User> candidates = apps.stream()
                .map(com.jobportal.model.JobApplication::getUser)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("candidates", candidates);
        model.addAttribute("currentPage", "messages");

        if (candidateId != null) {
            User selected = userService.getUserById(candidateId).orElse(null);
            model.addAttribute("selectedCandidate", selected);

            // Allow pre-filling message if directed here
            // We can add logic to fetch "history" if we had a database table for it
            // For now, it stays empty or static
        }

        return "recruiter/messages";
    }

    @GetMapping("/analytics")
    public String analytics(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        List<com.jobportal.model.JobApplication> applications = jobApplicationService
                .getApplicationsByRecruiter(user.getId());

        // 1. Total Hires
        long totalHires = applications.stream()
                .filter(a -> "HIRED".equals(a.getStatus()))
                .count();

        // 2. Active Pipelines (Jobs with at least one active application)
        long activePipelines = jobService.getJobsByRecruiter(user.getId()).stream()
                .filter(job -> "ACTIVE".equals(job.getStatus()))
                .count();

        // 3. Time to Hire (Mock logic mixed with real data: avg days from applied to
        // hired)
        // Since we don't store "hiredDate", we'll estimate using "appliedDate" vs now
        // for HIRED apps
        double avgDaysToHire = applications.stream()
                .filter(a -> "HIRED".equals(a.getStatus()) && a.getAppliedDate() != null)
                .mapToLong(a -> java.time.temporal.ChronoUnit.DAYS.between(a.getAppliedDate(),
                        java.time.LocalDateTime.now()))
                .average()
                .orElse(0);
        int timeToHire = (int) Math.round(avgDaysToHire > 0 ? avgDaysToHire : 15); // Default to 15 if no data

        // 4. Offer Acceptance Rate (Mock: 85% is a good standard, let's keep it static
        // or random variation for now as we lack 'OFFER' data history)
        int offerAcceptance = 85;

        model.addAttribute("user", user);
        model.addAttribute("totalHires", totalHires);
        model.addAttribute("activePipelines", activePipelines);
        model.addAttribute("timeToHire", timeToHire);
        model.addAttribute("offerAcceptance", offerAcceptance);

        // Mock chart data for now (could be real if we aggregated dates)
        model.addAttribute("chartData", "Mock Data");

        model.addAttribute("currentPage", "analytics");
        return "recruiter/analytics";
    }

    @GetMapping("/companies")
    public String companies(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        com.jobportal.model.Company company = companyService.getCompanyByRecruiter(user.getId())
                .orElse(new com.jobportal.model.Company());

        model.addAttribute("user", user);
        model.addAttribute("company", company);
        model.addAttribute("currentPage", "companies");
        return "recruiter/companies";
    }

    @org.springframework.web.bind.annotation.PostMapping("/companies/save")
    public String saveCompany(
            @org.springframework.web.bind.annotation.ModelAttribute com.jobportal.model.Company company,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        try {
            com.jobportal.model.Company existing = companyService.getCompanyByRecruiter(user.getId()).orElse(null);
            if (existing != null) {
                // Update existing
                company.setId(existing.getId());
                company.setRecruiter(user);
                // Preserve logo if not handled here (we'll handle logo upload similarly to
                // profile later if needed)
                company.setLogo(existing.getLogo());
            } else {
                // Create new
                company.setRecruiter(user);
            }

            companyService.save(company);
            redirectAttributes.addFlashAttribute("success", "Company profile updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error saving company profile.");
        }

        return "redirect:/recruiter/companies";
    }

    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {
        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "settings");
        return "recruiter/settings";
    }

    @org.springframework.web.bind.annotation.PostMapping("/settings/account")
    public String updateAccount(
            @org.springframework.web.bind.annotation.RequestParam String fullName,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        try {
            if (fullName != null && !fullName.trim().isEmpty()) {
                user.setFullName(fullName);
                userService.save(user);
                redirectAttributes.addFlashAttribute("success", "Account details updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update account.");
        }

        return "redirect:/recruiter/settings";
    }

    @org.springframework.web.bind.annotation.PostMapping("/settings/password")
    public String updatePassword(
            @org.springframework.web.bind.annotation.RequestParam String currentPassword,
            @org.springframework.web.bind.annotation.RequestParam String newPassword,
            @org.springframework.web.bind.annotation.RequestParam String confirmPassword,
            Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        User user = getRecruiter(principal);
        if (user == null)
            return "redirect:/login";

        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
                return "redirect:/recruiter/settings";
            }

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Incorrect current password.");
                return "redirect:/recruiter/settings";
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);

            redirectAttributes.addFlashAttribute("success", "Password updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update password.");
        }

        return "redirect:/recruiter/settings";
    }

    // Hidden helper to populate demo data for the current user - REMOVED
}
