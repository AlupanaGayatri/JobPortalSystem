package com.jobportal.controller;

import com.jobportal.model.JobApplication;
import com.jobportal.model.User;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DbCleanupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @GetMapping("/cleanup/users")
    @Transactional
    public String cleanupUsers() {
        List<User> users = userRepository.findAll();
        Map<String, List<User>> grouped = users.stream().collect(Collectors.groupingBy(User::getEmail));
        StringBuilder sb = new StringBuilder();
        int deletedCount = 0;
        int keptCount = 0;

        sb.append("Starting Duplicate User Cleanup...\n\n");

        for (Map.Entry<String, List<User>> entry : grouped.entrySet()) {
            List<User> dups = entry.getValue();
            if (dups.size() > 1) {
                sb.append("Found ").append(dups.size()).append(" duplicates for email: ").append(entry.getKey())
                        .append("\n");

                // Sort by ID descending (Keep the newest/latest one, delete older ones)
                // Assuming newer one is the one currently being used or re-created after error.
                // Or maybe keep oldest?
                // Given "NonUniqueResultException", often the user tries to register/login and
                // creates a new one?
                // Let's keep the one with the HIGHEST ID (newest).
                dups.sort(Comparator.comparing(User::getId).reversed());

                User keeper = dups.get(0);
                sb.append("  [KEEPING] ID: ").append(keeper.getId()).append(" | Role: ").append(keeper.getRole())
                        .append("\n");
                keptCount++;

                for (int i = 1; i < dups.size(); i++) {
                    User toDelete = dups.get(i);
                    sb.append("  [DELETING] Duplicate ID: ").append(toDelete.getId()).append("\n");

                    // Delete applications for this user ID
                    try {
                        jobApplicationRepository.deleteByUserId(toDelete.getId());
                        sb.append("    - Deleted user's job applications.\n");
                    } catch (Exception e) {
                        sb.append("    - Error deleting applications: ").append(e.getMessage()).append("\n");
                    }

                    // Profile is cascaded by User entity, so we just delete User.
                    userRepository.delete(toDelete);
                    deletedCount++;
                }
                sb.append("\n");
            }
        }

        if (deletedCount == 0) {
            sb.append("No duplicate users found.\n");
        } else {
            sb.append("\nSummary: Deleted ").append(deletedCount).append(" duplicate users.");
        }

        return "<pre>" + sb.toString() + "</pre>";
    }
}
