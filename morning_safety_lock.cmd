@echo off
echo ===========================================
echo   GOOD MORNING! FINAL SAFETY LOCK
echo ===========================================
echo.
echo This script sets your database to "Update Mode".
echo This means your users and data will be SAVED
echo and NOT deleted when the app restarts.
echo.
echo Run this ONLY after you have verified the site works!
echo.
pause

echo Adding application.properties...
git add src/main/resources/application.properties

echo Committing persistence fix...
git commit -m "Final: Switch to update mode to persist data"

echo Pushing to GitHub...
git push origin main

echo.
echo ===========================================
echo   ✅ ALL DONE! SYSTEM IS STABLE.
echo ===========================================
echo.
pause
