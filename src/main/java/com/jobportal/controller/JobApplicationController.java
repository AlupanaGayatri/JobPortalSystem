package com.jobportal.controller;

import com.jobportal.model.JobApplication;
import com.jobportal.service.JobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*") // allow frontend later
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    private final com.jobportal.service.UserService userService;

    public JobApplicationController(JobApplicationService jobApplicationService,
            com.jobportal.service.UserService userService) {
        this.jobApplicationService = jobApplicationService;
        this.userService = userService;
    }

    // 👩‍💼 Candidate applies to a job
    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(@RequestBody ApplyRequest request, java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        com.jobportal.model.User user = userService.findByPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        try {
            JobApplication application = jobApplicationService.applyForJob(
                    user.getId(),
                    request.getJobId(),
                    request.getResumeText());
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 👩‍💼 Candidate withdraws application
    @PutMapping("/withdraw/{id}")
    public ResponseEntity<JobApplication> withdraw(@PathVariable Long id) {
        JobApplication application = jobApplicationService.withdrawApplication(id);
        return ResponseEntity.ok(application);
    }

    // 📋 All applications of one user (Legacy/Admin)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplication>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByUser(userId));
    }

    // 📋 My Applications (Secure)
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }
        com.jobportal.model.User user = userService.findByPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }
        return ResponseEntity.ok(jobApplicationService.getApplicationsByUser(user.getId()));
    }

    // 📋 All applications for one job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplication>> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByJob(jobId));
    }

    // 🛠 Admin approves application
    @PutMapping("/approve/{id}")
    public ResponseEntity<JobApplication> approve(@PathVariable Long id) {
        JobApplication application = jobApplicationService.approveApplication(id);
        return ResponseEntity.ok(application);
    }

    // 🛠 Admin rejects application
    @PutMapping("/reject/{id}")
    public ResponseEntity<JobApplication> reject(@PathVariable Long id) {
        JobApplication application = jobApplicationService.rejectApplication(id);
        return ResponseEntity.ok(application);
    }

    // 🛠 Admin: list all applications
    @GetMapping("/all")
    public ResponseEntity<List<JobApplication>> getAll() {
        return ResponseEntity.ok(jobApplicationService.getAllApplications());
    }

    // 🗑 Delete all my applications
    @DeleteMapping("/my")
    public ResponseEntity<?> deleteMyApplications(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }
        com.jobportal.model.User user = userService.findByPrincipal(principal);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }
        jobApplicationService.deleteAllApplications(user.getId());
        return ResponseEntity.ok().build();
    }

    // DTO for apply request
    public static class ApplyRequest {
        private Long userId;
        private Long jobId;
        private String resumeText;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getJobId() {
            return jobId;
        }

        public void setJobId(Long jobId) {
            this.jobId = jobId;
        }

        public String getResumeText() {
            return resumeText;
        }

        public void setResumeText(String resumeText) {
            this.resumeText = resumeText;
        }
    }
}
