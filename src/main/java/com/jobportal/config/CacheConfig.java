package com.jobportal.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration using Caffeine
 * Provides in-memory caching for frequently accessed data
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String USERS_CACHE = "users";
    public static final String JOBS_CACHE = "jobs";
    public static final String PROFILES_CACHE = "profiles";
    public static final String APPLICATIONS_CACHE = "applications";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                USERS_CACHE,
                JOBS_CACHE,
                PROFILES_CACHE,
                APPLICATIONS_CACHE);

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // Cache expires after 10 minutes
                .maximumSize(1000) // Max 1000 entries per cache
                .recordStats(); // Enable statistics
    }
}
