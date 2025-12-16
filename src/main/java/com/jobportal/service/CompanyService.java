package com.jobportal.service;

import com.jobportal.model.Company;
import com.jobportal.model.User;
import com.jobportal.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Optional<Company> getCompanyByRecruiter(Long recruiterId) {
        return companyRepository.findByRecruiterId(recruiterId);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }
}
