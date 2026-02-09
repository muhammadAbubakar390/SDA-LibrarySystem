@echo off
echo ========================================
echo MongoDB Compass Connection Setup
echo ========================================
echo.

echo [Step 1/3] Checking MongoDB service...
sc query MongoDB | find "RUNNING" >nul
if %errorlevel% equ 0 (
    echo ✓ MongoDB is running
) else (
    echo ✗ MongoDB is not running
    echo Starting MongoDB...
    net start MongoDB
    if %errorlevel% neq 0 (
        echo ✗ Failed to start MongoDB
        echo Please start MongoDB manually
        pause
        exit /b 1
    )
)
echo.

echo [Step 2/3] MongoDB is ready!
echo.
echo Connection Details:
echo   Host: localhost
echo   Port: 27017
echo   Database: library_management_db
echo.

echo [Step 3/3] Instructions for MongoDB Compass:
echo.
echo 1. Open MongoDB Compass
echo 2. Use connection string: mongodb://localhost:27017
echo 3. Click "Connect"
echo 4. Look for database: library_management_db
echo.
echo NOTE: The database will be created when you first run the application.
echo.

echo ========================================
echo Next Steps:
echo ========================================
echo.
echo To create the database, you need to:
echo.
echo Option 1: Install Maven and run:
echo   mvn clean compile
echo   mvn exec:java -Dexec.mainClass="LibraryManagementSystem"
echo.
echo Option 2: Download MongoDB Java Driver JARs manually
echo   See: MANUAL_SETUP.md for instructions
echo.

pause
