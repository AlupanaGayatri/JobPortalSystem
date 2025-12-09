package com.jobportal.repository;

import com.jobportal.model.Experience;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByUser(User user);
}
