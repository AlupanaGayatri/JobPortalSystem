package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import com.jobportal.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class JobViewController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobApplicationService jobApplicationService;

    @GetMapping("/jobs")
    public String jobsPage(Model model, Principal principal) {
        model.addAttribute("jobs", jobService.getAll());
        if (principal != null) {
            User user = userService.findByPrincipal(principal);
            model.addAttribute("user", user);
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "jobs";
    }

    @GetMapping("/apply")
    public String applyPage(Model model, Principal principal, @RequestParam(required = false) Long jobId) {
        if (principal != null) {
            User user = userService.findByPrincipal(principal);
            model.addAttribute("user", user);
            model.addAttribute("userId", user.getId());
        }
        if (jobId != null) {
            model.addAttribute("jobId", jobId);
        }
        return "apply";
    }

    @GetMapping("/my-applications")
    public String myApplicationsPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByPrincipal(principal);
            model.addAttribute("user", user);
            model.addAttribute("applications", jobApplicationService.getApplicationsByUser(user.getId()));
        }
        return "my-applications";
    }

    @GetMapping("/post-job")
    public String postJobPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByPrincipal(principal);
            model.addAttribute("user", user);
            model.addAttribute("job", new com.jobportal.model.Job());
            return "post-job";
        }
        return "redirect:/login";
    }

    @PostMapping("/post-job")
    public String handlePostJob(com.jobportal.model.Job job, Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal != null) {
            User user = userService.findByPrincipal(principal);
            // Ensure user is recruiter
            if (!"RECRUITER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                return "redirect:/dashboard?error=unauthorized";
            }

            // Set status to ACTIVE by default
            job.setStatus("ACTIVE");

            // Associate the job with the recruiter
            job.setRecruiterId(user.getId());

            jobService.postJob(job);

            redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
            return "redirect:/recruiter/jobs";
        }
        return "redirect:/login";
    }
}
