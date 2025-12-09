package com.jobportal.service;

import com.jobportal.model.Experience;
import com.jobportal.model.User;

import java.util.List;

public interface ExperienceService {

    void save(Experience experience);

    List<Experience> getByUser(User user);

    void delete(Long id);
}
