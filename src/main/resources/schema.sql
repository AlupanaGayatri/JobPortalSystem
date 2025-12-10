-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    full_name VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255)
);

-- Create Job Profile Table
CREATE TABLE IF NOT EXISTS job_profile (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id),
    headline VARCHAR(255),
    summary VARCHAR(2000),
    phone VARCHAR(255),
    address VARCHAR(255),
    github_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    profile_photo VARCHAR(255),
    resume_file VARCHAR(255),
    experience INTEGER,
    skills_count INTEGER,
    job_current_role VARCHAR(255),
    date_of_birth DATE,
    gender VARCHAR(255),
    marital_status VARCHAR(255),
    permanent_address VARCHAR(255),
    hometown VARCHAR(255),
    current_industry VARCHAR(255),
    functional_area VARCHAR(255),
    role_category VARCHAR(255),
    desired_job_type VARCHAR(255),
    desired_employment_type VARCHAR(255),
    preferred_shift VARCHAR(255),
    expected_salary VARCHAR(255),
    desired_location VARCHAR(255)
);
