-- Verify all users in the database
SELECT 
    id,
    email,
    full_name,
    role,
    CASE 
        WHEN password = '' THEN 'OAuth User'
        WHEN password IS NULL THEN 'No Password'
        ELSE 'Regular User'
    END as user_type,
    CASE 
        WHEN role IS NULL THEN '❌ Missing Role'
        ELSE '✅ Has Role'
    END as role_status
FROM users
ORDER BY id;

-- Count users by type
SELECT 
    CASE 
        WHEN password = '' THEN 'OAuth Users'
        WHEN password IS NULL THEN 'Users without Password'
        ELSE 'Regular Users'
    END as user_category,
    COUNT(*) as count,
    GROUP_CONCAT(DISTINCT role) as roles
FROM users
GROUP BY user_category;
