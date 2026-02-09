# Testing Guide - Library Management System with MongoDB

## 🧪 Complete Testing Checklist

### Pre-Testing Setup

1. **Ensure MongoDB is Running**
   ```powershell
   Get-Service MongoDB
   # Should show: Running
   ```

2. **Build the Project**
   ```powershell
   mvn clean compile
   ```

3. **Start the Application**
   ```powershell
   mvn exec:java -Dexec.mainClass="LibraryManagementSystem"
   # OR
   .\run.bat
   ```

---

## 📋 Test Cases

### Test 1: Database Connection ✓

**Objective**: Verify MongoDB connection

**Steps**:
1. Start the application
2. Look for message: "✅ Successfully connected to MongoDB!"
3. Check for database name: "library_management_db"

**Expected Result**:
- Connection successful message
- No error messages
- System loads data or initializes defaults

**Verify in MongoDB Compass**:
- Database `library_management_db` exists
- Collections: users, books, transactions, categories

---

### Test 2: Admin Login ✓

**Objective**: Test admin authentication and features

**Steps**:
1. Main Menu → Select 1 (Admin Login)
2. Username: `admin`
3. Password: `admin123`

**Expected Result**:
- "✅ Login successful! Welcome Admin!"
- Admin menu appears with 9 options

**Verify**:
- Can access all admin functions
- Premium features displayed

---

### Test 3: User Registration ✓

**Objective**: Create new user and save to database

**Steps**:
1. Main Menu → Select 3 (Register New User)
2. Username: `testuser`
3. Password: `test123`
4. User Type: 1 (Authorized)

**Expected Result**:
- "✅ Registration successful!"
- User can now login

**Verify in MongoDB**:
```javascript
// In MongoDB Compass, users collection:
db.users.find({username: "testuser"})
// Should return the new user document
```

---

### Test 4: Add New Book (Admin) ✓

**Objective**: Add book and save to database

**Steps**:
1. Login as admin
2. Admin Menu → Select 4 (Add New Book)
3. Title: `Test Book`
4. Author: `Test Author`
5. Copies: `5`
6. Type: 1 (Regular Book)
7. Category: 1 (Programming)

**Expected Result**:
- "✅ New Regular book added successfully!"
- Book appears in book list

**Verify in MongoDB**:
```javascript
// In MongoDB Compass, books collection:
db.books.find({title: "Test Book by Test Author"})
```

---

### Test 5: Borrow Book ✓

**Objective**: Borrow book and verify database update

**Steps**:
1. Login as `user1` (password: `pass123`)
2. User Menu → Select 4 (Borrow Book)
3. Select any available book (e.g., 1)

**Expected Result**:
- "✅ Book borrowed successfully!"
- Shows borrow date and due date
- Shows fine rate

**Verify in MongoDB**:
```javascript
// Check user's borrowed books updated
db.users.find({username: "user1"})
// Check book copies decreased
db.books.find({title: "Java Programming"})
// Check transaction logged
db.transactions.find({username: "user1", action: "BORROW"})
```

---

### Test 6: Return Book ✓

**Objective**: Return book and verify updates

**Steps**:
1. As logged-in user with borrowed books
2. User Menu → Select 5 (Return Book)
3. Select book to return

**Expected Result**:
- "✅ Book returned successfully!"
- Shows return date
- Shows any fines if late

**Verify in MongoDB**:
```javascript
// Check user's borrowed books updated
db.users.find({username: "user1"})
// Check book copies increased
db.books.find({title: "Java Programming"})
// Check return transaction logged
db.transactions.find({username: "user1", action: "RETURN"})
```

---

### Test 7: Add to Favorites ✓

**Objective**: Add book to favorites

**Steps**:
1. Login as user
2. User Menu → Select 6 (Add to Favourites)
3. Select a book

**Expected Result**:
- "✅ Added to favourites: [Book Name]"

**Verify in MongoDB**:
```javascript
db.users.find({username: "user1"})
// Check favourites array contains the book
```

---

### Test 8: Database Save Operation ✓

**Objective**: Manual database save

**Steps**:
1. Main Menu → Select 7 (Database Operations)
2. Select 1 (Save All Data to Database)

**Expected Result**:
- "✅ Data saved to database successfully!"
- "✅ All data saved successfully!"

---

### Test 9: Database Statistics ✓

**Objective**: View database statistics

**Steps**:
1. Main Menu → Select 7 (Database Operations)
2. Select 3 (View Database Statistics)

**Expected Result**:
```
📊 === DATABASE STATISTICS ===
Users: X
Books: Y
Transactions: Z
Categories: 4
```

---

### Test 10: Data Persistence ✓

**Objective**: Verify data persists across restarts

**Steps**:
1. Perform some operations (borrow books, add favorites)
2. Exit the application (Option 8)
3. Restart the application
4. Login as same user

