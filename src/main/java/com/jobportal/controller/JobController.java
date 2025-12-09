package com.jobportal.controller;

import com.jobportal.model.Job;
import com.jobportal.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // list/search
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<Job>> all(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(jobService.searchJobs(status, title, location, experienceLevel, jobType, minSalary,
                maxSalary, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Job j = jobService.getById(id);
        if (j == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(j);
    }

    // create — only recruiter or admin
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<?> add(@RequestBody Job job) {
        // recruiter id should be set by client to match authenticated user in demo
        // In a real app, we'd get the user from the SecurityContext
        Job created = jobService.postJob(job);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Job job) {
        Job updated = jobService.updateJob(id, job);
        if (updated == null)
            return ResponseEntity.badRequest().body("Invalid ID");
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean ok = jobService.deleteJob(id);
        if (!ok)
            return ResponseEntity.badRequest().body("Invalid ID");
        return ResponseEntity.ok("Deleted");
    }
}
