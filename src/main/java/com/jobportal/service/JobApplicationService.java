package com.jobportal.service;

import java.util.List;

import com.jobportal.model.JobApplication;

public interface JobApplicationService {

    JobApplication applyForJob(Long userId, Long jobId, String resumeText);

    JobApplication withdrawApplication(Long applicationId);

    List<JobApplication> getApplicationsByUser(Long userId);

    List<JobApplication> getApplicationsByJob(Long jobId);

    JobApplication approveApplication(Long applicationId);

    JobApplication rejectApplication(Long applicationId);

    List<JobApplication> getAllApplications();

    JobApplication getApplicationById(Long id);

    void deleteApplication(Long id);

    void deleteAllApplications(Long userId);
}
