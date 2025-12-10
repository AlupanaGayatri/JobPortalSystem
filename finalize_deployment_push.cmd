@echo off
echo ===========================================
echo   FINAL SAFETY PUSH (PERSIST DATA)
echo ===========================================
echo.

echo Adding application.properties...
git add src/main/resources/application.properties

echo Committing fix...
git commit -m "Config: Revert ddl-auto to update for data persistence"

echo Pushing to GitHub...
git push origin main

echo.
echo ===========================================
if %errorlevel% neq 0 (
    echo   ❌ PUSH FAILED!
) else (
    echo   ✅ PUSH SUCCESS!
    echo   Render will deploy the final safe version.
)
echo ===========================================
echo.
pause
