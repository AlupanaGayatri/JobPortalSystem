@echo off
echo ===========================================
echo   FINAL FIX GITHUB PUSH
echo ===========================================
echo.

:: 1. Add all files (This was the missing step!)
echo Adding all files to Git...
git add .

:: 2. Commit (Save) files
echo Committing files...
git commit -m "Final ready for deployment"

:: 3. Set Branch to Main
echo Setting branch to main...
git branch -M main

:: 4. Set Remote URL
echo Setting remote...
git remote remove origin
git remote add origin https://github.com/AlupanaGayatri/JobPortalSystem.git

:: 5. PUSH
echo.
echo ===========================================
echo   STARTING UPLOAD...
echo   (Login if asked)
echo ===========================================
git push -u origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ UPLOAD FAILED!
) else (
    echo   ✅ UPLOAD SUCCESS!
    echo   Your code is now on GitHub.
)
echo ===========================================
echo.
pause
