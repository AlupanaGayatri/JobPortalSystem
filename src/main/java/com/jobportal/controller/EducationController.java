package com.jobportal.controller;

import com.jobportal.model.Education;
import com.jobportal.model.User;
import com.jobportal.service.EducationService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/education")
public class EducationController {

    @Autowired
    private EducationService eduService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewEducation(Model model, Principal principal) {
        User user = userService.findByPrincipal(principal);
        model.addAttribute("educationList", eduService.getByUser(user));
        model.addAttribute("education", new Education()); // Add empty object for form
        return "education";
    }

    @PostMapping("/add")
    public String addEducation(Education edu, Principal principal) {
        User user = userService.findByPrincipal(principal);
        edu.setUser(user);
        eduService.save(edu);
        return "redirect:/education";
    }

    @GetMapping("/delete/{id}")
    public String deleteEdu(@PathVariable Long id) {
        eduService.delete(id);
        return "redirect:/education";
    }
}
