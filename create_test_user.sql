-- Create a test user for login
-- Password: Test@123 (BCrypt encoded)

INSERT INTO users (email, password, full_name, role, created_at, updated_at)
VALUES ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', 'Test User', 'USER', NOW(), NOW());
