// --- Global State ---
const API_BASE = '/api';
let currentUser = null;

// --- Auth Functions ---
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const res = await fetch(`${API_BASE}/login`, {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();

        if (data.success) {
            localStorage.setItem('user', JSON.stringify({
                username: data.username,
                type: data.userType
            }));
            window.location.href = 'dashboard.html';
        } else {
            showAlert(data.message || 'Login failed', 'error');
        }
    } catch (err) {
        showAlert('Server error. Ensure backend is running.', 'error');
    }
});

function checkAuth() {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
        window.location.href = 'index.html';
        return;
    }
    currentUser = JSON.parse(userStr);
    const greeting = document.getElementById('userGreeting');
    if (greeting) greeting.textContent = `Hello, ${currentUser.username} (${currentUser.type})`;

    // Show Admin specific UI
    if (currentUser.username === 'admin') {
        document.getElementById('adminAddUserCard')?.classList.remove('hidden');
        document.getElementById('adminUsersCard')?.classList.remove('hidden');
    }
}

function logout() {
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

// --- Dashboard Logic ---
async function loadDashboard() {
    loadStats();
    loadBooks();
    loadMyBooks();
    loadMyFavorites();
    if (currentUser && currentUser.username === 'admin') {
        loadAllUsersForAdmin();
    }
}

async function loadStats() {
    const res = await fetch(`${API_BASE}/stats`);
    const stats = await res.json();
    document.getElementById('statsBooks').textContent = stats.totalBooks;
    document.getElementById('statsActive').textContent = stats.activeBorrows;
    document.getElementById('statsUsers').textContent = stats.totalUsers;
}

async function loadBooks() {
    // Pass username to get private books visibility
    const res = await fetch(`${API_BASE}/books?username=${currentUser.username}`);
    const books = await res.json();
    const tbody = document.querySelector('#booksTable tbody');
    tbody.innerHTML = '';

    // Fetch user favorites to check status
    const userRes = await fetch(`${API_BASE}/users?username=${currentUser.username}`);
    const userData = await userRes.json();
    const favs = userData.favourites || [];

    books.forEach(book => {
        const tr = document.createElement('tr');
        const isAvailable = book.copies > 0;
        const statusBadge = isAvailable
            ? `<span class="badge badge-available">Available (${book.copies})</span>`
            : `<span class="badge badge-out">Out of Stock</span>`;

        const isFav = favs.contains ? favs.contains(book.title) : favs.includes(book.title);
        const favIcon = isFav ? '❤️' : '🤍';

        const visibilityBadge = book.visibility === 'PRIVATE' ? '🔒' : '🌐';

        tr.innerHTML = `
            <td>
                <span style="cursor:pointer;" onclick="toggleFavorite('${book.title}')">${favIcon}</span>
                <strong>${book.title}</strong>
                <small style="color:#888">${visibilityBadge}</small>
            </td>
            <td>${book.type}</td>
            <td>${book.owner || 'System'}</td>
            <td>${statusBadge}</td>
            <td>
                <button 
                    onclick="borrowBook('${book.title}')" 
                    class="btn btn-sm ${isAvailable ? '' : 'btn-secondary'}"
                    style="padding: 5px 10px; font-size: 0.8rem;"
                    ${!isAvailable ? 'disabled' : ''}>
                    Borrow
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function filterBooks() {
    const input = document.getElementById('searchBooks').value.toLowerCase();
    const rows = document.querySelectorAll('#booksTable tbody tr');
    rows.forEach(row => {
        const title = row.querySelector('td').textContent.toLowerCase();
        row.style.display = title.includes(input) ? '' : 'none';
    });
}

async function loadMyBooks() {
    const res = await fetch(`${API_BASE}/users?username=${currentUser.username}`);
    const userData = await res.json();

    const container = document.getElementById('myBooksList');
    if (userData.borrowedBooks && userData.borrowedBooks.length > 0) {
        container.innerHTML = `<ul style="padding-left: 20px;">
            ${userData.borrowedBooks.map(book => {
            const due = userData.dueDates ? userData.dueDates[book] : 'N/A';
            const borrowed = userData.borrowDates ? userData.borrowDates[book] : 'N/A';

            // Color code due date if overdue (client side check)
            let dateStyle = "color: #555";
            if (due !== 'N/A') {
                const dueDate = new Date(due);
                const today = new Date();
                // reset time portion for fair comparison
                today.setHours(0, 0, 0, 0);
                if (today > dueDate) dateStyle = "color: red; font-weight: bold;";
            }

            return `
                <li style="margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <strong>${book}</strong>
                         <button onclick="returnBook('${book}')" class="btn btn-danger" style="padding: 2px 8px; font-size: 0.7rem; width: auto;">Return</button>
                    </div>
                    <div style="font-size: 0.8rem; color: #666; margin-top: 2px;">
                        Borrowed: ${borrowed} | <span style="${dateStyle}">Due: ${due}</span>
                    </div>
                </li>
            `;
        }).join('')}
        </ul>`;
    } else {
        container.innerHTML = '<p>No books borrowed yet.</p>';
    }
}

async function loadMyFavorites() {
    const res = await fetch(`${API_BASE}/users?username=${currentUser.username}`);
    const userData = await res.json();

    const container = document.getElementById('myFavoritesList');
    if (userData.favourites && userData.favourites.length > 0) {
        container.innerHTML = `<ul style="padding-left: 20px;">
            ${userData.favourites.map(book => `
                <li style="margin-bottom: 5px;">
                    <span>❤️ ${book}</span>
                </li>
            `).join('')}
        </ul>`;
    } else {
        container.innerHTML = '<p>No favorites yet.</p>';
    }
}

async function loadAllUsersForAdmin() {
    try {
        const res = await fetch(`${API_BASE}/users`);
        const usersMap = await res.json();

        const tbody = document.querySelector('#usersTable tbody');
        if (!tbody) return;
        tbody.innerHTML = '';

        Object.values(usersMap).forEach(user => {
            const tr = document.createElement('tr');
            let borrowedDisplay = user.borrowedBooks && user.borrowedBooks.length > 0 ? user.borrowedBooks.join(', ') : '<em>None</em>';
            const fine = user.totalFine ? `$${user.totalFine.toFixed(2)}` : '$0.00';
            const fineStyle = user.totalFine > 0 ? 'color: red; font-weight: bold;' : 'color: green;';

            tr.innerHTML = `
                <td>${user.username}</td>
                <td>${user.userType && user.userType.type ? user.userType.type : (user.userType || 'Unknown')}</td>
                <td>${borrowedDisplay}</td>
                <td style="${fineStyle}">${fine}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error("Failed to load users for admin", e);
    }
}

// --- Actions ---
async function borrowBook(bookTitle) {
    if (!currentUser) return;
    const res = await fetch(`${API_BASE}/borrow`, {
        method: 'POST',
        body: JSON.stringify({ username: currentUser.username, bookTitle: bookTitle })
    });
    const data = await res.json();
    if (data.success) {
        showAlert(data.message, 'success');
        loadBooks();
        loadMyBooks();
        loadStats();
    } else {
        showAlert(data.message, 'error');
    }
}

async function returnBook(bookTitle) {
    if (!currentUser) return;
    const res = await fetch(`${API_BASE}/return`, {
        method: 'POST',
        body: JSON.stringify({ username: currentUser.username, bookTitle: bookTitle })
    });
    const data = await res.json();
    if (data.success) {
        showAlert(data.message, 'success'); // Server sends back "Book returned. Fine: $..." message in 'message' field
        loadBooks();
        loadMyBooks();
        loadStats();
    } else {
        showAlert(data.message || 'Failed to return', 'error');
    }
}

async function toggleFavorite(bookTitle) {
    if (!currentUser) return;
    const res = await fetch(`${API_BASE}/favorites`, {
        method: 'POST',
        body: JSON.stringify({ username: currentUser.username, bookTitle: bookTitle })
    });
    const data = await res.json();
    if (data.success) {
        loadBooks(); // refresh icons
        loadMyFavorites(); // refresh list
    }
}

// Add Book (For everyone)
document.getElementById('addBookForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    // Allow everyone to add books now

    const bookData = {
        title: document.getElementById('newBookTitle').value,
        author: document.getElementById('newBookAuthor').value,
        copies: parseInt(document.getElementById('newBookCopies').value),
        type: document.getElementById('newBookType').value,
        category: document.getElementById('newBookCategory').value,
        owner: currentUser.username, // Set owner
        visibility: document.getElementById('newBookVisibility').value // Set visibility
    };

    const res = await fetch(`${API_BASE}/books`, {
        method: 'POST',
        body: JSON.stringify(bookData)
    });
    const data = await res.json();

    if (data.success) {
        showAlert('Book added successfully!', 'success');
        loadBooks();
        loadStats();
        e.target.reset();
    } else {
        showAlert('Failed to add book.', 'error');
    }
});

// Admin Add User
document.getElementById('addUserForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (currentUser.username !== 'admin') return;

    const userData = {
        username: document.getElementById('newUserName').value,
        password: document.getElementById('newUserPass').value,
        userType: document.getElementById('newUserType').value
    };

    const res = await fetch(`${API_BASE}/register`, {
        method: 'POST',
        body: JSON.stringify(userData)
    });
    const data = await res.json();

    if (data.success) {
        showAlert(data.message, 'success');
        if (typeof loadAllUsersForAdmin === 'function') loadAllUsersForAdmin();
        loadStats();
        e.target.reset();
    } else {
        showAlert(data.message, 'error');
    }
});

// --- Utils ---
function showAlert(msg, type) {
    const alertBox = document.getElementById('alertBox');
    if (!alertBox) return;
    alertBox.textContent = msg;
    alertBox.className = `alert ${type}`;
    alertBox.style.display = 'block';
    setTimeout(() => { alertBox.style.display = 'none'; }, 3000);
}
