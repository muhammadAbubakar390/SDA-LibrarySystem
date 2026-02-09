# Quick Setup Guide - Library Management System with MongoDB

## ⚡ Quick Start (3 Steps)

### Step 1: Install Prerequisites

1. **Install Java JDK 17+**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version`

2. **Install Maven**
   - Download: https://maven.apache.org/download.cgi
   - Add to PATH
   - Verify: `mvn -version`

3. **Install MongoDB**
   - Download: https://www.mongodb.com/try/download/community
   - Install as Windows Service
   - Verify: `mongod --version`

### Step 2: Start MongoDB

Open PowerShell as Administrator:
```powershell
# Start MongoDB service
net start MongoDB

# Verify it's running
Get-Service MongoDB
```

### Step 3: Run the Application

**Option A: Using the batch file (Easiest)**
```powershell
# Double-click run.bat
# OR from PowerShell:
.\run.bat
```

**Option B: Using Maven**
```powershell
# Build and run
mvn clean compile
mvn exec:java -Dexec.mainClass="LibraryManagementSystem"
```

## 🎯 First Time Login

### Admin Login:
- Username: `admin`
- Password: `admin123`

### Test User:
- Username: `user1`
- Password: `pass123`

## 📊 Verify Database

1. Open MongoDB Compass
2. Connect to: `mongodb://localhost:27017`
3. Look for database: `library_management_db`
4. Check collections: users, books, transactions, categories

## ❓ Common Issues

### "MongoDB not connected"
```powershell
# Check if MongoDB is running
Get-Service MongoDB

# If not running, start it
net start MongoDB
```

### "Maven command not found"
- Add Maven to PATH environment variable
- Restart PowerShell/Command Prompt

### "Java version error"
- Ensure JDK 17 or higher is installed
- Set JAVA_HOME environment variable

## 🔧 Manual Compilation (If Maven doesn't work)

```powershell
# 1. Download dependencies manually from Maven Central
# 2. Place JAR files in a 'lib' folder
# 3. Compile:
javac -cp ".;lib/*" *.java

# 4. Run:
java -cp ".;lib/*" LibraryManagementSystem
```

## 📁 Project Structure

```
library/
├── LibraryManagementSystem.java  # Main application
├── MongoDBManager.java            # Database operations
├── config.properties              # Database configuration
├── pom.xml                        # Maven dependencies
├── run.bat                        # Quick start script
├── README.md                      # Full documentation
└── SETUP.md                       # This file
```

## 🎓 Features to Try

1. **Login as admin** → Add a new book
2. **Register a new user** → Login as that user
3. **Borrow a book** → Check due date
4. **Return a book late** → See fine calculation
5. **Use Database Operations menu** → View statistics
6. **Add books to favorites** → View your favorites

## 💾 Database Operations

From the main menu, select option 7 (Database Operations):
1. Save All Data - Manually save current state
2. Reload Data - Refresh from database
3. View Statistics - See database stats
4. Clear Database - Reset everything (Admin only)

## 🚀 Next Steps

1. Explore the code to understand design patterns
2. Try adding new features
3. Modify the database schema
4. Add more book categories
5. Implement additional user types

## 📞 Need Help?

- Check README.md for detailed documentation
- Review the code comments
- Test with default data first
- Use MongoDB Compass to inspect database

---

**Happy Coding! 🎉**
