@echo off
echo ===========================================
echo   ADDING PASSWORD EYE ICON
echo ===========================================
echo.

echo Adding login.html...
git add src/main/resources/templates/login.html

echo Adding register.html...
git add src/main/resources/templates/register.html

echo Committing fix...
git commit -m "UI: Add eye icon to toggle password visibility"

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
