import java.util.*;

/* -------------------- USER CLASS -------------------- */
class User {
    String username;
    String password;
    List<String> favourites = new ArrayList<>();
    List<String> borrowedBooks = new ArrayList<>();
    Map<String, String> borrowDates = new HashMap<>(); // Track borrow dates
    Map<String, String> dueDates = new HashMap<>();    // Track due dates
    double totalFine = 0.0;
    static final double FINE_PER_DAY = 10.0;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

/* -------------------- MAIN SYSTEM -------------------- */
public class LibraryManagementSystem {

    static Scanner sc = new Scanner(System.in);
    static Map<String, User> users = new HashMap<>();
    static Map<String, Integer> books = new HashMap<>(); // Book name -> Available copies
    static List<String> categories = new ArrayList<>();
    static Map<String, List<String>> categoryBooks = new HashMap<>();
    static User loggedInUser = null;

    /* -------------------- MAIN METHOD -------------------- */
    public static void main(String[] args) {

        // Initialize categories
        categories.add("Programming");
        categories.add("Science");
        categories.add("Fiction");
        categories.add("History");
        
        // Initialize books with copies
        books.put("Java Programming", 3);
        books.put("Python Basics", 2);
        books.put("Data Structures", 1);
        books.put("Operating Systems", 2);
        books.put("Database Management", 1);
        books.put("Computer Networks", 2);
        books.put("Physics Fundamentals", 1);
        books.put("Chemistry Basics", 2);
        
        // Categorize books
        categoryBooks.put("Programming", Arrays.asList("Java Programming", "Python Basics", "Data Structures"));
        categoryBooks.put("Science", Arrays.asList("Physics Fundamentals", "Chemistry Basics"));
        categoryBooks.put("Fiction", new ArrayList<>());
        categoryBooks.put("History", new ArrayList<>());

        // Default admin user
        users.put("admin", new User("admin", "admin123"));

        System.out.println("📚 ===== LIBRARY MANAGEMENT SYSTEM =====");
        
        while (true) {
            System.out.println("\n📋 === MAIN MENU ===");
            System.out.println("1️⃣   Admin Login");
            System.out.println("2️⃣   User Login");
            System.out.println("3️⃣   Register New User");
            System.out.println("4️⃣   View Books (Unauthorized)");
            System.out.println("5️⃣   Browse by Category");
            System.out.println("6️⃣   Exit");
            System.out.print("👉 Enter choice: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1 -> adminMenu();
                case 2 -> userLogin();
                case 3 -> registerUser();
                case 4 -> viewBooks();
                case 5 -> browseByCategory();
                case 6 -> {
                    System.out.println("👋 Thank you for using the system!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice!");
            }
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
            System.out.println("✅ Login successful! Welcome Admin!");
            while (true) {
                System.out.println("\n⚙️  --- ADMIN MENU ---");
                System.out.println("1️⃣   Add User");
                System.out.println("2️⃣   Remove User");
                System.out.println("3️⃣   View All Users");
                System.out.println("4️⃣   Add New Book");
                System.out.println("5️⃣   Remove Book");
                System.out.println("6️⃣   View All Books");
                System.out.println("7️⃣   View User Fines");
                System.out.println("8️⃣   Logout");

                int choice;
                try {
                    choice = sc.nextInt();
                    sc.nextLine();
                } catch (Exception e) {
                    System.out.println("❌ Invalid input!");
                    sc.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1 -> addUser();
                    case 2 -> removeUser();
                    case 3 -> viewUsers();
                    case 4 -> addNewBook();
                    case 5 -> removeBook();
                    case 6 -> viewBooks();
                    case 7 -> viewUserFines();
                    case 8 -> {
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

    static void addUser() {
        System.out.print("👤 Enter new username: ");
        String username = sc.nextLine();
        if (users.containsKey(username)) {
            System.out.println("❌ User already exists!");
            return;
        }
        System.out.print("🔒 Enter password: ");
        String password = sc.nextLine();

        users.put(username, new User(username, password));
        System.out.println("✅ User added successfully!");
    }

    static void removeUser() {
        System.out.print("🗑️  Enter username to remove: ");
        String username = sc.nextLine();
        if (username.equals("admin")) {
            System.out.println("❌ Cannot remove admin!");
            return;
        }
        if (users.remove(username) != null) {
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
                " | 💰 Fine: Rs." + user.totalFine);
            i++;
        }
    }

    static void addNewBook() {
        System.out.print("📖 Enter book title: ");
        String title = sc.nextLine();
        
        if (books.containsKey(title)) {
            System.out.print("📚 Book exists! Add more copies? (yes/no): ");
            String response = sc.nextLine().toLowerCase();
            if (response.equals("yes") || response.equals("y")) {
                System.out.print("➕ Enter additional copies: ");
                int copies = sc.nextInt();
                sc.nextLine();
                books.put(title, books.get(title) + copies);
                System.out.println("✅ Added " + copies + " more copies of \"" + title + "\"");
            }
            return;
        }
        
        System.out.print("✍️  Enter author name: ");
        String author = sc.nextLine();
        
        System.out.print("🔢 Enter number of copies: ");
        int copies = sc.nextInt();
        sc.nextLine();
        
        System.out.println("📂 Available categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.print("📁 Select category number (or 0 for new category): ");
        int catChoice = sc.nextInt();
        sc.nextLine();
        
        String category;
        if (catChoice == 0) {
            System.out.print("📂 Enter new category name: ");
            category = sc.nextLine();
            categories.add(category);
            categoryBooks.put(category, new ArrayList<>());
        } else if (catChoice > 0 && catChoice <= categories.size()) {
            category = categories.get(catChoice - 1);
        } else {
            category = "Uncategorized";
        }
        
        books.put(title + " by " + author, copies);
        categoryBooks.get(category).add(title + " by " + author);
        System.out.println("✅ New book added successfully!");
    }

    static void removeBook() {
        viewBooks();
        if (books.isEmpty()) {
            System.out.println("📭 No books available!");
            return;
        }
        
        System.out.print("🗑️  Enter book number to remove: ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        List<String> bookList = new ArrayList<>(books.keySet());
        if (choice >= 1 && choice <= bookList.size()) {
            String bookToRemove = bookList.get(choice - 1);
            books.remove(bookToRemove);
            
            // Remove from categories
            for (List<String> catBooks : categoryBooks.values()) {
                catBooks.removeIf(b -> b.equals(bookToRemove));
            }
            
            // Remove from user favorites and borrowed books
            for (User user : users.values()) {
                user.favourites.remove(bookToRemove);
                user.borrowedBooks.remove(bookToRemove);
            }
            
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
        
        users.put(username, new User(username, password));
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
            System.out.println("1️⃣   View All Books");
            System.out.println("2️⃣   Browse by Category");
            System.out.println("3️⃣   Search Books");
            System.out.println("4️⃣   Borrow Book");
            System.out.println("5️⃣   Return Book");
            System.out.println("6️⃣   Add to Favourites");
            System.out.println("7️⃣   View Favourites");
            System.out.println("8️⃣   View Borrowed Books");
            System.out.println("9️⃣   Check Fine Details");
            System.out.println("🔟   Logout");
            System.out.print("👉 Enter choice: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("❌ Invalid input!");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1 -> viewBooks();
                case 2 -> browseByCategory();
                case 3 -> searchBooks();
                case 4 -> borrowBook();
                case 5 -> returnBook();
                case 6 -> addToFavourites();
                case 7 -> viewFavourites();
                case 8 -> viewBorrowedBooks();
                case 9 -> checkFineDetails();
                case 10 -> {
                    loggedInUser = null;
                    System.out.println("👋 Logged out successfully!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    /* -------------------- BOOK FUNCTIONS -------------------- */
    static void viewBooks() {
        System.out.println("\n📚 === AVAILABLE BOOKS ===");
        if (books.isEmpty()) {
            System.out.println("📭 No books available!");
            return;
        }
        
        List<String> bookList = new ArrayList<>(books.keySet());
        for (int i = 0; i < bookList.size(); i++) {
            String book = bookList.get(i);
            int copies = books.get(book);
            String status = (copies > 0) ? "✅ Available (" + copies + " copies)" : "❌ Out of Stock";
            System.out.println((i + 1) + ". " + book + " - " + status);
        }
    }

    static void browseByCategory() {
        System.out.println("\n📂 === BROWSE BY CATEGORY ===");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        
        System.out.print("👉 Select category number: ");
        int choice = sc.nextInt();
        sc.nextLine();
        
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
                    String status = (copies > 0) ? "✅ Available" : "❌ Out of Stock";
                    System.out.println((i + 1) + ". " + book + " - " + status);
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
                String status = (copies > 0) ? "✅ Available" : "❌ Out of Stock";
                System.out.println((i + 1) + ". " + book + " - " + status);
            }
        }
    }

    static void borrowBook() {
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
            System.out.println((i + 1) + ". " + book + " (" + books.get(book) + " copies)");
        }
        
        System.out.print("👉 Enter book number to borrow: ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        if (choice >= 1 && choice <= availableBooks.size()) {
            String bookName = availableBooks.get(choice - 1);
            
            // Check if user already has this book
            if (loggedInUser.borrowedBooks.contains(bookName)) {
                System.out.println("❌ You already have this book borrowed!");
                return;
            }
            
            // Check borrowing limit (max 3)
            if (loggedInUser.borrowedBooks.size() >= 3) {
                System.out.println("❌ You can only borrow 3 books at a time!");
                return;
            }
            
            // Borrow the book
            books.put(bookName, books.get(bookName) - 1);
            loggedInUser.borrowedBooks.add(bookName);
            
            // Set dates
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate dueDate = today.plusDays(14); // 2 weeks due
            loggedInUser.borrowDates.put(bookName, today.toString());
            loggedInUser.dueDates.put(bookName, dueDate.toString());
            
            System.out.println("✅ Book borrowed successfully!");
            System.out.println("📅 Borrow Date: " + today);
            System.out.println("⏰ Due Date: " + dueDate);
            System.out.println("⚠️  Fine: Rs." + loggedInUser.FINE_PER_DAY + " per day after due date");
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
            System.out.println((i + 1) + ". " + book + " | Due: " + dueDate);
        }
        
        System.out.print("👉 Enter book number to return: ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        if (choice >= 1 && choice <= loggedInUser.borrowedBooks.size()) {
            String bookName = loggedInUser.borrowedBooks.remove(choice - 1);
            
            // Return book to library
            books.put(bookName, books.get(bookName) + 1);
            
            // Calculate fine if overdue
            String dueDateStr = loggedInUser.dueDates.get(bookName);
            java.time.LocalDate dueDate = java.time.LocalDate.parse(dueDateStr);
            java.time.LocalDate returnDate = java.time.LocalDate.now();
            
            double fine = 0;
            if (returnDate.isAfter(dueDate)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
                fine = daysLate * loggedInUser.FINE_PER_DAY;
                loggedInUser.totalFine += fine;
                System.out.println("⚠️  Late return by " + daysLate + " days!");
                System.out.println("💰 Fine charged: Rs." + fine);
            }
            
            // Remove dates
            loggedInUser.borrowDates.remove(bookName);
            loggedInUser.dueDates.remove(bookName);
            
            System.out.println("✅ Book returned successfully!");
            System.out.println("📅 Return Date: " + returnDate);
            System.out.println("💰 Total Fine: Rs." + loggedInUser.totalFine);
        } else {
            System.out.println("❌ Invalid book number!");
        }
    }

    static void addToFavourites() {
        viewBooks();
        if (books.isEmpty()) {
            System.out.println("📭 No books available!");
            return;
        }
        
        System.out.print("❤️  Enter book number to add to favourites: ");
        int choice = sc.nextInt();
        sc.nextLine();
        
        List<String> bookList = new ArrayList<>(books.keySet());
        if (choice >= 1 && choice <= bookList.size()) {
            String bookName = bookList.get(choice - 1);
            if (!loggedInUser.favourites.contains(bookName)) {
                loggedInUser.favourites.add(bookName);
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
            String status = (copies > 0) ? "✅ Available" : "❌ Out of Stock";
            System.out.println((i + 1) + ". " + book + " - " + status);
        }
    }

    static void viewBorrowedBooks() {
        if (loggedInUser.borrowedBooks.isEmpty()) {
            System.out.println("📭 No borrowed books!");
            return;
        }
        
        System.out.println("\n📚 Your Borrowed Books:");
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (int i = 0; i < loggedInUser.borrowedBooks.size(); i++) {
            String book = loggedInUser.borrowedBooks.get(i);
            String dueDateStr = loggedInUser.dueDates.get(book);
            java.time.LocalDate dueDate = java.time.LocalDate.parse(dueDateStr);
            
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);
            String status;
            
            if (daysRemaining < 0) {
                long daysLate = -daysRemaining;
                double fine = daysLate * loggedInUser.FINE_PER_DAY;
                status = "❌ OVERDUE by " + daysLate + " days (Fine: Rs." + fine + ")";
            } else if (daysRemaining == 0) {
                status = "⚠️  DUE TODAY";
            } else if (daysRemaining <= 3) {
                status = "⚠️  Due in " + daysRemaining + " days";
            } else {
                status = "✅ Due in " + daysRemaining + " days";
            }
            
            System.out.println((i + 1) + ". " + book);
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
        java.time.LocalDate today = java.time.LocalDate.now();
        boolean hasPotentialFines = false;
        
        for (String book : loggedInUser.borrowedBooks) {
            String dueDateStr = loggedInUser.dueDates.get(book);
            java.time.LocalDate dueDate = java.time.LocalDate.parse(dueDateStr);
            
            if (today.isAfter(dueDate)) {
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
                double fine = daysLate * loggedInUser.FINE_PER_DAY;
                System.out.println("\n📖 " + book + ":");
                System.out.println("   ⏰ Days Late: " + daysLate);
                System.out.println("   💰 Potential Fine: Rs." + fine);
                hasPotentialFines = true;
            }
        }
        
        if (!hasPotentialFines && loggedInUser.totalFine == 0) {
            System.out.println("🎉 All books are on time!");
        }
    }
}