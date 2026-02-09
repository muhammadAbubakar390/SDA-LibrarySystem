# Library Management System with MongoDB Integration

A comprehensive Java-based Library Management System implementing multiple design patterns (Factory, Decorator, Observer, Singleton) with MongoDB database integration for data persistence.

## 🎯 Features

### Core Functionality
- **User Management**: Admin and regular user accounts with different privileges
- **Book Management**: Add, remove, and track books with different types (Regular, Reference)
- **Borrowing System**: Borrow and return books with automatic due date calculation
- **Fine Management**: Automatic fine calculation for overdue books
- **Category System**: Organize books by categories
- **Favorites**: Users can maintain a list of favorite books
- **Transaction History**: Track all borrowing and return activities

### Design Patterns Implemented
1. **Factory Pattern**: User and Book type creation
2. **Decorator Pattern**: Enhanced book display and user features
3. **Observer Pattern**: Event notifications for library activities
4. **Singleton Pattern**: Database manager and event manager

### Database Integration
- **MongoDB**: Full CRUD operations for users, books, categories, and transactions
- **Data Persistence**: All data is automatically saved to MongoDB
- **Auto-save**: Data is saved on key operations and system shutdown
- **Offline Mode**: System can run without database connection

## 📋 Prerequisites

### Required Software
1. **Java Development Kit (JDK)** - Version 17 or higher
   - Download from: https://www.oracle.com/java/technologies/downloads/

2. **Apache Maven** - For dependency management
   - Download from: https://maven.apache.org/download.cgi

3. **MongoDB** - Database server
   - Download from: https://www.mongodb.com/try/download/community

### Optional Tools
- **MongoDB Compass** - GUI for MongoDB (recommended for beginners)
  - Download from: https://www.mongodb.com/try/download/compass

## 🚀 Installation & Setup

### Step 1: Install MongoDB

#### Windows:
1. Download MongoDB Community Server installer
2. Run the installer and follow the setup wizard
3. Choose "Complete" installation
4. Install MongoDB as a service (recommended)
5. MongoDB will start automatically on port 27017

#### Verify MongoDB Installation:
```powershell
# Open PowerShell and run:
mongod --version
```

### Step 2: Start MongoDB Service

#### Windows:
```powershell
# MongoDB should start automatically if installed as a service
# To manually start:
net start MongoDB

# To check if MongoDB is running:
Get-Service MongoDB
```

#### Alternative: Run MongoDB manually
```powershell
# Navigate to MongoDB bin directory (usually):
cd "C:\Program Files\MongoDB\Server\7.0\bin"

# Start MongoDB server:
.\mongod.exe
```

### Step 3: Configure the Application

1. Open `config.properties` file
2. Update MongoDB connection string if needed (default is localhost:27017):

```properties
mongodb.connection.string=mongodb://localhost:27017
mongodb.database.name=library_management_db
```

### Step 4: Build the Project

```powershell
# Navigate to project directory
cd "c:\Users\my computer\Desktop\Uni\SEM 5\SDA W5\library"

# Clean and compile the project
mvn clean compile

# Download dependencies
mvn dependency:resolve
```

### Step 5: Run the Application

#### Option 1: Using Maven
```powershell
mvn exec:java -Dexec.mainClass="LibraryManagementSystem"
```

#### Option 2: Compile and Run Manually
```powershell
# Compile all Java files
javac -cp ".;target/classes;%USERPROFILE%\.m2\repository\org\mongodb\mongodb-driver-sync\4.11.1\*;%USERPROFILE%\.m2\repository\org\mongodb\bson\4.11.1\*" *.java

# Run the application
java -cp ".;target/classes;%USERPROFILE%\.m2\repository\org\mongodb\mongodb-driver-sync\4.11.1\*;%USERPROFILE%\.m2\repository\org\mongodb\bson\4.11.1\*" LibraryManagementSystem
```

## 📚 Usage Guide

### Default Credentials

#### Admin Account:
- Username: `admin`
- Password: `admin123`

#### User Account:
- Username: `user1`
- Password: `pass123`

#### Guest Account (Limited Access):
- Username: `guest`
- Password: `guest123`

### Main Menu Options

1. **Admin Login**: Access administrative functions
2. **User Login**: Access user functions
3. **Register New User**: Create a new user account
4. **View Books**: Browse all available books
5. **Browse by Category**: View books organized by category
6. **Display User Info**: View current user information
7. **Database Operations**: Manage database (save, load, stats)
8. **Exit**: Save and exit the system

