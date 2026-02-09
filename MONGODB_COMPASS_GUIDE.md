# MongoDB Compass Connection Guide

## 🎯 Step-by-Step Guide to View Your Library Database

### Step 1: Ensure MongoDB Service is Running

Open PowerShell as Administrator and run:
```powershell
# Check if MongoDB is running
Get-Service MongoDB

# If not running, start it:
net start MongoDB
```

**Expected Output:**
```
Status   Name               DisplayName
------   ----               -----------
Running  MongoDB            MongoDB Server
```

---

### Step 2: Run Your Library Application (First Time)

This will create the database and populate it with initial data.

```powershell
# Navigate to your project directory
cd "c:\Users\my computer\Desktop\Uni\SEM 5\SDA W5\library"

# Run the application
.\run.bat
```

**OR manually:**
```powershell
mvn clean compile
mvn exec:java -Dexec.mainClass="LibraryManagementSystem"
```

**What to look for:**
```
✅ Successfully connected to MongoDB!
📊 Database: library_management_db
ℹ️  No data found in database. Initializing with defaults...
✅ System ready!
```

**Important:** Let the application run and initialize. You can then exit (Option 8) or keep it running.

---

### Step 3: Open MongoDB Compass

1. **Launch MongoDB Compass** from Start Menu
2. You should see the connection screen

---

### Step 4: Connect to MongoDB

#### Option A: Using the Default Connection (Recommended)

1. In MongoDB Compass, you'll see a connection string field
2. The default should be: `mongodb://localhost:27017`
3. Click **"Connect"** button

#### Option B: Manual Connection Setup

If the default doesn't work:

1. Click **"New Connection"**
2. Enter connection details:
   - **Connection String**: `mongodb://localhost:27017`
   - **OR use Advanced Connection Options:**
     - Host: `localhost`
     - Port: `27017`
     - Authentication: None (for local development)
3. Click **"Connect"**

---

### Step 5: View Your Library Database

Once connected, you should see:

```
📁 Databases
  ├── admin
  ├── config
  ├── local
  └── 📚 library_management_db  ← Your database!
```

**Click on `library_management_db`** to expand it.

---

### Step 6: Explore Collections

You should see 4 collections:

```
library_management_db
  ├── 📄 users           (3 documents)
  ├── 📄 books           (5 documents)
  ├── 📄 categories      (4 documents)
  └── 📄 transactions    (0 documents initially)
```

---

### Step 7: View Collection Data

#### View Users Collection:
1. Click on **"users"** collection
2. You should see 3 documents:
   - admin
   - user1
   - guest

**Example Document:**
```json
{
  "_id": ObjectId("..."),
  "username": "admin",
  "password": "admin123",
  "userType": "Authorized",
  "favourites": [],
  "borrowedBooks": [],
  "borrowDates": {},
  "dueDates": {},
  "totalFine": 0.0,
  "createdAt": "2026-01-31T12:30:00",
  "lastModified": "2026-01-31T12:30:00"
}
```

#### View Books Collection:
1. Click on **"books"** collection
2. You should see 5 books:
   - Java Programming
   - Python Basics
   - Data Structures
   - Operating Systems
   - Database Management

**Example Document:**
```json
{
  "_id": ObjectId("..."),
  "title": "Java Programming",
  "copies": 3,
  "bookType": "Regular",
  "category": "Programming",
  "createdAt": "2026-01-31T12:30:00",
  "lastModified": "2026-01-31T12:30:00"
}
```

#### View Categories Collection:
1. Click on **"categories"** collection
2. You should see 4 categories:
   - Programming
   - Science
   - Fiction
   - History

**Example Document:**
```json
{
  "_id": ObjectId("..."),
  "name": "Programming",
  "books": [
    "Java Programming",
    "Python Basics",
    "Data Structures"
  ],
  "lastModified": "2026-01-31T12:30:00"
}
```

#### View Transactions Collection:
1. Click on **"transactions"** collection
2. Initially empty (0 documents)
3. Will populate when you borrow/return books

**Example Document (after borrowing):**
```json
{
  "_id": ObjectId("..."),
  "username": "user1",
  "bookTitle": "Java Programming",
  "action": "BORROW",
  "date": "2026-01-31",
  "timestamp": "2026-01-31T12:35:00"
}
```

