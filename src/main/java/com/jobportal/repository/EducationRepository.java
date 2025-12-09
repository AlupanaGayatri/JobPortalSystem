package com.jobportal.repository;

import com.jobportal.model.Education;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUser(User user);
}
