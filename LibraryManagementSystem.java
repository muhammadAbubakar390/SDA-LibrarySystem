import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

// ==================== FACTORY PATTERN ====================

/* -------------------- USER FACTORY PATTERN -------------------- */
interface UserType {
    String getType();
    int getMaxBooks();
    double getFineRate();
}

class AuthorizedUser implements UserType {
    @Override public String getType() { return "Authorized"; }
    @Override public int getMaxBooks() { return 3; }
    @Override public double getFineRate() { return 10.0; }
}

class UnauthorizedUser implements UserType {
    @Override public String getType() { return "Unauthorized"; }
    @Override public int getMaxBooks() { return 0; }
    @Override public double getFineRate() { return 0.0; }
}

class UserFactory {
    public static UserType createUserType(String type) {
        switch(type.toLowerCase()) {
            case "authorized":
                return new AuthorizedUser();
            case "unauthorized":
                return new UnauthorizedUser();
            default:
                return new UnauthorizedUser();
        }
    }
}

/* -------------------- BOOK FACTORY PATTERN -------------------- */
interface BookType {
    String getType();
    int getBorrowDuration();
    double getLateFeeMultiplier();
}

class RegularBook implements BookType {
    @Override public String getType() { return "Regular"; }
    @Override public int getBorrowDuration() { return 14; }
    @Override public double getLateFeeMultiplier() { return 1.0; }
}

class ReferenceBook implements BookType {
    @Override public String getType() { return "Reference"; }
    @Override public int getBorrowDuration() { return 7; }
    @Override public double getLateFeeMultiplier() { return 2.0; }
}

class BookFactory {
    public static BookType createBookType(String type) {
        switch(type.toLowerCase()) {
            case "reference":
                return new ReferenceBook();
            case "regular":
            default:
                return new RegularBook();
        }
    }
}

// ==================== DECORATOR PATTERN ====================

/* -------------------- USER FEATURES DECORATOR -------------------- */
interface UserFeatures {
    void displayFeatures();
    boolean canBorrow();
    boolean canAddFavorites();
    boolean canAddBooks();
    int getMaxBooks();
    double getFineRate();
}

class BasicUserFeatures implements UserFeatures {
    private UserType userType;
    
    public BasicUserFeatures(UserType userType) {
        this.userType = userType;
    }
    
    @Override
    public void displayFeatures() {
        System.out.println("User Type: " + userType.getType());
        System.out.println("Max Books: " + userType.getMaxBooks());
        System.out.println("Fine Rate: Rs." + userType.getFineRate() + "/day");
    }
    
    @Override
    public boolean canBorrow() {
        return userType.getMaxBooks() > 0;
    }
    
    @Override
    public boolean canAddFavorites() {
        return userType.getMaxBooks() > 0;
    }
    
    @Override
    public boolean canAddBooks() {
        return userType.getMaxBooks() > 0;
    }
    
    @Override
    public int getMaxBooks() {
        return userType.getMaxBooks();
    }
    
    @Override
    public double getFineRate() {
        return userType.getFineRate();
    }
}

abstract class UserFeaturesDecorator implements UserFeatures {
    protected UserFeatures decoratedFeatures;
    
    public UserFeaturesDecorator(UserFeatures features) {
        this.decoratedFeatures = features;
    }
    
    @Override
    public void displayFeatures() {
        decoratedFeatures.displayFeatures();
    }
    
    @Override
    public boolean canBorrow() {
        return decoratedFeatures.canBorrow();
    }
    
    @Override
    public boolean canAddFavorites() {
        return decoratedFeatures.canAddFavorites();
    }
    
    @Override
    public boolean canAddBooks() {
        return decoratedFeatures.canAddBooks();
    }
    
    @Override
    public int getMaxBooks() {
        return decoratedFeatures.getMaxBooks();
    }
    
    @Override
    public double getFineRate() {
        return decoratedFeatures.getFineRate();
    }
}

class PremiumFeaturesDecorator extends UserFeaturesDecorator {
    public PremiumFeaturesDecorator(UserFeatures features) {
        super(features);
    }
    
    @Override
    public void displayFeatures() {
        super.displayFeatures();
        System.out.println("🎖️  Premium Features:");
        System.out.println("   - Extended borrowing (5 books max)");
        System.out.println("   - Reduced fine rate (50% discount)");
        System.out.println("   - Priority reservations");
    }
    
    @Override
    public int getMaxBooks() {
        return 5; // Premium users can borrow more
    }
    
    @Override
    public double getFineRate() {
        return super.getFineRate() * 0.5; // 50% discount for premium
    }
}

/* -------------------- BOOK DECORATOR -------------------- */
interface BookDisplay {
    void display();
    String getDisplayString();
}

