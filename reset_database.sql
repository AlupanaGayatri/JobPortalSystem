-- ============================================
-- Complete Database Reset - Safe User Deletion
-- ============================================
-- This script safely deletes all user data by removing
-- foreign key dependencies first, then resetting IDs
-- ============================================

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Step 1: Delete all related data (in order of dependencies)
DELETE FROM job_applications;
DELETE FROM education;
DELETE FROM experience;
DELETE FROM skills;
DELETE FROM profile;
DELETE FROM users;

-- Step 2: Reset auto-increment counters to 1
ALTER TABLE job_applications AUTO_INCREMENT = 1;
ALTER TABLE education AUTO_INCREMENT = 1;
ALTER TABLE experience AUTO_INCREMENT = 1;
ALTER TABLE skills AUTO_INCREMENT = 1;
ALTER TABLE profile AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Step 3: Verify all tables are empty
SELECT 'users' as table_name, COUNT(*) as total FROM users
UNION ALL
SELECT 'profile' as table_name, COUNT(*) as total FROM profile
UNION ALL
SELECT 'education' as table_name, COUNT(*) as total FROM education
UNION ALL
SELECT 'experience' as table_name, COUNT(*) as total FROM experience
UNION ALL
SELECT 'skills' as table_name, COUNT(*) as total FROM skills
UNION ALL
SELECT 'job_applications' as table_name, COUNT(*) as total FROM job_applications;

-- Success message
SELECT 'Database reset complete! All user data deleted and IDs reset to 1.' as status;
