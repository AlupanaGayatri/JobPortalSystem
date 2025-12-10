package com.jobportal.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RenderDatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String dbUrl = System.getenv("DATABASE_URL");

        if (dbUrl != null && !dbUrl.isEmpty()) {
            try {
                URI dbUri = new URI(dbUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                int port = dbUri.getPort();
                if (port == -1) {
                    port = 5432;
                }
                String dbUrlJdbc = "jdbc:postgresql://" + dbUri.getHost() + ':' + port + dbUri.getPath();

                return DataSourceBuilder.create()
                        .url(dbUrlJdbc)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to parse DATABASE_URL: " + dbUrl, e);
            }
        } else {
            // Fallback for Local Development (PostgreSQL)
            return DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/job_portal_db")
                    .username("postgres")
                    .password("postgres")
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }
    }
}
