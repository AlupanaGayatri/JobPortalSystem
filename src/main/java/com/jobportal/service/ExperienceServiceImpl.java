package com.jobportal.service;

import com.jobportal.model.Experience;
import com.jobportal.model.User;
import com.jobportal.repository.ExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceRepository repo;

    @Override
    public void save(Experience e) {
        repo.save(e);
    }

    @Override
    public List<Experience> getByUser(User user) {
        return repo.findByUser(user);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
