package com.jobportal.service;

import com.jobportal.model.Education;
import com.jobportal.model.User;

import java.util.List;

public interface EducationService {

    void save(Education education);

    List<Education> getByUser(User user);

    void delete(Long id);
}
