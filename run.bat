@echo off
echo ========================================
echo Library Management System - Quick Start
echo ========================================
echo.

REM Check if MongoDB is running
echo [1/4] Checking MongoDB service...
sc query MongoDB | find "RUNNING" >nul
if %errorlevel% equ 0 (
    echo ✓ MongoDB is running
) else (
    echo ✗ MongoDB is not running
    echo Starting MongoDB service...
    net start MongoDB
    if %errorlevel% neq 0 (
        echo ✗ Failed to start MongoDB. Please start it manually.
        echo   Run: net start MongoDB
        pause
        exit /b 1
    )
)
echo.

REM Check if Maven is installed
echo [2/4] Checking Maven installation...
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Maven is installed
) else (
    echo ✗ Maven is not installed
    echo Please install Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo.

REM Build the project
echo [3/4] Building project with Maven...
call mvn clean compile
if %errorlevel% neq 0 (
    echo ✗ Build failed
    pause
    exit /b 1
)
echo ✓ Build successful
echo.

REM Run the application
echo [4/4] Starting Library Management System...
echo.
echo ========================================
echo.
call mvn exec:java -Dexec.mainClass="LibraryManagementSystem"

pause
