# Setup Guide for Job Portal System

## Prerequisites

### 1. Set JAVA_HOME Environment Variable

**Windows (PowerShell):**
```powershell
# Find your Java installation
Get-ChildItem "C:\Program Files\Java" -Directory

# Set JAVA_HOME (replace with your actual JDK path)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify
java -version
```

**Windows (Command Prompt):**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
java -version
```

### 2. Compile the Project

```bash
cd f:\JobPortalSystem
.\mvnw.cmd clean compile
```

### 3. Run the Application

```bash
.\mvnw.cmd spring-boot:run
```

## Database Setup

Make sure MySQL is running and the database exists:

```sql
CREATE DATABASE IF NOT EXISTS job_portal;
```

## Environment Variables

Set these before running:

```bash
# Google OAuth (optional)
set GOOGLE_CLIENT_ID=your_client_id
set GOOGLE_CLIENT_SECRET=your_client_secret
```

## Testing the API

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"SecurePass123!\",\"fullName\":\"Test User\"}"
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"SecurePass123!\"}"
```

### 3. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/v1/jobs ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Next Steps

Once the application is running:
1. Test authentication endpoints
2. Verify rate limiting works
3. Check audit logs in database
4. Test password validation
5. Verify account lockout mechanism
