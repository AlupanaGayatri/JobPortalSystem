-- ============================================
-- Reset Users Table - Clean Database Setup
-- ============================================
-- This script will:
-- 1. Delete all existing users
-- 2. Reset the auto-increment counter to 1
-- 3. Users will need to log in again to be recreated with IDs starting from 1
-- ============================================

-- Step 1: Delete all users
DELETE FROM users;

-- Step 2: Reset the auto-increment counter to 1
ALTER TABLE users AUTO_INCREMENT = 1;

-- Step 3: Verify the table is empty
SELECT 
    COUNT(*) as total_users,
    'Table is now empty and ready for fresh users' as status
FROM users;

-- Step 4: Show the table structure to confirm
SHOW CREATE TABLE users;
