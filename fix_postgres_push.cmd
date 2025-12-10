@echo off
echo ===========================================
echo   FIXING POSTGRES DIALECT (ATTEMPT 2)
echo ===========================================
echo.

echo Adding application.properties...
git add src/main/resources/application.properties

echo Committing fix...
git commit -m "Fix: Explicitly set PostgreSQLDialect"

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
