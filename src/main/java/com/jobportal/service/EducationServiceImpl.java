package com.jobportal.service;

import com.jobportal.model.Education;
import com.jobportal.model.User;
import com.jobportal.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationServiceImpl implements EducationService {

    @Autowired
    private EducationRepository repo;

    @Override
    public void save(Education e) {
        repo.save(e);
    }

    @Override
    public List<Education> getByUser(User user) {
        return repo.findByUser(user);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
