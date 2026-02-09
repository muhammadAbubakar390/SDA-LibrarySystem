# Manual Setup Guide (Without Maven)

## 🎯 Quick Setup for MongoDB Compass Connection

Since Maven is not installed, follow these steps to set up and view your database in MongoDB Compass.

---

## ✅ Step 1: Verify MongoDB is Running

MongoDB is already running! ✓

You can verify by opening PowerShell and running:
```powershell
Get-Service MongoDB
```

---

## 📦 Step 2: Download MongoDB Java Driver

You need the MongoDB Java Driver to run the application.

### Option A: Download JARs Manually (Recommended)

1. **Download MongoDB Driver JAR:**
   - Go to: https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.11.1/
   - Download: `mongodb-driver-sync-4.11.1.jar`

2. **Download BSON JAR:**
   - Go to: https://repo1.maven.org/maven2/org/mongodb/bson/4.11.1/
   - Download: `bson-4.11.1.jar`

3. **Download MongoDB Core JAR:**
   - Go to: https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/4.11.1/
   - Download: `mongodb-driver-core-4.11.1.jar`

4. **Create a `lib` folder** in your project directory:
   ```powershell
   cd "c:\Users\my computer\Desktop\Uni\SEM 5\SDA W5\library"
   mkdir lib
   ```

5. **Move all downloaded JARs** to the `lib` folder

### Option B: Install Maven (Easier for Future)

1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH environment variable
4. Restart PowerShell
5. Run: `mvn clean compile`

---

## 🔨 Step 3: Compile the Java Files

Once you have the JARs in the `lib` folder:

```powershell
cd "c:\Users\my computer\Desktop\Uni\SEM 5\SDA W5\library"

# Compile with classpath
javac -cp ".;lib/*" MongoDBManager.java LibraryManagementSystem.java
```

---

## 🚀 Step 4: Run the Application

```powershell
# Run with classpath
java -cp ".;lib/*" LibraryManagementSystem
```

**Expected Output:**
```
📚 ===== LIBRARY MANAGEMENT SYSTEM (WITH MONGODB) =====
🔄 Connecting to MongoDB...
✅ Successfully connected to MongoDB!
📊 Database: library_management_db
ℹ️  No data found in database. Initializing with defaults...
✅ System ready!
```

**Important:** Let it initialize, then you can exit (Option 8) or keep it running.

---

## 🗄️ Step 5: Open MongoDB Compass

### 5.1 Launch MongoDB Compass

1. Open MongoDB Compass from Start Menu
2. You'll see the connection screen

### 5.2 Connect to MongoDB

**Connection String:**
```
mongodb://localhost:27017
```

1. Paste this in the connection string field
2. Click **"Connect"**

### 5.3 View Your Database

Once connected, you should see:

```
📁 Databases
  └── 📚 library_management_db
      ├── 📄 users (3 documents)
      ├── 📄 books (5 documents)
      ├── 📄 categories (4 documents)
      └── 📄 transactions (0 documents)
```

---

## 📊 Step 6: Explore the Collections

### View Users:
Click on **users** collection to see:
- admin (username: admin, password: admin123)
- user1 (username: user1, password: pass123)
- guest (username: guest, password: guest123)

### View Books:
Click on **books** collection to see:
- Java Programming (3 copies)
- Python Basics (2 copies)
- Data Structures (1 copy)
- Operating Systems (2 copies)
- Database Management (1 copy)

### View Categories:
Click on **categories** collection to see:
- Programming
- Science
- Fiction
- History

### View Transactions:
Click on **transactions** collection
- Initially empty
- Will populate when you borrow/return books

---

## 🎯 Alternative: Use Simplified Version (No Dependencies)

If downloading JARs is difficult, I can create a simplified version that uses a different approach. Let me know!

---

## 📸 Visual Guide for MongoDB Compass

### 1. Connection Screen
```
┌─────────────────────────────────────────┐
│  New Connection                         │
│  ┌───────────────────────────────────┐  │
│  │ mongodb://localhost:27017         │  │
│  └───────────────────────────────────┘  │
│              [Connect]                  │
└─────────────────────────────────────────┘
```

### 2. After Connection
```
┌─────────────────────────────────────────┐
│  📁 Databases                           │
│    ├── admin                            │
│    ├── config                           │
│    ├── local                            │
│    └── 📚 library_management_db         │
│        ├── users (3)                    │
│        ├── books (5)                    │
│        ├── categories (4)               │
│        └── transactions (0)             │
└─────────────────────────────────────────┘
```

### 3. Click on a Collection
```
┌─────────────────────────────────────────┐
│  library_management_db > users          │
│  ┌───────────────────────────────────┐  │
│  │ Document 1:                       │  │
│  │ {                                 │  │
│  │   "_id": ObjectId("..."),         │  │
│  │   "username": "admin",            │  │
│  │   "password": "admin123",         │  │
│  │   "userType": "Authorized",       │  │
│  │   "favourites": [],               │  │
│  │   "borrowedBooks": [],            │  │
│  │   "totalFine": 0.0                │  │
│  │ }                                 │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

---

## 🔍 Troubleshooting

### Issue: "Database not found in Compass"

**Reason:** The application hasn't run yet to create the database.

**Solution:**
1. Make sure you've compiled the Java files
2. Run the application at least once
3. Let it initialize completely
4. Refresh MongoDB Compass (click the refresh icon)

### Issue: "Cannot compile - missing dependencies"

**Reason:** MongoDB Java Driver JARs not found.

**Solution:**
1. Download the 3 JAR files mentioned in Step 2
2. Place them in the `lib` folder
3. Use the `-cp ".;lib/*"` flag when compiling

### Issue: "Connection refused in Compass"

**Reason:** MongoDB service not running.

**Solution:**
```powershell
net start MongoDB
```

---

## ✅ Quick Checklist

- [ ] MongoDB service is running
- [ ] Downloaded MongoDB Java Driver JARs (or installed Maven)
- [ ] Compiled Java files successfully
- [ ] Ran the application at least once
- [ ] Opened MongoDB Compass
- [ ] Connected to `mongodb://localhost:27017`
- [ ] Can see `library_management_db` database
- [ ] Can view collections and documents

---

## 🎉 Success Criteria

You'll know everything is working when:

1. **In MongoDB Compass:**
   - ✅ Connected to localhost:27017
   - ✅ Database `library_management_db` visible
   - ✅ 4 collections present
   - ✅ Can view document details

2. **In Application:**
   - ✅ "Successfully connected to MongoDB!" message
   - ✅ Can login and use features
   - ✅ Changes reflect in MongoDB Compass

---

## 📞 Need Help?

### Quick Commands Reference:

**Check MongoDB:**
```powershell
Get-Service MongoDB
```

**Start MongoDB:**
```powershell
net start MongoDB
```

**Compile (with JARs):**
```powershell
javac -cp ".;lib/*" MongoDBManager.java LibraryManagementSystem.java
```

**Run (with JARs):**
```powershell
java -cp ".;lib/*" LibraryManagementSystem
```

**MongoDB Compass Connection:**
```
mongodb://localhost:27017
```

---

## 🚀 Next Steps After Setup

1. **Test in Application:**
   - Login as user1
   - Borrow a book
   - Add to favorites

2. **Verify in Compass:**
   - Refresh the users collection
   - See updated borrowedBooks array
   - Check transactions collection

3. **Explore Features:**
   - Try all menu options
   - Watch data update in real-time
   - Test search and browse

---

**Ready to proceed?** Follow the steps above and let me know if you need help with any step!
