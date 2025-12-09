# Swagger/OpenAPI API Documentation

## Access Swagger UI

Once the application is running, access the interactive API documentation at:

**Swagger UI**: http://localhost:8080/swagger-ui.html  
**OpenAPI JSON**: http://localhost:8080/api-docs

## Features

### Interactive Testing
- Try out all API endpoints directly from the browser
- Automatic request/response examples
- JWT authentication support

### Documentation
- Complete API reference
- Request/response schemas
- Authentication requirements
- Error responses

## Using Swagger UI

### 1. Authenticate

1. Click on **POST /api/v1/auth/login**
2. Click "Try it out"
3. Enter credentials:
```json
{
  "email": "test@example.com",
  "password": "SecurePass123!"
}
```
4. Click "Execute"
5. Copy the `accessToken` from the response

### 2. Authorize

1. Click the **"Authorize"** button at the top
2. Enter: `Bearer YOUR_ACCESS_TOKEN`
3. Click "Authorize"
4. Click "Close"

### 3. Test Protected Endpoints

Now you can test any protected endpoint:
- GET /api/v1/users
- GET /api/v1/jobs
- POST /api/v1/applications
- etc.

## API Groups

### Authentication
- POST /api/v1/auth/register - Register new user
- POST /api/v1/auth/login - Login
- POST /api/v1/auth/refresh - Refresh token
- GET /api/v1/auth/me - Get current user

### Users
- GET /api/v1/users - List users
- GET /api/v1/users/{id} - Get user
- POST /api/v1/users - Create user
- PUT /api/v1/users/{id} - Update user
- DELETE /api/v1/users/{id} - Delete user

### Jobs
- GET /api/v1/jobs - List jobs
- GET /api/v1/jobs/{id} - Get job
- POST /api/v1/jobs - Create job
- PUT /api/v1/jobs/{id} - Update job
- DELETE /api/v1/jobs/{id} - Delete job
- GET /api/v1/jobs/search - Search jobs
- GET /api/v1/jobs/active - Get active jobs

### Profiles
- GET /api/v1/profiles/me - Get my profile
- GET /api/v1/profiles/user/{userId} - Get user profile
- POST /api/v1/profiles - Create/update profile
- PUT /api/v1/profiles/{id} - Update profile
- DELETE /api/v1/profiles/{id} - Delete profile

### Applications
- GET /api/v1/applications - List all applications
- GET /api/v1/applications/my - My applications
- GET /api/v1/applications/job/{jobId} - Applications by job
- POST /api/v1/applications - Submit application
- PATCH /api/v1/applications/{id}/status - Update status
- DELETE /api/v1/applications/{id} - Withdraw application

## Security

All endpoints (except /auth/*) require JWT authentication:
- Include header: `Authorization: Bearer YOUR_ACCESS_TOKEN`
- Tokens expire after 24 hours
- Use refresh token to get new access token

## Rate Limiting

- Limit: 100 requests per minute per IP
- Applies to all /api/v1/* endpoints
- Returns 429 Too Many Requests if exceeded
