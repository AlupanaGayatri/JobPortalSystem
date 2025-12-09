package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String homePage(Model model, Principal principal) {

        User loggedInUser = userService.findByPrincipal(principal);
        model.addAttribute("user", loggedInUser);

        return "home"; // dashboard.html
    }
}
