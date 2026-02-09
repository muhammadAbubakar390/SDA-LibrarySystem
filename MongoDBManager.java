import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * MongoDB Database Manager - Singleton Pattern
 * Handles all database operations for the Library Management System
 */
public class MongoDBManager {
    private static MongoDBManager instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private Properties config;
    
    // Collection names
    private String usersCollection;
    private String booksCollection;
    private String transactionsCollection;
    private String categoriesCollection;
    
    // Private constructor for Singleton
    private MongoDBManager() {
        loadConfiguration();
        connectToDatabase();
    }
    
    /**
     * Get singleton instance
     */
    public static MongoDBManager getInstance() {
        if (instance == null) {
            synchronized (MongoDBManager.class) {
                if (instance == null) {
                    instance = new MongoDBManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load configuration from properties file
     */
    private void loadConfiguration() {
        config = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
            usersCollection = config.getProperty("mongodb.collection.users", "users");
            booksCollection = config.getProperty("mongodb.collection.books", "books");
            transactionsCollection = config.getProperty("mongodb.collection.transactions", "transactions");
            categoriesCollection = config.getProperty("mongodb.collection.categories", "categories");
        } catch (IOException e) {
            System.err.println("⚠️  Warning: Could not load config.properties. Using default values.");
            usersCollection = "users";
            booksCollection = "books";
            transactionsCollection = "transactions";
            categoriesCollection = "categories";
        }
    }
    
    /**
     * Connect to MongoDB database
     */
    private void connectToDatabase() {
        try {
            String connectionString = config.getProperty("mongodb.connection.string", "mongodb://localhost:27017");
            String databaseName = config.getProperty("mongodb.database.name", "library_management_db");
            
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            
            System.out.println("✅ Successfully connected to MongoDB!");
            System.out.println("📊 Database: " + databaseName);
        } catch (Exception e) {
            System.err.println("❌ Error connecting to MongoDB: " + e.getMessage());
            System.err.println("⚠️  Running in offline mode. Data will not be persisted.");
        }
    }
    
    /**
     * Check if database is connected
     */
    public boolean isConnected() {
        try {
            if (mongoClient != null && database != null) {
                database.listCollectionNames().first();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    // ==================== USER OPERATIONS ====================
    
    /**
     * Save user to database
     */
    public boolean saveUser(User user) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(usersCollection);
            
            Document userDoc = new Document("username", user.username)
                    .append("password", user.password)
                    .append("userType", user.userType.getType())
                    .append("favourites", user.favourites)
                    .append("borrowedBooks", user.borrowedBooks)
                    .append("borrowDates", user.borrowDates)
                    .append("dueDates", user.dueDates)
                    .append("totalFine", user.totalFine)
                    .append("createdAt", LocalDateTime.now().toString())
                    .append("lastModified", LocalDateTime.now().toString());
            
            // Check if user exists
            Document existing = collection.find(Filters.eq("username", user.username)).first();
            if (existing != null) {
                // Update existing user
                collection.replaceOne(Filters.eq("username", user.username), userDoc);
            } else {
                // Insert new user
                collection.insertOne(userDoc);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Toggle favorite status for a book
     */
    public boolean toggleFavorite(String username, String bookTitle) {
        User user = loadUser(username);
        if (user == null) return false;

        boolean added;
        if (user.favourites.contains(bookTitle)) {
            user.favourites.remove(bookTitle);
            added = false;
        } else {
            user.favourites.add(bookTitle);
            added = true;
        }
        return saveUser(user);
    }
    
    /**
     * Load user from database
     */
    public User loadUser(String username) {
        if (!isConnected()) return null;
        
        try {
            MongoCollection<Document> collection = database.getCollection(usersCollection);
            Document userDoc = collection.find(Filters.eq("username", username)).first();
            
            if (userDoc != null) {
                String password = userDoc.getString("password");
                String userType = userDoc.getString("userType");
                
                User user = new User(username, password, userType);
                
                // Load user data
                user.favourites = userDoc.get("favourites") != null ? (List<String>) userDoc.get("favourites") : new ArrayList<>();
                user.borrowedBooks = userDoc.get("borrowedBooks") != null ? (List<String>) userDoc.get("borrowedBooks") : new ArrayList<>();
                user.borrowDates = userDoc.get("borrowDates") != null ? (Map<String, String>) userDoc.get("borrowDates") : new HashMap<>();
                user.dueDates = userDoc.get("dueDates") != null ? (Map<String, String>) userDoc.get("dueDates") : new HashMap<>();
                user.totalFine = userDoc.getDouble("totalFine") != null ? userDoc.getDouble("totalFine") : 0.0;
                
                return user;
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Load all users from database
     */
    public Map<String, User> loadAllUsers() {
        Map<String, User> users = new HashMap<>();
        
        if (!isConnected()) return users;
        
        try {
            MongoCollection<Document> collection = database.getCollection(usersCollection);
            
            for (Document doc : collection.find()) {
                String username = doc.getString("username");
                User user = loadUser(username);
                if (user != null) {
                    users.put(username, user);
                }
            }
            
            System.out.println("✅ Loaded " + users.size() + " users from database");
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Delete user from database
     */
    public boolean deleteUser(String username) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(usersCollection);
            collection.deleteOne(Filters.eq("username", username));
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== BOOK OPERATIONS ====================
    
    /**
     * Save book to database
     */
    public boolean saveBook(String bookTitle, int copies, String bookType, String category) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            
            Document bookDoc = new Document("title", bookTitle)
                    .append("copies", copies)
                    .append("bookType", bookType)
                    .append("category", category)
                    .append("createdAt", LocalDateTime.now().toString())
                    .append("lastModified", LocalDateTime.now().toString());
            
            // Check if book exists
            Document existing = collection.find(Filters.eq("title", bookTitle)).first();
            if (existing != null) {
                // Update existing book
                collection.replaceOne(Filters.eq("title", bookTitle), bookDoc);
            } else {
                // Insert new book
                collection.insertOne(bookDoc);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save book with visibility and owner settings
     */
    public boolean saveBookWithVisibility(String bookTitle, int copies, String bookType, String category, String owner, String visibility) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            
            Document bookDoc = new Document("title", bookTitle)
                    .append("copies", copies)
                    .append("bookType", bookType)
                    .append("category", category)
                    .append("owner", owner)
                    .append("visibility", visibility) // "PUBLIC" or "PRIVATE"
                    .append("createdAt", LocalDateTime.now().toString())
                    .append("lastModified", LocalDateTime.now().toString());
            
            // Check if book exists
            Document existing = collection.find(Filters.eq("title", bookTitle)).first();
            if (existing != null) {
                // Keep creator if not specified, or update it? Let's just update fields.
                collection.replaceOne(Filters.eq("title", bookTitle), bookDoc);
            } else {
                collection.insertOne(bookDoc);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving book with visibility: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load all books with full details (for web app)
     */
    public List<Document> loadBooksWithDetails() {
        List<Document> books = new ArrayList<>();
        if (!isConnected()) return books;

        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            collection.find().into(books);
        } catch (Exception e) {
            System.err.println("❌ Error loading book details: " + e.getMessage());
        }
        return books;
    }
    
    /**
     * Load all books from database
     */
    public Map<String, Integer> loadAllBooks() {
        Map<String, Integer> books = new HashMap<>();
        
        if (!isConnected()) return books;
        
        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            
            for (Document doc : collection.find()) {
                String title = doc.getString("title");
                int copies = doc.getInteger("copies", 0);
                books.put(title, copies);
            }
            
            System.out.println("✅ Loaded " + books.size() + " books from database");
        } catch (Exception e) {
            System.err.println("❌ Error loading books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Load book types from database
     */
    public Map<String, String> loadBookTypes() {
        Map<String, String> bookTypes = new HashMap<>();
        
        if (!isConnected()) return bookTypes;
        
        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            
            for (Document doc : collection.find()) {
                String title = doc.getString("title");
                String type = doc.getString("bookType");
                if (type != null) {
                    bookTypes.put(title, type);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading book types: " + e.getMessage());
        }
        
        return bookTypes;
    }
    
    /**
     * Delete book from database
     */
    public boolean deleteBook(String bookTitle) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            collection.deleteOne(Filters.eq("title", bookTitle));
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update book copies
     */
    public boolean updateBookCopies(String bookTitle, int copies) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(booksCollection);
            Bson update = Updates.combine(
                Updates.set("copies", copies),
                Updates.set("lastModified", LocalDateTime.now().toString())
            );
            collection.updateOne(Filters.eq("title", bookTitle), update);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error updating book copies: " + e.getMessage());
            return false;
        }
    }
    
    // ==================== CATEGORY OPERATIONS ====================
    
    /**
     * Save categories to database
     */
    public boolean saveCategories(List<String> categories, Map<String, List<String>> categoryBooks) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(categoriesCollection);
            
            // Clear existing categories
            collection.deleteMany(new Document());
            
            // Save each category with its books
            for (String category : categories) {
                List<String> books = categoryBooks.getOrDefault(category, new ArrayList<>());
                Document categoryDoc = new Document("name", category)
                        .append("books", books)
                        .append("lastModified", LocalDateTime.now().toString());
                collection.insertOne(categoryDoc);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving categories: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load categories from database
     */
    public List<String> loadCategories() {
        List<String> categories = new ArrayList<>();
        
        if (!isConnected()) return categories;
        
        try {
            MongoCollection<Document> collection = database.getCollection(categoriesCollection);
            
            for (Document doc : collection.find()) {
                String name = doc.getString("name");
                categories.add(name);
            }
            
            System.out.println("✅ Loaded " + categories.size() + " categories from database");
        } catch (Exception e) {
            System.err.println("❌ Error loading categories: " + e.getMessage());
        }
        
        return categories;
    }
    
    /**
     * Load category books mapping from database
     */
    public Map<String, List<String>> loadCategoryBooks() {
        Map<String, List<String>> categoryBooks = new HashMap<>();
        
        if (!isConnected()) return categoryBooks;
        
        try {
            MongoCollection<Document> collection = database.getCollection(categoriesCollection);
            
            for (Document doc : collection.find()) {
                String name = doc.getString("name");
                List<String> books = doc.get("books") != null ? (List<String>) doc.get("books") : new ArrayList<>();
                categoryBooks.put(name, new ArrayList<>(books));
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading category books: " + e.getMessage());
        }
        
        return categoryBooks;
    }
    
    // ==================== TRANSACTION OPERATIONS ====================
    
    /**
     * Save transaction (borrow/return) to database
     */
    public boolean saveTransaction(String username, String bookTitle, String action, String date) {
        if (!isConnected()) return false;
        
        try {
            MongoCollection<Document> collection = database.getCollection(transactionsCollection);
            
            Document transaction = new Document("username", username)
                    .append("bookTitle", bookTitle)
                    .append("action", action)
                    .append("date", date)
                    .append("timestamp", LocalDateTime.now().toString());
            
            collection.insertOne(transaction);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error saving transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user transaction history
     */
    public List<Document> getUserTransactions(String username) {
        List<Document> transactions = new ArrayList<>();
        
        if (!isConnected()) return transactions;
        
        try {
            MongoCollection<Document> collection = database.getCollection(transactionsCollection);
            collection.find(Filters.eq("username", username)).into(transactions);
        } catch (Exception e) {
            System.err.println("❌ Error loading transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Initialize database with default data
     */
    public void initializeDefaultData() {
        if (!isConnected()) return;
        
        System.out.println("🔄 Initializing database with default data...");
        
        // Check if data already exists
        MongoCollection<Document> usersCol = database.getCollection(usersCollection);
        if (usersCol.countDocuments() > 0) {
            System.out.println("ℹ️  Database already contains data. Skipping initialization.");
            return;
        }
        
        // Create default admin user
        User admin = new User("admin", "admin123", "authorized");
        saveUser(admin);
        
        System.out.println("✅ Default data initialized successfully!");
    }
    
    /**
     * Clear all data from database
     */
    public void clearAllData() {
        if (!isConnected()) return;
        
        try {
            database.getCollection(usersCollection).deleteMany(new Document());
            database.getCollection(booksCollection).deleteMany(new Document());
            database.getCollection(transactionsCollection).deleteMany(new Document());
            database.getCollection(categoriesCollection).deleteMany(new Document());
            
            System.out.println("✅ All data cleared from database");
        } catch (Exception e) {
            System.err.println("❌ Error clearing data: " + e.getMessage());
        }
    }
    
    /**
     * Get database statistics
     */
    public void printDatabaseStats() {
        if (!isConnected()) {
            System.out.println("❌ Not connected to database");
            return;
        }
        
        try {
            System.out.println("\n📊 === DATABASE STATISTICS ===");
            System.out.println("Users: " + database.getCollection(usersCollection).countDocuments());
            System.out.println("Books: " + database.getCollection(booksCollection).countDocuments());
            System.out.println("Transactions: " + database.getCollection(transactionsCollection).countDocuments());
            System.out.println("Categories: " + database.getCollection(categoriesCollection).countDocuments());
        } catch (Exception e) {
            System.err.println("❌ Error getting stats: " + e.getMessage());
        }
    }
    
    /**
     * Close database connection
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("✅ Database connection closed");
        }
    }
}
