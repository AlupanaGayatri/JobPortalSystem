package com.jobportal.config;

import com.github.javafaker.Faker;
import com.jobportal.model.Job;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// @Component
public class BulkDataSeeder implements CommandLineRunner {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final Faker faker;
    private final Random random;

    // Artifact path for CSV dump
    private static final String CSV_FILE_PATH = "C:\\Users\\gayat\\.gemini\\antigravity\\brain\\dc90ad24-5e07-4f89-b5c9-728259817acf\\job_dump.csv";

    public BulkDataSeeder(JobRepository jobRepository, JobApplicationRepository jobApplicationRepository) {
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.faker = new Faker();
        this.random = new Random();
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if we need to seed data
        // If data exists, we assume it's good (since the clean-up run has passed).
        if (jobRepository.count() > 0) {
            System.out.println("Existing data found (" + jobRepository.count() + " jobs). Skipping bulk seed.");
            return;
        }

        System.out.println("Starting bulk data seeding (adding 10,000 jobs)... this may take a moment.");

        List<Job> batch = new ArrayList<>();
        int totalToGenerate = 10000;
        int batchSize = 1000;

        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE_PATH, true))) { // Append mode
            if (jobRepository.count() == 0) {
                writer.println(
                        "ID,Title,Company,Location,Status,JobType,ExperienceLevel,MinSalary,MaxSalary,PostedDate,Skills");
            }

            for (int i = 0; i < totalToGenerate; i++) {
                Job job = generateRandomJob();
                batch.add(job);

                writer.printf("%s,\"%s\",\"%s\",\"%s\",%s,%s,%s,%.2f,%.2f,%s,\"%s\"%n",
                        "Generated",
                        job.getTitle(),
                        job.getCompanyName(),
                        job.getLocation(),
                        job.getStatus(),
                        job.getJobType(),
                        job.getExperienceLevel(),
                        job.getMinSalary(),
                        job.getMaxSalary(),
                        job.getPostedDate(),
                        job.getSkillsRequired().replace("\"", "\"\""));

                if (batch.size() >= batchSize) {
                    jobRepository.saveAll(batch);
                    batch.clear();
                    System.out.println("Seeded " + (i + 1) + " jobs...");
                }
            }

            if (!batch.isEmpty()) {
                jobRepository.saveAll(batch);
            }

            System.out.println("Bulk seeding completed! Data dump saved to: " + CSV_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error writing CSV dump: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Job generateRandomJob() {
        Job job = new Job();

        String title = faker.job().title();
        if (random.nextBoolean()) {
            title = faker.job().seniority() + " " + title;
        }

        job.setTitle(title);
        job.setCompanyName(faker.company().name());

        // Critical Fix: Add Country to location for better search (e.g., "London, UK")
        String city = faker.address().city();
        String country = faker.address().countryCode();
        if ("UK".equalsIgnoreCase(country) || "GB".equalsIgnoreCase(country))
            country = "UK";
        else
            country = faker.address().country();

        job.setLocation(city + ", " + country);

        job.setStatus("ACTIVE");

        String[] types = { "Full-time", "Part-time", "Contract", "Freelance", "Remote" };
        job.setJobType(types[random.nextInt(types.length)]);

        String[] exps = { "Fresher", "0-1 Years", "1-3 Years", "3-5 Years", "5-10 Years", "10+ Years" };
        job.setExperienceLevel(exps[random.nextInt(exps.length)]);

        double baseSalary = 30000 + random.nextInt(100000);
        if (job.getExperienceLevel().contains("10+"))
            baseSalary *= 4;
        else if (job.getExperienceLevel().contains("5-10"))
            baseSalary *= 2.5;
        else if (job.getExperienceLevel().contains("3-5"))
            baseSalary *= 1.8;

        job.setMinSalary(baseSalary);
        job.setMaxSalary(baseSalary * (1.2 + random.nextDouble()));

        java.util.Date date = faker.date().past(90, TimeUnit.DAYS);
        job.setPostedDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        // Generate English description instead of Latin Lorem Ipsum
        String description = "We are seeking a talented " + title + " to join our dynamic team. " +
                "You will be responsible for " + faker.company().bs() + " and helping us "
                + faker.company().catchPhrase() + ". " +
                "This is an exciting opportunity to work in a fast-paced environment.";
        job.setDescription(description);

        job.setSkillsRequired(String.join(", ", generateSkills()));
        job.setRecruiterId(1L);

        return job;
    }

    private List<String> generateSkills() {
        String[] pool = {
                "Java", "Python", "React", "Angular", "Node.js", "AWS", "Docker", "Kubernetes",
                "SQL", "NoSQL", "MongoDB", "Spring Boot", "C++", "C#", ".NET", "Go", "Rust",
                "Machine Learning", "AI", "Data Science", "Excel", "Communication", "Sales",
                "Marketing", "SEO", "Content Writing", "Design", "Figma", "Photoshop"
        };

        int count = 3 + random.nextInt(5);
        List<String> skills = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String skill = pool[random.nextInt(pool.length)];
            if (!skills.contains(skill)) {
                skills.add(skill);
            }
        }
        return skills;
    }
}