**Expected Result**:
- All borrowed books still shown
- All favorites still present
- Fine amounts preserved

---

### Test 11: Offline Mode ✓

**Objective**: Test system without MongoDB

**Steps**:
1. Stop MongoDB service:
   ```powershell
   net stop MongoDB
   ```
2. Start the application

**Expected Result**:
- "⚠️ Database not connected. Running in offline mode."
- System still works
- Data not persisted

3. Restart MongoDB:
   ```powershell
   net start MongoDB
   ```

---

### Test 12: Fine Calculation ✓

**Objective**: Test overdue fine calculation

**Note**: This requires modifying due dates in the database

**Steps**:
1. Borrow a book
2. In MongoDB Compass, modify the due date to past:
   ```javascript
   db.users.updateOne(
     {username: "user1"},
     {$set: {"dueDates.Java Programming": "2026-01-01"}}
   )
   ```
3. Return the book

**Expected Result**:
- Shows days late
- Calculates fine correctly
- Updates total fine

---

### Test 13: User Removal (Admin) ✓

**Objective**: Remove user from system

**Steps**:
1. Login as admin
2. Admin Menu → Select 2 (Remove User)
3. Enter username to remove

**Expected Result**:
- "✅ User removed successfully!"
- User no longer in system

**Verify in MongoDB**:
```javascript
db.users.find({username: "testuser"})
// Should return no results
```

---

### Test 14: Book Search ✓

**Objective**: Search for books

**Steps**:
1. Login as user
2. User Menu → Select 3 (Search Books)
3. Enter search term: `Java`

**Expected Result**:
- Shows all books containing "Java"
- Displays with decorator pattern (status, category)

---

### Test 15: Category Browsing ✓

**Objective**: Browse books by category

**Steps**:
1. Main Menu → Select 5 (Browse by Category)
2. Select a category (e.g., 1 for Programming)

**Expected Result**:
- Shows all books in that category
- Shows availability status

---

## 🔍 Advanced Testing

### Test 16: Concurrent Operations

**Objective**: Test data consistency

**Steps**:
1. Open two instances of the application
2. Login as different users in each
3. Borrow the same book from both

**Expected Result**:
- Only one should succeed
- Book copies correctly updated

---

### Test 17: Database Reload

**Objective**: Test data reload functionality

**Steps**:
1. Make changes in MongoDB Compass directly
2. In application: Database Operations → Reload Data
3. Verify changes reflected

---

### Test 18: Clear Database (Admin)

**Objective**: Reset entire database

**Steps**:
1. Login as admin
2. Database Operations → Clear All Database Data
3. Confirm with "yes"

**Expected Result**:
- All data cleared
- System reinitializes with defaults
- Default users and books created

---

## 📊 Performance Testing

### Test 19: Large Dataset

**Objective**: Test with many records

**Steps**:
1. Add 100+ books via MongoDB Compass
2. Create 50+ users
3. Test search and browse functions

**Expected Result**:
- System remains responsive
- All operations complete successfully

---

### Test 20: Transaction History

**Objective**: Verify transaction logging

**Steps**:
1. Perform multiple borrow/return operations
2. Check transactions collection in MongoDB

**Expected Result**:
- All transactions logged
- Correct timestamps
- Accurate action types

---

## ✅ Testing Checklist Summary

- [ ] MongoDB connection successful
- [ ] Admin login works
- [ ] User registration saves to database
- [ ] Books can be added and saved
- [ ] Borrow operation updates database
- [ ] Return operation updates database
- [ ] Favorites saved to database
- [ ] Manual save works
- [ ] Database statistics accurate
- [ ] Data persists across restarts
- [ ] Offline mode works
- [ ] Fine calculation correct
- [ ] User removal works
- [ ] Search functionality works
- [ ] Category browsing works
- [ ] Transaction logging works
- [ ] Database reload works
- [ ] Clear database works (admin)

---

## 🐛 Known Issues & Solutions

### Issue 1: "Connection refused"
**Solution**: Ensure MongoDB service is running

### Issue 2: "Collection not found"
**Solution**: Let application initialize database first

### Issue 3: Data not saving
**Solution**: Check MongoDB connection in config.properties

### Issue 4: Duplicate key error
**Solution**: Ensure unique usernames and book titles

---

## 📝 Test Report Template

```
Test Date: _______________
Tester: _______________
MongoDB Version: _______________
Java Version: _______________

Test Results:
[ ] All tests passed
[ ] Some tests failed (list below)
[ ] System not ready for testing

Failed Tests:
1. _____________________
2. _____________________

Notes:
_____________________
_____________________
```

---

## 🎯 Acceptance Criteria

System is ready for deployment when:
- ✅ All 20 tests pass
- ✅ No data loss on restart
- ✅ All CRUD operations work
- ✅ Database properly synchronized
- ✅ Error handling works correctly
- ✅ Documentation is complete

---

**Happy Testing! 🧪**
