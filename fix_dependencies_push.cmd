@echo off
echo ===========================================
echo   FIXING TABLE GENERATION & DEPENDENCIES
echo ===========================================
echo.

echo Adding pom.xml...
git add pom.xml

echo Adding Profile.java...
git add src/main/java/com/jobportal/model/Profile.java

echo Committing fix...
git commit -m "Fix: Remove Flyway and rename profile table to job_profile"

echo Pushing to GitHub...
git push origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ PUSH FAILED!
) else (
    echo   ✅ PUSH SUCCESS!
    echo   Render will build again.
)
echo ===========================================
echo.
pause
