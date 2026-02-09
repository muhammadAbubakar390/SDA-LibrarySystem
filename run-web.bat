@echo off
echo ========================================
echo 🌐 Starting Library Web Server...
echo ========================================
echo.
echo [1/2] Compiling web components...
javac -cp ".;lib/*" LibraryHttpServer.java MongoDBManager.java LibraryManagementSystem.java

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo.
echo [2/2] Launching server...
echo.
echo 🌍 Server URL: http://localhost:8080/index.html
echo using browser...
start http://localhost:8080/index.html

java -cp ".;lib/*" LibraryHttpServer
pause