class BasicBookDisplay implements BookDisplay {
    private String title;
    private String author;
    private int copies;
    
    public BasicBookDisplay(String title, String author, int copies) {
        this.title = title;
        this.author = author;
        this.copies = copies;
    }
    
    @Override
    public void display() {
        System.out.println(getDisplayString());
    }
    
    @Override
    public String getDisplayString() {
        return title + " by " + author + " (" + copies + " copies)";
    }
}

abstract class BookDecorator implements BookDisplay {
    protected BookDisplay decoratedBook;
    
    public BookDecorator(BookDisplay book) {
        this.decoratedBook = book;
    }
    
    @Override
    public void display() {
        decoratedBook.display();
    }
    
    @Override
    public String getDisplayString() {
        return decoratedBook.getDisplayString();
    }
}

class AvailableStatusDecorator extends BookDecorator {
    private boolean isAvailable;
    
    public AvailableStatusDecorator(BookDisplay book, boolean isAvailable) {
        super(book);
        this.isAvailable = isAvailable;
    }
    
    @Override
    public void display() {
        String status = isAvailable ? "✅ Available" : "❌ Out of Stock";
        System.out.println(getDisplayString() + " - " + status);
    }
    
    @Override
    public String getDisplayString() {
        String status = isAvailable ? "✅ Available" : "❌ Out of Stock";
        return decoratedBook.getDisplayString() + " - " + status;
    }
}

class CategoryDecorator extends BookDecorator {
    private String category;
    
    public CategoryDecorator(BookDisplay book, String category) {
        super(book);
        this.category = category;
    }
    
    @Override
    public void display() {
        System.out.println(getDisplayString());
    }
    
    @Override
    public String getDisplayString() {
        return decoratedBook.getDisplayString() + " [" + category + "]";
    }
}

// ==================== OBSERVER PATTERN ====================

/* -------------------- LIBRARY EVENT OBSERVER -------------------- */
interface LibraryObserver {
    void update(String eventType, String message);
}

class EmailNotificationObserver implements LibraryObserver {
    private String email;
    
    public EmailNotificationObserver(String email) {
        this.email = email;
    }
    
    @Override
    public void update(String eventType, String message) {
        System.out.println("📧 Email sent to " + email + ":");
        System.out.println("   Event: " + eventType);
        System.out.println("   Message: " + message);
        System.out.println();
    }
}

class ConsoleNotificationObserver implements LibraryObserver {
    @Override
    public void update(String eventType, String message) {
        System.out.println("📢 System Notification:");
        System.out.println("   ⚡ Event: " + eventType);
        System.out.println("   📝 Message: " + message);
        System.out.println();
    }
}

class FineNotificationObserver implements LibraryObserver {
    @Override
    public void update(String eventType, String message) {
        if (eventType.contains("FINE") || eventType.contains("OVERDUE")) {
            System.out.println("💰 Fine Alert:");
            System.out.println("   ⚠️  " + message);
            System.out.println();
        }
    }
}

class LibraryEventManager {
    private static LibraryEventManager instance;
    private List<LibraryObserver> observers = new ArrayList<>();
    
    private LibraryEventManager() {}
    
    public static LibraryEventManager getInstance() {
        if (instance == null) {
            instance = new LibraryEventManager();
        }
        return instance;
    }
    
