package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @GetMapping({ "/register", "/register.html" })
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        userService.save(user);
        return "redirect:/login?success";
    }

    // ❌ REMOVED loginPage() mapping
    // It exists already in LoginController

    // ❌ REMOVED dashboard() mapping
    // It exists already in DashboardController
}
