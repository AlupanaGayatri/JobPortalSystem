package com.jobportal.config;

import com.jobportal.model.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

// @Component
public class DataSeeder implements CommandLineRunner {

        private final JobRepository jobRepository;

        public DataSeeder(JobRepository jobRepository) {
                this.jobRepository = jobRepository;
        }

        @Override
        public void run(String... args) throws Exception {
                // 1. Backfill existing jobs with missing fields
                List<Job> allJobs = jobRepository.findAll();
                boolean dataUpdated = false;

                for (Job job : allJobs) {
                        boolean changed = false;
                        // Ensure fields are definitely not null options
                        if (job.getJobType() == null || job.getJobType().isEmpty()) {
                                job.setJobType("Full-time");
                                changed = true;
                        }
                        if (job.getExperienceLevel() == null || job.getExperienceLevel().isEmpty()) {
                                job.setExperienceLevel("1-3 Years");
                                changed = true;
                        }
                        if (job.getMinSalary() == null && job.getMaxSalary() == null) {
                                // If we have an old string salary, we might parse it, but for safety, default
                                // to a range
                                job.setMinSalary(400000.0);
                                job.setMaxSalary(800000.0);
                                changed = true;
                        }

                        if (changed) {
                                jobRepository.save(job);
                                dataUpdated = true;
                        }
                }

                if (dataUpdated) {
                        System.out.println("Updated existing jobs with default professional fields.");
                }

                // 2. See if we need more data (if count is low)
                if (jobRepository.count() < 30) {
                        System.out.println("Seeding database with diverse Indian job market data...");

                        String[] commonSkills = { "Java", "Python", "Communication", "Sales", "Accounting", "React",
                                        "AWS", "Marketing" };

                        // IT Roles
                        createJob("Java Full Stack Developer", "TechSolutions India", "Hyderabad", "ACTIVE",
                                        "3-5 Years", "Full-time", 600000.0, 1200000.0, "Java, Spring Boot, React, SQL");
                        createJob("Frontend Developer (React)", "InnoMinds", "Bengaluru", "ACTIVE", "1-3 Years",
                                        "Full-time", 500000.0, 1000000.0, "React, Redux, HTML, CSS");
                        createJob("Data Scientist", "Analytics Pro", "Gurugram", "ACTIVE", "3-5 Years", "Full-time",
                                        1200000.0, 2000000.0, "Python, ML, SQL, Pandas");
                        createJob("DevOps Engineer", "Cloud Systems", "Pune", "ACTIVE", "3-5 Years", "Full-time",
                                        800000.0, 1600000.0, "AWS, Docker, Kubernetes, Jenkins");
                        createJob("Python Developer", "Django Boys", "Chennai", "ACTIVE", "0-1 Years", "Full-time",
                                        400000.0, 800000.0, "Python, Django, REST API");
                        createJob("Android Developer", "AppWiz", "Noida", "ACTIVE", "1-3 Years", "Contract", 600000.0,
                                        1100000.0, "Kotlin, Android SDK, API Integration");
                        createJob("Software Intern", "StartUp Hub", "Mumbai", "ACTIVE", "Fresher", "Internship",
                                        120000.0, 240000.0, "Java, Basic HTML, Problem Solving");

                        // Non-IT Roles
                        createJob("Digital Marketing Manager", "Growth Hackers", "Mumbai", "ACTIVE", "3-5 Years",
                                        "Full-time", 500000.0, 900000.0, "SEO, SEM, Google Ads, Content Marketing");
                        createJob("HR Executive", "PeopleFirst", "Delhi NCR", "ACTIVE", "1-3 Years", "Full-time",
                                        300000.0, 500000.0, "Recruitment, Payroll, Employee Relations");
                        createJob("Sales Associate", "Retail Giants", "Kolkata", "ACTIVE", "0-1 Years", "Full-time",
                                        250000.0, 450000.0, "Sales, Communication, Negotiation");
                        createJob("Business Analyst", "FinCorp", "Mumbai", "ACTIVE", "3-5 Years", "Full-time", 700000.0,
                                        1300000.0, "Analysis, SQL, Excel, Requirement Gathering");
                        createJob("Content Writer", "Media House", "Remote", "ACTIVE", "1-3 Years", "Freelance",
                                        300000.0, 600000.0, "Writing, Editing, SEO, Blogging");
                        createJob("Graphic Designer", "Creative Studio", "Pune", "ACTIVE", "0-1 Years", "Full-time",
                                        350000.0, 650000.0, "Photoshop, Illustrator, Figma");
                        createJob("Accountant", "Global Finance", "Ahmedabad", "ACTIVE", "3-5 Years", "Full-time",
                                        400000.0, 700000.0, "Tally, GST, Excel, Accounting");

                        // Random generation for scale across India
                        String[] locations = {
                                        "Mumbai", "Delhi", "Bengaluru", "Hyderabad", "Chennai", "Kolkata", "Pune",
                                        "Ahmedabad", "Jaipur", "Surat",
                                        "Lucknow", "Kanpur", "Nagpur", "Indore", "Thane", "Bhopal", "Visakhapatnam",
                                        "Patna", "Vadodara", "Ghaziabad",
                                        "Ludhiana", "Agra", "Nashik", "Ranchi", "Faridabad", "Meerut", "Rajkot",
                                        "Varanasi", "Srinagar", "Aurangabad",
                                        "Dhanbad", "Amritsar", "Navi Mumbai", "Coimbatore", "Vijayawada", "Jodhpur",
                                        "Madurai", "Raipur", "Kota",
                                        "Chandigarh", "Guwahati", "Solapur", "Hubli", "Mysuru", "Gurugram", "Noida",
                                        "Kochi"
                        };
                        String[] types = { "Full-time", "Part-time", "Contract", "Internship", "Freelance" };
                        String[] exps = { "Fresher", "0-1 Years", "1-3 Years", "3-5 Years", "5-8 Years", "8-12 Years",
                                        "12+ Years" };

                        for (int i = 0; i < 20; i++) {
                                String loc = locations[(int) (Math.random() * locations.length)];
                                String type = types[(int) (Math.random() * types.length)];
                                String exp = exps[(int) (Math.random() * exps.length)];
                                createJob("Operations Associate", "PanIndia Ops", loc, "ACTIVE", exp, type, 200000.0,
                                                500000.0, "Operations, Excel, Coordination");
                        }

                        System.out.println("Seeding completed.");
                }
        }

        private Job createJob(String title, String company, String location, String status, String expLevel,
                        String type,
                        Double min, Double max, String skills) {
                Job job = new Job();
                job.setTitle(title);
                job.setCompanyName(company);
                job.setLocation(location);
                job.setStatus(status);
                job.setExperienceLevel(expLevel);
                job.setJobType(type);
                job.setMinSalary(min);
                job.setMaxSalary(max);
                job.setPostedDate(LocalDate.now().minusDays((long) (Math.random() * 60)));
                job.setDescription("Exciting opportunity for a " + title + " at " + company
                                + ". You will be responsible for key deliverables in " + title + " domain.");
                job.setSkillsRequired(skills != null ? skills : "Java, Communication, SQL");
                job.setRecruiterId(1L);
                return jobRepository.save(job);
        }
}
