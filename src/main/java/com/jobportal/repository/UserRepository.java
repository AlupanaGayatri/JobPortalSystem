package com.jobportal.repository;

import com.jobportal.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for User entity
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    java.util.List<User> findByRole(String role);
}
