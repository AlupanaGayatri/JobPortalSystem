package com.jobportal.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many applications can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Many applications can belong to one job
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "resume_text", length = 255)
    private String resumeText;

    // PENDING, APPROVED, REJECTED, WITHDRAWN
    private String status;

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    public JobApplication() {
    }

    public JobApplication(User user, Job job, String resumeText, String status) {
        this.user = user;
        this.job = job;
        this.resumeText = resumeText;
        this.status = status;
        this.appliedDate = LocalDateTime.now();
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }
}
