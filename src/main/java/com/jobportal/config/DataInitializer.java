package com.jobportal.config;

import com.jobportal.model.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(JobRepository jobRepository) {
        return args -> {
            // Only add sample jobs if database is empty
            if (jobRepository.count() == 0) {
                Job job1 = new Job();
                job1.setTitle("Senior Java Developer");
                job1.setCompanyName("Tech Solutions Inc");
                job1.setLocation("Bangalore, India");
                job1.setSalary("15-20 LPA");
                job1.setDescription(
                        "We are looking for an experienced Java developer with expertise in Spring Boot, Microservices, and cloud technologies. Join our dynamic team and work on cutting-edge projects.");
                job1.setSkillsRequired("Java, Spring Boot, MySQL, AWS");
                job1.setPostedDate(LocalDate.now());
                job1.setRecruiterId(1L);
                job1.setStatus("ACTIVE");
                jobRepository.save(job1);

                Job job2 = new Job();
                job2.setTitle("Full Stack Developer");
                job2.setCompanyName("Digital Innovations");
                job2.setLocation("Mumbai, India");
                job2.setSalary("12-18 LPA");
                job2.setDescription(
                        "Looking for a full stack developer proficient in React, Node.js, and modern web technologies. Great opportunity to work on innovative projects.");
                job2.setSkillsRequired("React, Node.js, MongoDB, JavaScript");
                job2.setPostedDate(LocalDate.now().minusDays(2));
                job2.setRecruiterId(1L);
                job2.setStatus("ACTIVE");
                jobRepository.save(job2);

                Job job3 = new Job();
                job3.setTitle("DevOps Engineer");
                job3.setCompanyName("Cloud Systems Ltd");
                job3.setLocation("Hyderabad, India");
                job3.setSalary("18-25 LPA");
                job3.setDescription(
                        "Seeking a DevOps engineer with strong experience in CI/CD, Docker, Kubernetes, and cloud platforms. Help us build scalable infrastructure.");
                job3.setSkillsRequired("Docker, Kubernetes, Jenkins, AWS");
                job3.setPostedDate(LocalDate.now().minusDays(5));
                job3.setRecruiterId(1L);
                job3.setStatus("ACTIVE");
                jobRepository.save(job3);

                System.out.println("✅ Sample jobs added to database!");
            }
        };
    }
}
