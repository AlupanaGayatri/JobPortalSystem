package com.jobportal.service;

import com.jobportal.model.Skill;
import com.jobportal.model.User;
import com.jobportal.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository repo;

    @Override
    public void save(Skill skill) {
        repo.save(skill);
    }

    @Override
    public List<Skill> getByUser(User user) {
        return repo.findByUser(user);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
