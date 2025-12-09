package com.jobportal.security;

import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("=== OAuth2 Login START ===");
        System.out.println("Registration ID: " + registrationId);
        System.out.println("Attributes: " + oauth2User.getAttributes());

        // Extract email and name from OAuth provider (Google)
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        System.out.println("Extracted email: " + email);
        System.out.println("Extracted name: " + name);

        // Create user if not exists
        if (email != null) {
            System.out.println("Checking if user exists in database...");
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                System.out.println("User NOT found - Creating new user...");
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(name != null ? name : "User");
                newUser.setPassword(""); // No password for OAuth users
                newUser.setRole("USER"); // Set default role for OAuth users

                User savedUser = userRepository.save(newUser);
                System.out.println("User saved with ID: " + savedUser.getId());
            } else {
                System.out.println("User already exists with ID: " + userOptional.get().getId());
            }
        } else {
            System.out.println("ERROR: Email is NULL!");
        }

        System.out.println("=== OAuth2 Login END ===");
        return oauth2User;
    }
}
