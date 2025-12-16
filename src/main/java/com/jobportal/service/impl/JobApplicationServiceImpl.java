package com.jobportal.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobportal.model.Job;
import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobApplicationService;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final com.jobportal.service.EmailService emailService;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            com.jobportal.service.EmailService emailService) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    @Override
    public JobApplication applyForJob(Long userId, Long jobId, String resumeText) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        // Check if already applied
        if (jobApplicationRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new RuntimeException("You have already applied for this job!");
        }

        // Use default text if resume is empty/null since we removed the field from UI
        String finalResumeText = (resumeText == null || resumeText.trim().isEmpty())
                ? "Applied via Quick Apply"
                : resumeText;

        JobApplication application = new JobApplication(user, job, finalResumeText, "PENDING");
        application.setAppliedDate(java.time.LocalDateTime.now());
        JobApplication savedApp = jobApplicationRepository.save(application);

        // Send email notification
        try {
            String subject = "Application Received: " + job.getTitle();
            String body = "Dear " + user.getFullName() + ",\n\n" +
                    "You have successfully applied for the position of " + job.getTitle() + " at "
                    + job.getCompanyName()
                    + ".\n" +
                    "Application ID: " + savedApp.getId() + "\n\n" +
                    "Best regards,\nJob Portal Team";

            String emailTo = user.getEmail();
            if (emailTo != null && !emailTo.isEmpty()) {
                emailService.sendSimpleMessage(emailTo, subject, body);
            }
        } catch (Exception e) {
            // Log but don't fail the application if email fails
            System.err.println("Failed to send application confirmation email: " + e.getMessage());
        }

        return savedApp;
    }

    @Override
    public JobApplication withdrawApplication(Long applicationId) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));

        app.setStatus("WITHDRAWN");
        return jobApplicationRepository.save(app);
    }

    @Override
    public List<JobApplication> getApplicationsByUser(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }

    @Override
    public List<JobApplication> getApplicationsByJob(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    @Override
    public JobApplication approveApplication(Long applicationId) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));

        app.setStatus("APPROVED");
        return jobApplicationRepository.save(app);
    }

    @Override
    public JobApplication rejectApplication(Long applicationId) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));

        app.setStatus("REJECTED");
        return jobApplicationRepository.save(app);
    }

    @Override
    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    @Override
    public JobApplication getApplicationById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
    }

    @Override
    public void deleteApplication(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new RuntimeException("Application not found with id: " + id);
        }
        jobApplicationRepository.deleteById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteAllApplications(Long userId) {
        jobApplicationRepository.deleteByUserId(userId);
    }

    @Override
    public long countApplicationsByRecruiter(Long recruiterId) {
        return jobApplicationRepository.countByJobRecruiterId(recruiterId);
    }

    @Override
    public List<JobApplication> getApplicationsByRecruiter(Long recruiterId) {
        return jobApplicationRepository.findByJobRecruiterId(recruiterId);
    }

    @Override
    public void updateStatus(Long applicationId, String status) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));
        app.setStatus(status);
        jobApplicationRepository.save(app);
    }
}
