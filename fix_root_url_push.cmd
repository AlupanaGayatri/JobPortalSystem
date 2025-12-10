@echo off
echo ===========================================
echo   FIXING ROOT URL MAPPING
echo ===========================================
echo.

echo Adding HomeController...
git add src/main/java/com/jobportal/controller/HomeController.java

echo Committing fix...
git commit -m "Fix: Map root URL / to redirect to /login"

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