---

## 🔍 Troubleshooting

### Issue 1: "Unable to connect to server"

**Solution:**
```powershell
# Check if MongoDB is running
Get-Service MongoDB

# Start MongoDB if stopped
net start MongoDB

# Verify MongoDB is listening on port 27017
netstat -an | findstr "27017"
```

### Issue 2: "Database not found"

**Solution:**
- The database is created when you first run the application
- Make sure you've run the Library Management System at least once
- The application must complete initialization

### Issue 3: "Collections are empty"

**Solution:**
- Run the application and let it initialize
- Check console for: "✅ System ready!"
- Exit the application properly (Option 8) to ensure data is saved

---

## 📊 Viewing Live Data Changes

### Test 1: Borrow a Book

1. **In your application:**
   - Login as user1 (password: pass123)
   - Borrow a book (Option 4)
   - Select "Java Programming"

2. **In MongoDB Compass:**
   - Refresh the **users** collection
   - Find user1 document
   - See `borrowedBooks` array updated
   - Check `borrowDates` and `dueDates` maps

3. **Check transactions:**
   - Refresh **transactions** collection
   - See new BORROW transaction

4. **Check books:**
   - Refresh **books** collection
   - See "Java Programming" copies decreased

### Test 2: Add to Favorites

1. **In your application:**
   - Add a book to favorites (Option 6)

2. **In MongoDB Compass:**
   - Refresh **users** collection
   - See `favourites` array updated

### Test 3: Return a Book

1. **In your application:**
   - Return a book (Option 5)

2. **In MongoDB Compass:**
   - Refresh **users** collection
   - See `borrowedBooks` array updated
   - Check if `totalFine` increased (if late)
   - Refresh **transactions** collection
   - See new RETURN transaction

---

## 🎯 Quick Reference

### Connection Details:
```
Host: localhost
Port: 27017
Database: library_management_db
Authentication: None (local development)
```

### Connection String:
```
mongodb://localhost:27017
```

### Collections:
```
users         - User accounts and activity
books         - Book inventory
categories    - Book organization
transactions  - Borrow/return history
```

---

## 📸 Visual Guide

### 1. MongoDB Compass Main Screen
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
│  Databases                              │
│  ├── admin                              │
│  ├── config                             │
│  ├── local                              │
│  └── 📚 library_management_db           │
│      ├── users (3)                      │
│      ├── books (5)                      │
│      ├── categories (4)                 │
│      └── transactions (0)               │
└─────────────────────────────────────────┘
```

### 3. Viewing a Collection
```
┌─────────────────────────────────────────┐
│  library_management_db > users          │
│  ┌───────────────────────────────────┐  │
│  │ {                                 │  │
│  │   "username": "admin",            │  │
│  │   "password": "admin123",         │  │
│  │   "userType": "Authorized",       │  │
│  │   "borrowedBooks": [],            │  │
│  │   "totalFine": 0.0                │  │
│  │ }                                 │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

---

## 🎓 Advanced Features in MongoDB Compass

### 1. Filter Documents
In the filter bar, try:
```json
{ "username": "admin" }
{ "bookType": "Regular" }
{ "action": "BORROW" }
```

### 2. Sort Documents
Click on column headers to sort

### 3. Export Data
- Click "Export Collection"
- Choose JSON or CSV format

### 4. Import Data
- Click "Import Data"
- Select JSON or CSV file

### 5. View Schema
- Click "Schema" tab
- See data structure analysis

---

## ✅ Verification Checklist

After connecting, verify:
- [ ] Database `library_management_db` exists
- [ ] Collection `users` has 3 documents
- [ ] Collection `books` has 5 documents
- [ ] Collection `categories` has 4 documents
- [ ] Collection `transactions` exists (may be empty)
- [ ] Can view document details
- [ ] Can refresh and see updates

---

## 🎉 Success!

You should now see:
- ✅ MongoDB Compass connected to localhost:27017
- ✅ Database `library_management_db` visible
- ✅ All 4 collections with data
- ✅ Can view and explore documents
- ✅ Real-time updates when using the application

---

**Need Help?** 
- Check if MongoDB service is running
- Ensure application has run at least once
- Verify connection string: `mongodb://localhost:27017`
- Check firewall settings (allow port 27017)

**Happy Exploring! 🎉**
