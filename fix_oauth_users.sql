-- Fix existing OAuth users by setting their role to 'USER'
UPDATE users 
SET role = 'USER' 
WHERE role IS NULL AND password = '';

-- Verify the update
SELECT id, email, full_name, role, 
       CASE WHEN password = '' THEN 'OAuth User' ELSE 'Regular User' END as user_type
FROM users
ORDER BY id;
