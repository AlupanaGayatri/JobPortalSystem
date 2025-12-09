# Setup JAVA_HOME for Job Portal System
# Run this script in PowerShell: .\setup-java.ps1

Write-Host "Setting up JAVA_HOME..." -ForegroundColor Green

# Common Java installation paths
$javaPaths = @(
    "C:\Program Files\Java\jdk-21",
    "C:\Program Files\Java\jdk-17",
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.7-hotspot",
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot"
)

# Find Java installation
$javaHome = $null
foreach ($path in $javaPaths) {
    if (Test-Path $path) {
        $javaHome = $path
        Write-Host "Found Java at: $javaHome" -ForegroundColor Cyan
        break
    }
}

if ($null -eq $javaHome) {
    Write-Host "Java not found! Please install Java 21 or 17" -ForegroundColor Red
    Write-Host "Download from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Set environment variables for current session
$env:JAVA_HOME = $javaHome
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "`nJAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green
Write-Host "`nVerifying Java installation..." -ForegroundColor Yellow

# Verify
java -version

Write-Host "`n✅ Java setup complete!" -ForegroundColor Green
Write-Host "`nYou can now run: .\mvnw.cmd spring-boot:run" -ForegroundColor Cyan
