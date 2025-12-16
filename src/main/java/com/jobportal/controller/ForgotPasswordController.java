package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.EmailService;
import com.jobportal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // 1. Show Forgot Password Form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // 2. Process Email Submission (Generate OTP)
    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, @RequestParam("email") String userEmail,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            redirectAttributes.addFlashAttribute("info", "If an account exists, an OTP has been sent.");
            return "redirect:/forgot-password";
        }

        // Generate OTP
        String otp = userService.generateOTP(user);

        // DEBUG: Print OTP to console
        System.out.println("==================================================");
        System.out.println("OTP FOR " + userEmail + ": " + otp);
        System.out.println("==================================================");

        // Send Email (Async)
        new Thread(() -> {
            try {
                emailService.sendSimpleMessage(user.getEmail(), "Password Reset OTP",
                        "Your OTP for password reset is: " + otp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        redirectAttributes.addFlashAttribute("success", "OTP sent to your email.");
        return "redirect:/verify-otp?email=" + userEmail;
    }

    // 3. Show Verify OTP Form
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp";
    }

    // 4. Process OTP Verification
    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp,
            RedirectAttributes redirectAttributes) {

        String sessionToken = userService.validateOTP(email, otp);

        if (sessionToken != null) {
            return "redirect:/reset-password?token=" + sessionToken;
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or Expired OTP.");
            return "redirect:/verify-otp?email=" + email;
        }
    }

    // 5. Show Reset Password Form (Validate Token)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model,
            RedirectAttributes redirectAttributes) {
        String result = userService.validatePasswordResetToken(token);
        if (result != null) {
            String message = (result.equals("expired")) ? "Session expired." : "Invalid session.";
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // 6. Process Password Reset
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changeUserPassword(user.get(), password);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully. Please login.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid session.");
            return "redirect:/login";
        }
    }
}
