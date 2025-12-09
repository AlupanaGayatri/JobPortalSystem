package com.jobportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final JobApplicationService jobApplicationService;

    public AdminController(UserService userService,
            JobService jobService,
            JobApplicationService jobApplicationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.jobApplicationService = jobApplicationService;
    }

    // ========== USER MANAGEMENT ==========

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ========== JOB MANAGEMENT ==========

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAll());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Job job = jobService.getById(id);
        if (job == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(job);
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        return ResponseEntity.ok(jobService.postJob(job));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job job) {
        return ResponseEntity.ok(jobService.updateJob(id, job));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    // ========== APPLICATION MANAGEMENT ==========

    @GetMapping("/applications")
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        return ResponseEntity.ok(jobApplicationService.getAllApplications());
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<JobApplication> getApplicationById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobApplicationService.getApplicationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/applications/{id}/approve")
    public ResponseEntity<JobApplication> approveApplication(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.approveApplication(id));
    }

    @PutMapping("/applications/{id}/reject")
    public ResponseEntity<JobApplication> rejectApplication(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.rejectApplication(id));
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        try {
            jobApplicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/applications/job/{jobId}")
    public ResponseEntity<List<JobApplication>> getApplicationsByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByJob(jobId));
    }

    @GetMapping("/applications/user/{userId}")
    public ResponseEntity<List<JobApplication>> getApplicationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByUser(userId));
    }
}
