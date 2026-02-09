import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class LibraryHttpServer {
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final MongoDBManager dbManager = MongoDBManager.getInstance();

    public static void main(String[] args) throws IOException {
        // Initialize DB Data
        loadData();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Serve Static Files (HTML, CSS, JS)
        server.createContext("/", new StaticFileHandler());

        // API Endpoints
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/books", new BooksHandler());
        server.createContext("/api/borrow", new BorrowHandler());
        server.createContext("/api/return", new ReturnHandler());
        server.createContext("/api/users", new UsersHandler());
        server.createContext("/api/register", new RegisterHandler()); // Added Register Handler
        server.createContext("/api/favorites", new FavoritesHandler()); // Added Favorites Handler
        server.createContext("/api/stats", new StatsHandler());

        server.setExecutor(null); // creates a default executor
        System.out.println("🌍 Web Server running at http://localhost:" + PORT + "/index.html");
        server.start();
    }

    private static void loadData() {
        if (!dbManager.isConnected()) {
            System.out.println("⚠️ Database not connected.");
        } else {
             // Ensure defaults exist if DB is empty
            dbManager.initializeDefaultData();
        }
    }

    // --- Handlers ---

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            // Security: Prevent accessing files outside public folder
            if (path.contains("..")) {
                sendResponse(exchange, 403, "Forbidden");
                return;
            }

            Path file = Paths.get("public" + path);
            if (Files.exists(file) && !Files.isDirectory(file)) {
                String mimeType = "text/plain";
                if (path.endsWith(".html")) mimeType = "text/html";
                else if (path.endsWith(".css")) mimeType = "text/css";
                else if (path.endsWith(".js")) mimeType = "application/javascript";
                
                exchange.getResponseHeaders().set("Content-Type", mimeType);
                byte[] bytes = Files.readAllBytes(file);
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                sendResponse(exchange, 404, "File Not Found: " + path);
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> creds = gson.fromJson(body, Map.class);
                
                User user = dbManager.loadUser(creds.get("username"));
                Map<String, Object> response = new HashMap<>();

                if (user != null && user.password.equals(creds.get("password"))) {
                    response.put("success", true);
                    response.put("userType", user.userType.getType());
                    response.put("username", user.username);
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid credentials");
                }
                sendJsonResponse(exchange, response);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }
    
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = gson.fromJson(body, Map.class);
                
                String username = data.get("username");
                String password = data.get("password");
                String userType = data.get("userType");
                
                // Check if user exists
                if (dbManager.loadUser(username) != null) {
                     Map<String, Object> response = new HashMap<>();
                     response.put("success", false);
                     response.put("message", "User already exists");
                     sendJsonResponse(exchange, response);
                     return;
                }
                
                // User class is available in the package
                User newUser = new User(username, password, userType);
                boolean success = dbManager.saveUser(newUser);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", success);
                response.put("message", success ? "User registered successfully" : "Failed to register user");
                
                sendJsonResponse(exchange, response);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class BooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String currentUser = null;
                if (query != null && query.startsWith("username=")) {
                    currentUser = query.split("=")[1];
                }

                // Load raw documents
                List<org.bson.Document> allBooks = dbManager.loadBooksWithDetails();
                List<Map<String, Object>> responseList = new ArrayList<>();

                for (org.bson.Document doc : allBooks) {
                    String owner = doc.getString("owner");
                    String visibility = doc.getString("visibility");
                    
                    // Default legacy books (no visibility set) are treated as PUBLIC
                    boolean isPublic = visibility == null || "PUBLIC".equalsIgnoreCase(visibility);
                    boolean isOwner = currentUser != null && currentUser.equals(owner);

                    if (isPublic || isOwner || "admin".equals(currentUser)) {
                        Map<String, Object> bookInfo = new HashMap<>();
                        bookInfo.put("title", doc.getString("title"));
                        bookInfo.put("copies", doc.getInteger("copies"));
                        bookInfo.put("type", doc.getString("bookType"));
                        bookInfo.put("category", doc.getString("category"));
                        bookInfo.put("visibility", visibility != null ? visibility : "PUBLIC");
                        bookInfo.put("owner", owner != null ? owner : "System");
                        responseList.add(bookInfo);
                    }
                }
                sendJsonResponse(exchange, responseList);

            } else if ("POST".equals(exchange.getRequestMethod())) {
               String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
               Map<String, Object> data = gson.fromJson(body, Map.class);
               
               String title = (String) data.get("title");
               String author = (String) data.get("author");
               int copies = ((Number) data.get("copies")).intValue(); 
               String type = (String) data.get("type");
               String category = (String) data.get("category");
               String owner = (String) data.get("owner");
               String visibility = (String) data.get("visibility"); // PUBLIC or PRIVATE
               
               if (owner == null) owner = "admin";
               if (visibility == null) visibility = "PUBLIC";
               
               String fullTitle = title + " by " + author;
               boolean success = dbManager.saveBookWithVisibility(fullTitle, copies, type, category, owner, visibility);
               
               Map<String, Object> response = new HashMap<>();
               response.put("success", success);
               sendJsonResponse(exchange, response);
            }
        }
    }

    static class FavoritesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = gson.fromJson(body, Map.class);
                
                String username = data.get("username");
                String bookTitle = data.get("bookTitle");
                
                boolean success = dbManager.toggleFavorite(username, bookTitle);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", success);
                
                sendJsonResponse(exchange, response);
            }
        }
    }
    
    static class BorrowHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = gson.fromJson(body, Map.class);
                
                String username = data.get("username");
                String bookTitle = data.get("bookTitle");
                
                User user = dbManager.loadUser(username);
                Map<String, Integer> books = dbManager.loadAllBooks();
                Map<String, String> bookTypes = dbManager.loadBookTypes();
                
                Map<String, Object> response = new HashMap<>();
                
                if (user != null && books.getOrDefault(bookTitle, 0) > 0) {
                    int maxBooks = user.userType.getType().equalsIgnoreCase("authorized") ? 3 : 0;
                     if(username.equals("admin")) maxBooks = 10;

                    if (user.borrowedBooks.size() >= maxBooks) {
                        response.put("success", false);
                        response.put("message", "Max book limit reached.");
                    } else if (user.borrowedBooks.contains(bookTitle)) {
                         response.put("success", false);
                        response.put("message", "Already borrowed this book.");
                    } else {
                        // Perform Borrow
                        user.borrowedBooks.add(bookTitle);
                        
                        // Date Logic
                        String typeStr = bookTypes.getOrDefault(bookTitle, "Regular");
                        int borrowDays = "Reference".equalsIgnoreCase(typeStr) ? 5 : 14; // Simple logic matching factory
                        
                        java.time.LocalDate today = java.time.LocalDate.now();
                        java.time.LocalDate due = today.plusDays(borrowDays);
                        
                        user.borrowDates.put(bookTitle, today.toString());
                        user.dueDates.put(bookTitle, due.toString());
                        
                        // Decrease copy
                        int newCopies = books.get(bookTitle) - 1;
                        dbManager.updateBookCopies(bookTitle, newCopies);
                        dbManager.saveUser(user);
                        dbManager.saveTransaction(username, bookTitle, "BORROW", today.toString());
                        
                        response.put("success", true);
                        response.put("message", "Book borrowed! Due date: " + due.toString());
                    }
                } else {
                     response.put("success", false);
                     response.put("message", "Book not available or user not found.");
                }
                sendJsonResponse(exchange, response);
            }
        }
    }
    
     static class ReturnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> data = gson.fromJson(body, Map.class);
                
                String username = data.get("username");
                String bookTitle = data.get("bookTitle");
                
                User user = dbManager.loadUser(username);
                Map<String, Integer> books = dbManager.loadAllBooks();
                
                Map<String, Object> response = new HashMap<>();
                
                if (user != null && user.borrowedBooks.contains(bookTitle)) {
                    // Fine Calculation
                    String dueStr = user.dueDates.get(bookTitle);
                    double fineAmount = 0.0;
                    
                    if (dueStr != null) {
                        java.time.LocalDate due = java.time.LocalDate.parse(dueStr);
                        java.time.LocalDate today = java.time.LocalDate.now();
                        
                        if (today.isAfter(due)) {
                            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(due, today);
                            fineAmount = daysOverdue * 1.0; // $1.00 per day fine
                            user.totalFine += fineAmount;
                        }
                    }

                    user.borrowedBooks.remove(bookTitle);
                    user.borrowDates.remove(bookTitle);
                    user.dueDates.remove(bookTitle);
                    
                    int newCopies = books.getOrDefault(bookTitle, 0) + 1;
                    dbManager.updateBookCopies(bookTitle, newCopies);
                    dbManager.saveUser(user);
                     
                    String today = java.time.LocalDate.now().toString();
                    dbManager.saveTransaction(username, bookTitle, "RETURN", today);

                    response.put("success", true);
                    response.put("message", fineAmount > 0 ? "Book returned. Fine incurred: $" + fineAmount : "Book returned successfully.");
                } else {
                    response.put("success", false);
                    response.put("message", "User does not have this book.");
                }
                sendJsonResponse(exchange, response);
            }
        }
    }

     static class UsersHandler implements HttpHandler {
         @Override
         public void handle(HttpExchange exchange) throws IOException {
             if ("GET".equals(exchange.getRequestMethod())) {
                 String query = exchange.getRequestURI().getQuery();
                 // If query contains "username=...", returns specific user details
                 if (query != null && query.startsWith("username=")) {
                     String username = query.split("=")[1];
                     User user = dbManager.loadUser(username);
                     if (user != null) {
                         sendJsonResponse(exchange, user);
                     } else {
                         sendResponse(exchange, 404, "User not found");
                     }
                 } else {
                     // Return all users (for admin)
                     sendJsonResponse(exchange, dbManager.loadAllUsers());
                 }
             }
         }
     }
     
     static class StatsHandler implements HttpHandler {
         @Override
         public void handle(HttpExchange exchange) throws IOException {
              Map<String, Long> stats = new HashMap<>();
              Map<String, User> users = dbManager.loadAllUsers();
              Map<String, Integer> books = dbManager.loadAllBooks();
              
              stats.put("totalUsers", (long) users.size());
              stats.put("totalBooks", (long) books.size());
              long borrowedCount = users.values().stream().mapToLong(u -> u.borrowedBooks.size()).sum();
              stats.put("activeBorrows", borrowedCount);
              
              sendJsonResponse(exchange, stats);
         }
     }

    private static void sendJsonResponse(HttpExchange exchange, Object responseObj) throws IOException {
        String jsonDetails = gson.toJson(responseObj);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = jsonDetails.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
