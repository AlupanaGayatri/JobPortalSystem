package com.jobportal.service;

import com.jobportal.model.Skill;
import com.jobportal.model.User;

import java.util.List;

public interface SkillService {

    void save(Skill skill);

    List<Skill> getByUser(User user);

    void delete(Long id);
}
