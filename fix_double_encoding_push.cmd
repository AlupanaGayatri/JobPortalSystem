@echo off
echo ===========================================
echo   FIXING DOUBLE ENCODING
echo ===========================================
echo.

echo Adding UserServiceImpl.java...
git add src/main/java/com/jobportal/service/UserServiceImpl.java

echo Committing fix...
git commit -m "Fix: Remove double password encoding (handled by Controller)"

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