### Admin Functions

- Add/Remove Users
- Add/Remove Books
- View All Users
- View User Fines
- Send System Notifications
- Database Management

### User Functions

- View and Search Books
- Borrow Books
- Return Books
- Add Books to Favorites
- View Borrowed Books
- Check Fine Details

## 🗄️ Database Structure

### Collections

1. **users**
   - username (String)
   - password (String)
   - userType (String)
   - favourites (Array)
   - borrowedBooks (Array)
   - borrowDates (Map)
   - dueDates (Map)
   - totalFine (Double)

2. **books**
   - title (String)
   - copies (Integer)
   - bookType (String)
   - category (String)

3. **transactions**
   - username (String)
   - bookTitle (String)
   - action (String)
   - date (String)
   - timestamp (String)

4. **categories**
   - name (String)
   - books (Array)

## 🔧 Troubleshooting

### MongoDB Connection Issues

**Problem**: "Database not connected" message
**Solutions**:
1. Ensure MongoDB service is running:
   ```powershell
   Get-Service MongoDB
   ```
2. Check if MongoDB is listening on port 27017:
   ```powershell
   netstat -an | findstr "27017"
   ```
3. Verify connection string in `config.properties`

### Maven Build Issues

**Problem**: Dependencies not downloading
**Solutions**:
1. Clear Maven cache:
   ```powershell
   mvn dependency:purge-local-repository
   ```
2. Force update:
   ```powershell
   mvn clean install -U
   ```

### Java Compilation Issues

**Problem**: "Class not found" errors
**Solutions**:
1. Ensure JDK 17+ is installed
2. Set JAVA_HOME environment variable
3. Verify classpath includes all dependencies

## 📊 Database Operations

### View Database in MongoDB Compass

1. Open MongoDB Compass
2. Connect to: `mongodb://localhost:27017`
3. Select database: `library_management_db`
4. Browse collections: users, books, transactions, categories

### Backup Database

```powershell
# Create backup
mongodump --db library_management_db --out "C:\backup\library_db"

# Restore backup
mongorestore --db library_management_db "C:\backup\library_db\library_management_db"
```

## 🎨 Design Patterns Explained

### 1. Factory Pattern
- **UserFactory**: Creates different user types (Authorized/Unauthorized)
- **BookFactory**: Creates different book types (Regular/Reference)

### 2. Decorator Pattern
- **UserFeaturesDecorator**: Adds premium features to users
- **BookDecorator**: Enhances book display with status and category

### 3. Observer Pattern
- **LibraryEventManager**: Notifies observers of library events
- **Observers**: Email, Console, and Fine notifications

### 4. Singleton Pattern
- **MongoDBManager**: Single database connection instance
- **LibraryEventManager**: Single event manager instance

## 📝 Configuration Files

### config.properties
```properties
# MongoDB Configuration
mongodb.connection.string=mongodb://localhost:27017
mongodb.database.name=library_management_db

# Collections
mongodb.collection.users=users
mongodb.collection.books=books
mongodb.collection.transactions=transactions
mongodb.collection.categories=categories
```

### pom.xml
Contains Maven dependencies:
- MongoDB Java Driver (4.11.1)
- BSON (4.11.1)
- SLF4J Logging (2.0.9)
- Gson (2.10.1)

## 🔒 Security Notes

- Passwords are stored in plain text (for educational purposes only)
- In production, use password hashing (BCrypt, Argon2, etc.)
- Consider adding authentication tokens for sessions
- Implement role-based access control (RBAC)

## 🚀 Future Enhancements

- [ ] Password encryption
- [ ] Email notifications (actual SMTP integration)
- [ ] Book reservation system
- [ ] User profile pictures
- [ ] Advanced search filters
- [ ] Report generation (PDF/Excel)
- [ ] REST API for web/mobile clients
- [ ] Multi-library support

## 📄 License

This project is created for educational purposes as part of Software Design and Architecture coursework.

## 👥 Author

Muhammad Abubakar
- GitHub: [@muhammadAbubakar390](https://github.com/muhammadAbubakar390)

## 🙏 Acknowledgments

- Design Patterns: Gang of Four (GoF)
- MongoDB Java Driver Documentation
- Maven Central Repository

---

**Note**: This is an academic project demonstrating software design patterns and database integration. It is not intended for production use without proper security enhancements.
