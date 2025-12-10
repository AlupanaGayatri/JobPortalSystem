@echo off
echo ===========================================
echo   MANUAL SCHEMA PUSH
echo ===========================================
echo.
echo Adding schema.sql...
git add src/main/resources/schema.sql
git add src/main/resources/application.properties

echo Committing fix...
git commit -m "Fix: Add schema.sql to manually create missing tables"

echo Pushing to GitHub...
git push origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ PUSH FAILED!
) else (
    echo   ✅ PUSH SUCCESS!
    echo   Render will build and run the SQL script.
)
echo ===========================================
echo.
pause
