package com.jobportal.repository;

import com.jobportal.model.Profile;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for Profile entity with caching
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Cacheable(value = "profiles", key = "#userId")
    Optional<Profile> findByUserId(Long userId);

    @Override
    @CacheEvict(value = "profiles", key = "#entity.userId")
    <S extends Profile> S save(S entity);
}
