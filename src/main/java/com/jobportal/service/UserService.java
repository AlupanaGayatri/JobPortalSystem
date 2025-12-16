package com.jobportal.service;

import com.jobportal.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    User findByEmail(String email);

    // Merged from Namitha's module
    User registerUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    List<User> getUsersByRole(String role);

    User findByPrincipal(java.security.Principal principal);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changeUserPassword(User user, String password);

    // OTP Methods
    String generateOTP(User user);

    String validateOTP(String email, String otp);
}
