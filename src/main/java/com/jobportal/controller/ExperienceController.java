package com.jobportal.controller;

import com.jobportal.model.Experience;
import com.jobportal.model.User;
import com.jobportal.service.ExperienceService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/experience")
public class ExperienceController {

    @Autowired
    private ExperienceService expService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewExp(Model model, Principal principal) {
        User user = userService.findByPrincipal(principal);
        model.addAttribute("experienceList", expService.getByUser(user));
        model.addAttribute("experience", new Experience()); // Add empty object for form
        return "experience";
    }

    @PostMapping("/add")
    public String addExp(Experience exp, Principal principal) {
        User user = userService.findByPrincipal(principal);
        exp.setUser(user);
        expService.save(exp);
        return "redirect:/experience";
    }

    @GetMapping("/delete/{id}")
    public String deleteExp(@PathVariable Long id) {
        expService.delete(id);
        return "redirect:/experience";
    }
}
