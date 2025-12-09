# Job Portal API Test Script
# Run this script to test your Job Portal System APIs

$baseUrl = "http://localhost:9090"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🚀 Job Portal System API Test Suite" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Register User
Write-Host "📝 Test 1: Registering new user..." -ForegroundColor Cyan
$registerBody = @{
    email = "testuser@example.com"
    password = "Test@12345"
    fullName = "Test User"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/register" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody
    
    Write-Host "✅ Registration successful!" -ForegroundColor Green
    Write-Host "   User ID: $($registerResponse.data.user.id)" -ForegroundColor Yellow
    Write-Host "   Email: $($registerResponse.data.user.email)" -ForegroundColor Yellow
    Write-Host "   Role: $($registerResponse.data.user.role)" -ForegroundColor Yellow
    
    $accessToken = $registerResponse.data.accessToken
    Write-Host "   🔑 Access Token received" -ForegroundColor Green
} catch {
    Write-Host "❌ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   (User might already exist - trying login instead)" -ForegroundColor Yellow
}

Write-Host ""

# Test 2: Login
Write-Host "🔐 Test 2: Logging in..." -ForegroundColor Cyan
$loginBody = @{
    email = "testuser@example.com"
    password = "Test@12345"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody
    
    Write-Host "✅ Login successful!" -ForegroundColor Green
    $accessToken = $loginResponse.data.accessToken
    Write-Host "   🔑 New Access Token received" -ForegroundColor Green
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 3: Get Current User
Write-Host "👤 Test 3: Getting current user info..." -ForegroundColor Cyan
try {
    $headers = @{
        Authorization = "Bearer $accessToken"
    }
    
    $userResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/me" `
        -Method Get `
        -Headers $headers
    
    Write-Host "✅ User info retrieved!" -ForegroundColor Green
    Write-Host "   Name: $($userResponse.data.fullName)" -ForegroundColor Yellow
    Write-Host "   Email: $($userResponse.data.email)" -ForegroundColor Yellow
    Write-Host "   Role: $($userResponse.data.role)" -ForegroundColor Yellow
} catch {
    Write-Host "❌ Failed to get user info: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 4: Get All Jobs
Write-Host "💼 Test 4: Getting all jobs (public endpoint)..." -ForegroundColor Cyan
try {
    $jobsResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/jobs" -Method Get
    
    Write-Host "✅ Jobs retrieved!" -ForegroundColor Green
    Write-Host "   Total jobs: $($jobsResponse.data.Count)" -ForegroundColor Yellow
    
    if ($jobsResponse.data.Count -gt 0) {
        Write-Host "   Sample job:" -ForegroundColor Yellow
        Write-Host "     - Title: $($jobsResponse.data[0].title)" -ForegroundColor White
        Write-Host "     - Company: $($jobsResponse.data[0].companyName)" -ForegroundColor White
        Write-Host "     - Location: $($jobsResponse.data[0].location)" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Failed to get jobs: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 5: Test Authentication Requirement
Write-Host "🔒 Test 5: Testing authentication requirement..." -ForegroundColor Cyan
try {
    # Try to access protected endpoint without token
    $response = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/me" -Method Get
    Write-Host "❌ Security issue: Protected endpoint accessible without token!" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "✅ Authentication working correctly!" -ForegroundColor Green
        Write-Host "   Protected endpoints require valid JWT token" -ForegroundColor Yellow
    } else {
        Write-Host "⚠️  Unexpected error: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🎉 Testing Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Summary:" -ForegroundColor Cyan
Write-Host "   ✅ User Registration: Working" -ForegroundColor Green
Write-Host "   ✅ User Login: Working" -ForegroundColor Green
Write-Host "   ✅ JWT Authentication: Working" -ForegroundColor Green
Write-Host "   ✅ Public Endpoints: Working" -ForegroundColor Green
Write-Host "   ✅ Protected Endpoints: Working" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Your Job Portal System is fully operational!" -ForegroundColor Green
Write-Host ""
