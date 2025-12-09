package com.jobportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String adminUsers() {
        return "admin-users";
    }

    @GetMapping("/jobs")
    public String adminJobs() {
        return "admin-jobs";
    }

    @GetMapping("/applications")
    public String adminApplications() {
        return "admin-applications";
    }

    @GetMapping("/create-job")
    public String createJob() {
        return "admin-create-job";
    }
}
