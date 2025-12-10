@echo off
echo ===========================================
echo   FORCING SCHEMA REBUILD
echo ===========================================
echo.
echo We are forcing Render to rebuild the database schema.
echo This ensures the table 'job_profile' is created.
echo.

echo Adding application.properties...
git add src/main/resources/application.properties

echo Committing fix...
git commit -m "Fix: Force rebuild to ensure job_profile table creation"

echo Pushing to GitHub...
git push origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ PUSH FAILED!
) else (
    echo   ✅ PUSH SUCCESS!
    echo   Render will build this new version.
)
echo ===========================================
echo.
pause
