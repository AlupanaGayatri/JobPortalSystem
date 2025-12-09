@echo off
echo Pushing code to GitHub...
echo Repository: https://github.com/AlupanaGayatri/JobPortalSystem.git
echo.
echo You will be asked to sign in to GitHub in a browser window.
echo.

git push -u origin main

echo.
if %errorlevel% neq 0 (
    echo Push Failed! Please try again.
) else (
    echo Push Success! Your code is now on GitHub.
)
pause
