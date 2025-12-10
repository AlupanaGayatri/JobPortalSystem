@echo off
echo ===========================================
echo   STANDARDIZING PASSWORD ENCODING
echo ===========================================
echo.

echo Adding Modified Files...
git add src/main/java/com/jobportal/controller/UserController.java
git add src/main/java/com/jobportal/service/UserServiceImpl.java

echo Committing Refactor...
git commit -m "Refactor: Move password encoding from Controller to Service layer"

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
