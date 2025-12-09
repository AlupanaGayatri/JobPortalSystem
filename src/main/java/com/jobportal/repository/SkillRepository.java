package com.jobportal.repository;

import com.jobportal.model.Skill;
import com.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUser(User user);
}
