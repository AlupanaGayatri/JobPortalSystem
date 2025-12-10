@echo off
echo ===========================================
echo   FIXING DATABASE DIALECT & PUSHING
echo ===========================================
echo.

echo Adding application.properties...
git add src/main/resources/application.properties

echo Committing fix...
git commit -m "Fix: Remove hardcoded MySQL dialect for Render PostgreSQL"

echo Pushing to GitHub...
git push origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ PUSH FAILED!
) else (
    echo   ✅ PUSH SUCCESS!
    echo   Render should auto-deploy this fix.
)
echo ===========================================
echo.
pause