    public void addObserver(LibraryObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(LibraryObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(String eventType, String message) {
        for (LibraryObserver observer : observers) {
            observer.update(eventType, message);
        }
    }
}

// ==================== MODIFIED USER CLASS ====================

/* -------------------- USER CLASS (UPDATED WITH PATTERNS) -------------------- */
class User {
    String username;
    String password;
    List<String> favourites = new ArrayList<>();
    List<String> borrowedBooks = new ArrayList<>();
    Map<String, String> borrowDates = new HashMap<>();
    Map<String, String> dueDates = new HashMap<>();
    Map<String, BookType> bookTypes = new HashMap<>();
    double totalFine = 0.0;
    UserType userType;
    UserFeatures userFeatures;
    
    User(String username, String password, String type) {
        this.username = username;
        this.password = password;
        this.userType = UserFactory.createUserType(type);
        
        // Create basic features
        UserFeatures basicFeatures = new BasicUserFeatures(this.userType);
        
        // Apply premium decorator for admin
        if (username.equals("admin")) {
            this.userFeatures = new PremiumFeaturesDecorator(basicFeatures);
        } else {
            this.userFeatures = basicFeatures;
        }
        
        // Register for notifications
        LibraryEventManager eventManager = LibraryEventManager.getInstance();
        eventManager.addObserver(new ConsoleNotificationObserver());
        if (!username.equals("admin")) {
            eventManager.addObserver(new FineNotificationObserver());
        }
    }
    
    void displayUserInfo() {
        System.out.println("\n👤 User Information:");
        System.out.println("Username: " + username);
        userFeatures.displayFeatures();
    }
    
    // ADD THESE METHODS TO ACCESS USERFEATURES
    boolean canBorrow() {
        return userFeatures.canBorrow();
    }
    
    boolean canAddFavorites() {
        return userFeatures.canAddFavorites();
    }
    
    boolean canAddBooks() {
        return userFeatures.canAddBooks();
    }
    
    int getMaxBooks() {
        return userFeatures.getMaxBooks();
    }
    
    double getFineRate() {
        return userFeatures.getFineRate();
    }
}

// ==================== MODIFIED MAIN SYSTEM ====================

/* -------------------- MAIN SYSTEM (UPDATED) -------------------- */
public class LibraryManagementSystem {

    static Scanner sc = new Scanner(System.in);
    static Map<String, User> users = new HashMap<>();
    static Map<String, Integer> books = new HashMap<>();
    static Map<String, BookType> bookTypes = new HashMap<>();
    static List<String> categories = new ArrayList<>();
    static Map<String, List<String>> categoryBooks = new HashMap<>();
    static User loggedInUser = null;
    static LibraryEventManager eventManager = LibraryEventManager.getInstance();

    /* -------------------- MAIN METHOD -------------------- */
    public static void main(String[] args) {
        // Initialize event manager with observers
        initializeObservers();
        
        // Initialize categories
        categories.add("Programming");
        categories.add("Science");
        categories.add("Fiction");
        categories.add("History");
        
        // Initialize books with Factory Pattern
        initializeBooks();
        
        // Create users with Factory Pattern
        users.put("admin", new User("admin", "admin123", "authorized"));
        users.put("user1", new User("user1", "pass123", "authorized"));
        users.put("guest", new User("guest", "guest123", "unauthorized"));

        System.out.println("📚 ===== LIBRARY MANAGEMENT SYSTEM (WITH DESIGN PATTERNS) =====");
        
        while (true) {
            System.out.println("\n📋 === MAIN MENU ===");
            System.out.println("1️⃣   Admin Login");
            System.out.println("2️⃣   User Login");
            System.out.println("3️⃣   Register New User");
            System.out.println("4️⃣   View Books (Decorator Pattern)");
            System.out.println("5️⃣   Browse by Category");
            System.out.println("6️⃣   Display User Info (Factory Pattern)");
            System.out.println("7️⃣   Exit");
            System.out.print("👉 Enter choice: ");

            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> adminMenu();
                case 2 -> userLogin();
                case 3 -> registerUser();
                case 4 -> viewBooksWithDecorator();
                case 5 -> browseByCategory();
                case 6 -> displayUserInfo();
                case 7 -> {
                    eventManager.notifyObservers("SYSTEM_SHUTDOWN", "Library system is shutting down");
                    System.out.println("👋 Thank you for using the system!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }
    
    private static void initializeObservers() {
        // Add global observers
        eventManager.addObserver(new ConsoleNotificationObserver());
        eventManager.addObserver(new EmailNotificationObserver("admin@library.com"));
    }
    
    private static void initializeBooks() {
        // Using Factory Pattern to create different book types
        BookType regular = BookFactory.createBookType("regular");
        BookType reference = BookFactory.createBookType("reference");
        
        books.put("Java Programming", 3);
        bookTypes.put("Java Programming", regular);
        
        books.put("Python Basics", 2);
        bookTypes.put("Python Basics", regular);
        
        books.put("Data Structures", 1);
        bookTypes.put("Data Structures", reference);
        
        books.put("Operating Systems", 2);
        bookTypes.put("Operating Systems", regular);
        
        books.put("Database Management", 1);
        bookTypes.put("Database Management", reference);
        
        // Categorize books
        categoryBooks.put("Programming", Arrays.asList("Java Programming", "Python Basics", "Data Structures"));
        categoryBooks.put("Science", Arrays.asList("Physics Fundamentals", "Chemistry Basics"));
        categoryBooks.put("Fiction", new ArrayList<>());
        categoryBooks.put("History", new ArrayList<>());
    }
    
    private static void displayUserInfo() {
        if (loggedInUser != null) {
            loggedInUser.displayUserInfo();
        } else {
            System.out.println("❌ Please login first!");
        }
    }

    /* -------------------- ADMIN MENU -------------------- */
    static void adminMenu() {
        System.out.print("👤 Enter admin username: ");
        String u = sc.nextLine();
        System.out.print("🔒 Enter admin password: ");
        String p = sc.nextLine();

        User admin = users.get(u);
        if (u.equals("admin") && admin != null && admin.password.equals(p)) {
            loggedInUser = admin;
            eventManager.notifyObservers("ADMIN_LOGIN", "Admin logged in successfully");
            System.out.println("✅ Login successful! Welcome Admin!");
            
            while (true) {
                System.out.println("\n⚙️  --- ADMIN MENU ---");
                System.out.println("1️⃣   Add User (Factory Pattern)");
                System.out.println("2️⃣   Remove User");
                System.out.println("3️⃣   View All Users");
                System.out.println("4️⃣   Add New Book (Factory Pattern)");
                System.out.println("5️⃣   Remove Book");
                System.out.println("6️⃣   View All Books (Decorator Pattern)");
                System.out.println("7️⃣   View User Fines");
                System.out.println("8️⃣   Send Notification (Observer Pattern)");
                System.out.println("9️⃣   Logout");

                int choice = getIntInput();
                
                switch (choice) {
                    case 1 -> addUserWithFactory();
                    case 2 -> removeUser();
                    case 3 -> viewUsers();
                    case 4 -> addNewBookWithFactory();
                    case 5 -> removeBook();
                    case 6 -> viewBooksWithDecorator();
                    case 7 -> viewUserFines();
                    case 8 -> sendNotification();
                    case 9 -> {
                        eventManager.notifyObservers("ADMIN_LOGOUT", "Admin logged out");
                        loggedInUser = null;
                        System.out.println("👋 Logging out...");
                        return;
                    }
                    default -> System.out.println("❌ Invalid choice!");
                }
            }
        } else {
            System.out.println("❌ Invalid admin credentials!");
        }
    }
    
    static void addUserWithFactory() {
        System.out.print("👤 Enter new username: ");
        String username = sc.nextLine();
        if (users.containsKey(username)) {
            System.out.println("❌ User already exists!");
            return;
        }
        System.out.print("🔒 Enter password: ");
        String password = sc.nextLine();
        
        System.out.println("Select user type (Factory Pattern):");
        System.out.println("1. Authorized User (can borrow books)");
        System.out.println("2. Unauthorized User (can only browse)");
        System.out.print("Choice: ");
        int typeChoice = getIntInput();
        
        String userType = (typeChoice == 1) ? "authorized" : "unauthorized";
        User newUser = new User(username, password, userType);
        users.put(username, newUser);
        
        eventManager.notifyObservers("USER_REGISTERED", 
            "New " + userType + " user registered: " + username);
        System.out.println("✅ User added successfully with " + userType + " privileges!");
    }
    
    static void addNewBookWithFactory() {
        System.out.print("📖 Enter book title: ");
        String title = sc.nextLine();
        
        if (books.containsKey(title)) {
            System.out.print("📚 Book exists! Add more copies? (yes/no): ");
            String response = sc.nextLine().toLowerCase();
            if (response.equals("yes") || response.equals("y")) {
                System.out.print("➕ Enter additional copies: ");
                int copies = getIntInput();
                books.put(title, books.get(title) + copies);
                System.out.println("✅ Added " + copies + " more copies of \"" + title + "\"");
            }
            return;
        }
        
        System.out.print("✍️  Enter author name: ");
        String author = sc.nextLine();
        
        System.out.print("🔢 Enter number of copies: ");
        int copies = getIntInput();
        
        System.out.println("Select book type (Factory Pattern):");
        System.out.println("1. Regular Book (14 days borrow)");
        System.out.println("2. Reference Book (7 days borrow, higher fines)");
        System.out.print("Choice: ");
        int typeChoice = getIntInput();
        
        BookType bookType = (typeChoice == 2) ? 
            BookFactory.createBookType("reference") : 
            BookFactory.createBookType("regular");
        
        System.out.println("📂 Available categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.print("📁 Select category number: ");
        int catChoice = getIntInput();
        
        String category = "Uncategorized";
        if (catChoice > 0 && catChoice <= categories.size()) {
            category = categories.get(catChoice - 1);
        }
        
        books.put(title + " by " + author, copies);
        bookTypes.put(title + " by " + author, bookType);
        categoryBooks.get(category).add(title + " by " + author);
        
        eventManager.notifyObservers("NEW_BOOK_ADDED", 
            "New " + bookType.getType() + " book added: " + title);
        System.out.println("✅ New " + bookType.getType() + " book added successfully!");
    }
    
    static void sendNotification() {
        System.out.println("📢 Send System Notification (Observer Pattern):");
        System.out.print("Enter event type: ");
        String eventType = sc.nextLine();
        System.out.print("Enter message: ");
        String message = sc.nextLine();
        
        eventManager.notifyObservers(eventType, message);
        System.out.println("✅ Notification sent to all observers!");
    }

    static void removeUser() {
        System.out.print("🗑️  Enter username to remove: ");
        String username = sc.nextLine();
        if (username.equals("admin")) {
            System.out.println("❌ Cannot remove admin!");
            return;
        }
        if (users.remove(username) != null) {
            eventManager.notifyObservers("USER_REMOVED", "User removed: " + username);
            System.out.println("✅ User removed successfully!");
        } else {
            System.out.println("❌ User not found!");
        }
    }

    static void viewUsers() {
        System.out.println("👥 Registered Users:");
        if (users.isEmpty()) {
            System.out.println("📭 No users registered!");
            return;
        }
        int i = 1;
        for (String u : users.keySet()) {
            User user = users.get(u);
            System.out.println(i + ". 👤 " + u + 
                " | 📚 Borrowed: " + user.borrowedBooks.size() +
                " | ❤️  Favorites: " + user.favourites.size() +
                " | 💰 Fine: Rs." + user.totalFine +
                " | Type: " + user.userType.getType());
            i++;
        }
    }

    static void removeBook() {
        viewBooksWithDecorator();
        if (books.isEmpty()) {
            System.out.println("📭 No books available!");
            return;
        }
        
        System.out.print("🗑️  Enter book number to remove: ");
        int choice = getIntInput();
        
        List<String> bookList = new ArrayList<>(books.keySet());
        if (choice >= 1 && choice <= bookList.size()) {
            String bookToRemove = bookList.get(choice - 1);
            books.remove(bookToRemove);
            bookTypes.remove(bookToRemove);
            
            for (List<String> catBooks : categoryBooks.values()) {
                catBooks.removeIf(b -> b.equals(bookToRemove));
            }
            
            for (User user : users.values()) {
                user.favourites.remove(bookToRemove);
                user.borrowedBooks.remove(bookToRemove);
            }
            
            eventManager.notifyObservers("BOOK_REMOVED", "Book removed: " + bookToRemove);
            System.out.println("✅ Book removed successfully!");
        } else {
            System.out.println("❌ Invalid book number!");
        }
    }

    static void viewUserFines() {
        System.out.println("💰 === USER FINES ===");
        boolean hasFines = false;
        
        for (User user : users.values()) {
            if (user.totalFine > 0) {
                System.out.println("👤 " + user.username + ": Rs." + user.totalFine);
                hasFines = true;
            }
        }
        
        if (!hasFines) {
            System.out.println("✅ No users have outstanding fines!");
        }
    }

    /* -------------------- USER REGISTRATION -------------------- */
    static void registerUser() {
        System.out.print("👤 Enter username: ");
        String username = sc.nextLine();
        if (users.containsKey(username)) {
            System.out.println("❌ Username already exists!");
            return;
        }
        System.out.print("🔒 Enter password: ");
        String password = sc.nextLine();
        
        System.out.println("Select user type:");
        System.out.println("1. Authorized User");
        System.out.println("2. Unauthorized User");
        System.out.print("Choice: ");
        int typeChoice = getIntInput();
        
        String userType = (typeChoice == 1) ? "authorized" : "unauthorized";
        users.put(username, new User(username, password, userType));
        
        eventManager.notifyObservers("USER_REGISTERED", 
            "New user registered: " + username + " (" + userType + ")");
        System.out.println("✅ Registration successful! You can now login.");
    }

    /* -------------------- USER LOGIN -------------------- */
    static void userLogin() {
        System.out.print("👤 Enter username: ");
        String username = sc.nextLine();
        System.out.print("🔒 Enter password: ");
        String password = sc.nextLine();

        User user = users.get(username);

        if (user != null && user.password.equals(password)) {
            loggedInUser = user;
            eventManager.notifyObservers("USER_LOGIN", "User logged in: " + username);
            System.out.println("✅ Login successful! Welcome " + username + "!");
            userMenu();
        } else {
            System.out.println("❌ Invalid credentials!");
        }
    }

    /* -------------------- USER MENU -------------------- */
    static void userMenu() {
        while (true) {
            System.out.println("\n👤 === USER MENU ===");
            System.out.println("1️⃣   View All Books (Decorator Pattern)");
            System.out.println("2️⃣   Browse by Category");
            System.out.println("3️⃣   Search Books");
            System.out.println("4️⃣   Borrow Book (Factory Pattern)");
            System.out.println("5️⃣   Return Book");
            System.out.println("6️⃣   Add to Favourites");
            System.out.println("7️⃣   View Favourites");
            System.out.println("8️⃣   View Borrowed Books");
            System.out.println("9️⃣   Check Fine Details");
            System.out.println("🔟   Display My Info (Factory Pattern)");
            System.out.println("1️⃣1️⃣ Logout");
            System.out.print("👉 Enter choice: ");

            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> viewBooksWithDecorator();
                case 2 -> browseByCategory();
                case 3 -> searchBooks();
                case 4 -> borrowBookWithFactory();
                case 5 -> returnBook();
                case 6 -> addToFavourites();
                case 7 -> viewFavourites();
                case 8 -> viewBorrowedBooks();
                case 9 -> checkFineDetails();
                case 10 -> loggedInUser.displayUserInfo();
                case 11 -> {
                    eventManager.notifyObservers("USER_LOGOUT", "User logged out: " + loggedInUser.username);
                    loggedInUser = null;
                    System.out.println("👋 Logged out successfully!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    /* -------------------- BOOK FUNCTIONS WITH DECORATOR -------------------- */
    static void viewBooksWithDecorator() {
        System.out.println("\n📚 === AVAILABLE BOOKS (Decorator Pattern) ===");
        if (books.isEmpty()) {
            System.out.println("📭 No books available!");
            return;
        }
        
        List<String> bookList = new ArrayList<>(books.keySet());
        for (int i = 0; i < bookList.size(); i++) {
            String book = bookList.get(i);
            int copies = books.get(book);
            boolean isAvailable = copies > 0;
            
            // Create basic book display
            BookDisplay basicBook = new BasicBookDisplay(
                book.split(" by ")[0],
                book.split(" by ").length > 1 ? book.split(" by ")[1] : "Unknown",
                copies
            );
            
            // Apply decorators
            BookDisplay decoratedBook = new AvailableStatusDecorator(basicBook, isAvailable);
            
            // Add category decorator if available
            String category = getBookCategory(book);
            if (!category.isEmpty()) {
                decoratedBook = new CategoryDecorator(decoratedBook, category);
            }
            
            // Display with decorator
            System.out.print((i + 1) + ". ");
            decoratedBook.display();
        }
    }
    
    static String getBookCategory(String bookName) {
        for (Map.Entry<String, List<String>> entry : categoryBooks.entrySet()) {
            if (entry.getValue().contains(bookName)) {
                return entry.getKey();
            }
        }
        return "";
    }

    static void browseByCategory() {
        System.out.println("\n📂 === BROWSE BY CATEGORY ===");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        
        System.out.print("👉 Select category number: ");
        int choice = getIntInput();
        
        if (choice >= 1 && choice <= categories.size()) {
            String category = categories.get(choice - 1);
            List<String> catBooks = categoryBooks.get(category);
            
            System.out.println("\n📚 Books in " + category + ":");
            if (catBooks.isEmpty()) {
                System.out.println("📭 No books in this category!");
            } else {
                for (int i = 0; i < catBooks.size(); i++) {
                    String book = catBooks.get(i);
                    int copies = books.getOrDefault(book, 0);
                    boolean isAvailable = copies > 0;
                    
                    BookDisplay basicBook = new BasicBookDisplay(
                        book.split(" by ")[0],
                        book.split(" by ")[1],
                        copies
                    );
                    BookDisplay decoratedBook = new AvailableStatusDecorator(basicBook, isAvailable);
                    
                    System.out.print((i + 1) + ". ");
                    decoratedBook.display();
                }
            }
        } else {
            System.out.println("❌ Invalid category!");
        }
    }

    static void searchBooks() {
        System.out.print("🔍 Enter search term: ");
        String term = sc.nextLine().toLowerCase();
        
        System.out.println("\n🔎 Search Results:");
        List<String> results = new ArrayList<>();
        List<String> bookList = new ArrayList<>(books.keySet());
        
        for (String book : bookList) {
            if (book.toLowerCase().contains(term)) {
                results.add(book);
            }
        }
        
        if (results.isEmpty()) {
            System.out.println("📭 No books found!");
        } else {
            for (int i = 0; i < results.size(); i++) {
                String book = results.get(i);
                int copies = books.get(book);
                boolean isAvailable = copies > 0;
                
                BookDisplay basicBook = new BasicBookDisplay(
                    book.split(" by ")[0],
                    book.split(" by ")[1],
                    copies
                );
                BookDisplay decoratedBook = new AvailableStatusDecorator(basicBook, isAvailable);
                
                System.out.print((i + 1) + ". ");
                decoratedBook.display();
            }
        }
    }

    static void borrowBookWithFactory() {
        if (!loggedInUser.canBorrow()) {
            System.out.println("❌ Unauthorized users cannot borrow books!");
            return;
        }
        
        System.out.println("\n📚 Available Books for Borrowing:");
        List<String> availableBooks = new ArrayList<>();
        List<String> bookList = new ArrayList<>(books.keySet());
        
        for (String book : bookList) {
            if (books.get(book) > 0) {
                availableBooks.add(book);
            }
        }
        
        if (availableBooks.isEmpty()) {
            System.out.println("❌ No books available for borrowing!");
            return;
        }
        
        for (int i = 0; i < availableBooks.size(); i++) {
            String book = availableBooks.get(i);
            BookType bookType = bookTypes.get(book);
            System.out.println((i + 1) + ". " + book + 
                " (" + books.get(book) + " copies) [" + bookType.getType() + "]");
        }
        
        System.out.print("👉 Enter book number to borrow: ");
        int choice = getIntInput();
        
        if (choice >= 1 && choice <= availableBooks.size()) {
            String bookName = availableBooks.get(choice - 1);
            
            if (loggedInUser.borrowedBooks.contains(bookName)) {
                System.out.println("❌ You already have this book borrowed!");
                return;
            }
            
            if (loggedInUser.borrowedBooks.size() >= loggedInUser.getMaxBooks()) {
                System.out.println("❌ You can only borrow " + loggedInUser.getMaxBooks() + " books at a time!");
                return;
            }
            
            // Get book type using Factory Pattern
            BookType bookType = bookTypes.get(bookName);
            
            // Borrow the book
            books.put(bookName, books.get(bookName) - 1);
            loggedInUser.borrowedBooks.add(bookName);
            loggedInUser.bookTypes.put(bookName, bookType);
            
            // Set dates based on book type
            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(bookType.getBorrowDuration());
            loggedInUser.borrowDates.put(bookName, today.toString());
            loggedInUser.dueDates.put(bookName, dueDate.toString());
            
            eventManager.notifyObservers("BOOK_BORROWED", 
                loggedInUser.username + " borrowed: " + bookName + " (Due: " + dueDate + ")");
            
            System.out.println("✅ Book borrowed successfully!");
            System.out.println("📅 Borrow Date: " + today);
            System.out.println("⏰ Due Date: " + dueDate + " (" + bookType.getBorrowDuration() + " days)");
            System.out.println("⚠️  Fine: Rs." + (loggedInUser.getFineRate() * bookType.getLateFeeMultiplier()) + 
                " per day after due date");
        } else {
            System.out.println("❌ Invalid book number!");
        }
    }

    static void returnBook() {
        if (loggedInUser.borrowedBooks.isEmpty()) {
            System.out.println("📭 No borrowed books to return!");
            return;
        }
        
        System.out.println("\n📚 Your Borrowed Books:");
        for (int i = 0; i < loggedInUser.borrowedBooks.size(); i++) {
            String book = loggedInUser.borrowedBooks.get(i);
            String dueDate = loggedInUser.dueDates.get(book);
            BookType bookType = loggedInUser.bookTypes.get(book);
            System.out.println((i + 1) + ". " + book + " | Due: " + dueDate + " [" + bookType.getType() + "]");
        }
        
        System.out.print("👉 Enter book number to return: ");
        int choice = getIntInput();
        
        if (choice >= 1 && choice <= loggedInUser.borrowedBooks.size()) {
            String bookName = loggedInUser.borrowedBooks.remove(choice - 1);
            
            // Return book to library
            books.put(bookName, books.get(bookName) + 1);
            
            // Calculate fine if overdue
            String dueDateStr = loggedInUser.dueDates.get(bookName);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            LocalDate returnDate = LocalDate.now();
            BookType bookType = loggedInUser.bookTypes.get(bookName);
            
            double fine = 0;
            if (returnDate.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
                double dailyRate = loggedInUser.getFineRate() * bookType.getLateFeeMultiplier();
                fine = daysLate * dailyRate;
                loggedInUser.totalFine += fine;
                
                eventManager.notifyObservers("LATE_RETURN", 
                    loggedInUser.username + " returned " + bookName + " late by " + 
                    daysLate + " days. Fine: Rs." + fine);
                
                System.out.println("⚠️  Late return by " + daysLate + " days!");
                System.out.println("💰 Fine charged: Rs." + fine + 
                    " (Rate: Rs." + dailyRate + "/day for " + bookType.getType() + " book)");
            }
            
            // Remove dates and book type
            loggedInUser.borrowDates.remove(bookName);
            loggedInUser.dueDates.remove(bookName);
            loggedInUser.bookTypes.remove(bookName);
            
            eventManager.notifyObservers("BOOK_RETURNED", 
                loggedInUser.username + " returned: " + bookName);
            
            System.out.println("✅ Book returned successfully!");
            System.out.println("📅 Return Date: " + returnDate);
            System.out.println("💰 Total Fine: Rs." + loggedInUser.totalFine);
        } else {
            System.out.println("❌ Invalid book number!");
        }
    }

    static void addToFavourites() {
        // FIXED: Now using loggedInUser.canAddFavorites() method
        if (!loggedInUser.canAddFavorites()) {
            System.out.println("❌ Unauthorized users cannot add favorites!");
            return;
        }
        
        viewBooksWithDecorator();
        if (books.isEmpty()) {
            System.out.println("📭 No books available!");
            return;
        }
        
        System.out.print("❤️  Enter book number to add to favourites: ");
        int choice = getIntInput();
        
        List<String> bookList = new ArrayList<>(books.keySet());
        if (choice >= 1 && choice <= bookList.size()) {
            String bookName = bookList.get(choice - 1);
            if (!loggedInUser.favourites.contains(bookName)) {
                loggedInUser.favourites.add(bookName);
                eventManager.notifyObservers("BOOK_FAVORITED", 
                    loggedInUser.username + " favorited: " + bookName);
                System.out.println("✅ Added to favourites: " + bookName);
            } else {
                System.out.println("✅ Already in favourites!");
            }
        } else {
            System.out.println("❌ Invalid book number!");
        }
    }

    static void viewFavourites() {
        if (loggedInUser.favourites.isEmpty()) {
            System.out.println("📭 No favourites!");
            return;
        }
        System.out.println("\n❤️  Your Favourites:");
        for (int i = 0; i < loggedInUser.favourites.size(); i++) {
            String book = loggedInUser.favourites.get(i);
            int copies = books.getOrDefault(book, 0);
            boolean isAvailable = copies > 0;
            
            BookDisplay basicBook = new BasicBookDisplay(
                book.split(" by ")[0],
                book.split(" by ")[1],
                copies
            );
            BookDisplay decoratedBook = new AvailableStatusDecorator(basicBook, isAvailable);
            
            System.out.print((i + 1) + ". ");
            decoratedBook.display();
        }
    }

    static void viewBorrowedBooks() {
        if (loggedInUser.borrowedBooks.isEmpty()) {
            System.out.println("📭 No borrowed books!");
            return;
        }
        
        System.out.println("\n📚 Your Borrowed Books:");
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < loggedInUser.borrowedBooks.size(); i++) {
            String book = loggedInUser.borrowedBooks.get(i);
            String dueDateStr = loggedInUser.dueDates.get(book);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            BookType bookType = loggedInUser.bookTypes.get(book);
            
            long daysRemaining = ChronoUnit.DAYS.between(today, dueDate);
            String status;
            
            if (daysRemaining < 0) {
                long daysLate = -daysRemaining;
                double dailyRate = loggedInUser.getFineRate() * bookType.getLateFeeMultiplier();
                double fine = daysLate * dailyRate;
                status = "❌ OVERDUE by " + daysLate + " days (Fine: Rs." + fine + ")";
            } else if (daysRemaining == 0) {
                status = "⚠️  DUE TODAY";
            } else if (daysRemaining <= 3) {
                status = "⚠️  Due in " + daysRemaining + " days";
            } else {
                status = "✅ Due in " + daysRemaining + " days";
            }
            
            System.out.println((i + 1) + ". " + book + " [" + bookType.getType() + "]");
            System.out.println("   📅 Borrowed: " + loggedInUser.borrowDates.get(book));
            System.out.println("   ⏰ Due: " + dueDateStr + " - " + status);
        }
    }

    static void checkFineDetails() {
        System.out.println("\n💰 === YOUR FINE DETAILS ===");
        System.out.println("Total Fine: Rs." + loggedInUser.totalFine);
        
        if (loggedInUser.totalFine > 0) {
            System.out.println("⚠️  You have outstanding fines! Please pay at the library counter.");
        } else {
            System.out.println("✅ No outstanding fines!");
        }
        
        // Show potential fines for borrowed books
        LocalDate today = LocalDate.now();
        boolean hasPotentialFines = false;
        
        for (String book : loggedInUser.borrowedBooks) {
            String dueDateStr = loggedInUser.dueDates.get(book);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            BookType bookType = loggedInUser.bookTypes.get(book);
            
            if (today.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS.between(dueDate, today);
                double dailyRate = loggedInUser.getFineRate() * bookType.getLateFeeMultiplier();
                double fine = daysLate * dailyRate;
                System.out.println("\n📖 " + book + " [" + bookType.getType() + "]:");
                System.out.println("   ⏰ Days Late: " + daysLate);
                System.out.println("   📊 Daily Rate: Rs." + dailyRate + "/day");
                System.out.println("   💰 Potential Fine: Rs." + fine);
                hasPotentialFines = true;
            }
        }
        
        if (!hasPotentialFines && loggedInUser.totalFine == 0) {
            System.out.println("🎉 All books are on time!");
        }
    }

    static int getIntInput() {
        while (true) {
            try {
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.print("❌ Invalid input! Please enter a number: ");
                sc.nextLine();
            }
        }
    }
} // <-- ADDED THIS CLOSING BRACE FOR THE MAIN CLASS