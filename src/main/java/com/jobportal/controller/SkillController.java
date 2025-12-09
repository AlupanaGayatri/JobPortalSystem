package com.jobportal.controller;

import com.jobportal.model.Skill;
import com.jobportal.model.User;
import com.jobportal.service.SkillService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String viewSkills(Model model, Principal principal) {
        User user = userService.findByPrincipal(principal);
        model.addAttribute("skills", skillService.getByUser(user));
        model.addAttribute("skill", new Skill()); // Add empty object for form
        return "skills";
    }

    @PostMapping("/add")
    public String addSkill(Skill skill, Principal principal) {
        User user = userService.findByPrincipal(principal);
        skill.setUser(user);
        skillService.save(skill);
        return "redirect:/skills";
    }

    @GetMapping("/delete/{id}")
    public String deleteSkill(@PathVariable Long id) {
        skillService.delete(id);
        return "redirect:/skills";
    }
}
