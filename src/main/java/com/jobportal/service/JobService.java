package com.jobportal.service;

import com.jobportal.model.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepo;

    public JobService(JobRepository jobRepo) {
        this.jobRepo = jobRepo;
    }

    public Job postJob(Job job) {
        job.setPostedDate(LocalDate.now());
        return jobRepo.save(job);
    }

    public Job updateJob(Long id, Job job) {
        return jobRepo.findById(id).map(existing -> {
            existing.setTitle(job.getTitle());
            existing.setCompanyName(job.getCompanyName());
            existing.setLocation(job.getLocation());
            existing.setSalary(job.getSalary());
            existing.setDescription(job.getDescription());
            existing.setSkillsRequired(job.getSkillsRequired());
            return jobRepo.save(existing);
        }).orElse(null);
    }

    public boolean deleteJob(Long id) {
        if (!jobRepo.existsById(id))
            return false;
        jobRepo.deleteById(id);
        return true;
    }

    public Job getById(Long id) {
        return jobRepo.findById(id).orElse(null);
    }

    public List<Job> getAll() {
        return jobRepo.findAll();
    }

    public org.springframework.data.domain.Page<Job> searchJobs(
            String status,
            String title,
            String location,
            String experienceLevel,
            String jobType,
            Double minSalary,
            Double maxSalary,
            org.springframework.data.domain.Pageable pageable) {

        // Normalize strings for case-insensitive search to avoid database-specific
        // lower() issues on null parameters
        if (title != null && !title.isBlank()) {
            title = "%" + title.toLowerCase() + "%";
        } else {
            title = null;
        }

        if (location != null && !location.isBlank()) {
            location = "%" + location.toLowerCase() + "%";
        } else {
            location = null;
        }

        return jobRepo.findByFilters(status, title, location, experienceLevel, jobType, minSalary, maxSalary, pageable);
    }

    public List<Job> search(String q) {
        if (q == null || q.isBlank())
            return jobRepo.findAll();
        return jobRepo.findByTitleContainingIgnoreCase(q);
    }

    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return jobRepo.findByRecruiterId(recruiterId);
    }
}
