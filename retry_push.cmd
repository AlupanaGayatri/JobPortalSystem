@echo off
echo ===========================================
echo   RETRYING GITHUB PUSH - ATTEMPT 3
echo ===========================================
echo.

:: 1. Force set the remote URL to be 100% sure
echo Setting remote to: https://github.com/AlupanaGayatri/JobPortalSystem.git
git remote remove origin
git remote add origin https://github.com/AlupanaGayatri/JobPortalSystem.git

:: 2. FORCE rename current branch to 'main'
echo Renaming current branch to 'main'...
git branch -M main

:: 3. Status check
echo.
echo Current Git Status:
git status
echo.

:: 4. PUSH
echo ===========================================
echo   STARTING UPLOAD...
echo   (A login window should pop up)
echo ===========================================
git push -u origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ UPLOAD FAILED!
    echo   Read the error message above.
) else (
    echo   ✅ UPLOAD SUCCESS!
    echo   Your code is now on GitHub.
)
echo ===========================================
echo.
pause
