-- Create the job_portal database
CREATE DATABASE IF NOT EXISTS job_portal
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE job_portal;

-- Verify database is created
SELECT DATABASE() as current_database;
