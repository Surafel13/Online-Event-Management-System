const API_BASE = (window.location.port === '5500' || window.location.port === '3000')
    ? 'http://localhost:9090/OnlineEventManagement/api'
    : window.location.pathname.includes('/UI/')
        ? '../api'
        : '/OnlineEventManagement/api';
let currentUser = null;

// Initialization
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadEvents();
    setupEventListeners();
});

async function checkAuth() {
    try {
        const res = await fetch(`${API_BASE}/auth/check`, { credentials: 'include' });
        if (res.ok) {
            const data = await res.json();
            currentUser = { role: data.role };
            updateUIForAuth();
        }
    } catch (err) {
        console.error("Auth check failed:", err);
    }
}

function setupEventListeners() {
    // Auth Forms
    document.getElementById('loginForm')?.addEventListener('submit', handleLogin);
    document.getElementById('registerForm')?.addEventListener('submit', handleRegister);
    document.getElementById('createEventForm')?.addEventListener('submit', handleCreateEvent);
}

// Navigation Logic
function showView(viewId) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.getElementById(viewId)?.classList.add('active');

    if (viewId === 'adminView') loadAdminDashboard();
    if (viewId === 'ticketsView') loadUserTickets();
}

// Data Loading
async function loadEvents() {
    try {
        const res = await fetch(`${API_BASE}/events`, { credentials: 'include' });
        const events = await res.json();
        const container = document.getElementById('eventList');

        if (!events || events.length === 0) {
            container.innerHTML = '<p>No events found.</p>';
            return;
        }

        container.innerHTML = events.map(e => `
            <div class="event-card">
                <h3>${e.title}</h3>
                <div class="event-info"><i class="fa fa-calendar"></i> ${e.date}</div>
                <div class="event-info"><i class="fa fa-clock"></i> ${e.time}</div>
                <div class="event-info"><i class="fa fa-location-dot"></i> ${e.location}</div>
                <div class="event-info" style="margin-top:1rem; font-weight:700">
                    <span style="color: ${e.capacity > 0 ? '#22c55e' : '#ef4444'}">
                        ${e.capacity > 0 ? e.capacity + ' seats available' : 'SOLD OUT'}
                    </span>
                </div>
                <button class="btn-book" ${e.capacity <= 0 ? 'disabled' : ''} onclick="bookTicket(${e.id})">
                    ${e.capacity > 0 ? 'Book My Spot' : 'Waitlist Only'}
                </button>
            </div>
        `).join('');
    } catch (err) {
        showToast('Failed to load events', 'error');
    }
}

// Authentication
async function handleLogin(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const params = new URLSearchParams(formData);

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            body: params,
            credentials: 'include'
        });
        const data = await res.json();

        if (res.ok) {
            currentUser = { role: data.role };
            showToast('Welcome back!', 'success');
            updateUIForAuth();
            showView('eventsView');
        } else {
            showToast(data.error || 'Login failed', 'error');
        }
    } catch (err) {
        showToast('Server connection error', 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const params = new URLSearchParams(formData);

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            body: params,
            credentials: 'include'
        });
        const data = await res.json();

        if (res.ok) {
            currentUser = { role: data.role };
            showToast('Welcome to the platform!', 'success');
            updateUIForAuth();
            showView('eventsView');
        } else {
            showToast(data.error || 'Registration failed', 'error');
        }
    } catch (err) {
        showToast('Server connection error', 'error');
    }
}

async function logout() {
    await fetch(`${API_BASE}/auth/logout`, { method: 'POST', credentials: 'include' });
    currentUser = null;
    updateUIForAuth();
    showToast('Logged out successfully');
    showView('eventsView');
}

// UI Updates
function updateUIForAuth() {
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    const logoutBtn = document.getElementById('logoutBtn');
    const adminBtn = document.getElementById('adminBtn');
    const ticketsBtn = document.getElementById('myTicketsBtn');

    if (currentUser) {
        loginBtn.style.display = 'none';
        registerBtn.style.display = 'none';
        logoutBtn.style.display = 'inline-block';
        ticketsBtn.style.display = 'inline-block';
        if (currentUser.role === 'ADMIN') {
            adminBtn.style.display = 'inline-block';
        }
    } else {
        loginBtn.style.display = 'inline-block';
        registerBtn.style.display = 'inline-block';
        logoutBtn.style.display = 'none';
        adminBtn.style.display = 'none';
        ticketsBtn.style.display = 'none';
    }
}

// Booking
async function bookTicket(eventId) {
    if (!currentUser) {
        showToast('Please login to book tickets', 'error');
        showView('loginView');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `eventId=${eventId}`,
            credentials: 'include'
        });
        const data = await res.json();

        if (res.ok) {
            showToast(`Success! Ticket ID: ${data.ticketId}`, 'success');
            loadEvents();
            showView('ticketsView'); // Redirect to see the new ticket
        } else {
            showToast(data.error || 'Booking failed', 'error');
        }
    } catch (err) {
        showToast('Error booking ticket', 'error');
    }
}

async function loadUserTickets() {
    const list = document.getElementById('ticketList');
    try {
        const res = await fetch(`${API_BASE}/bookings`, { credentials: 'include' });
        const tickets = await res.json();

        if (tickets.length === 0) {
            list.innerHTML = '<p style="color: var(--text-muted)">You haven\'t booked any tickets yet.</p>';
            return;
        }

        list.innerHTML = tickets.map(t => `
            <div class="ticket-card">
                <div class="ticket-main">
                    <div class="ticket-header">Electronic Admission Ticket</div>
                    <div class="ticket-title">${t.eventTitle}</div>
                    
                    <div class="ticket-details">
                        <div>
                            <div class="ticket-label">Attendee</div>
                            <div class="ticket-value">${t.userName}</div>
                        </div>
                        <div>
                            <div class="ticket-label">Email</div>
                            <div class="ticket-value">${t.userEmail}</div>
                        </div>
                        <div>
                            <div class="ticket-label">Location</div>
                            <div class="ticket-value">${t.eventLocation}</div>
                        </div>
                        <div>
                            <div class="ticket-label">Booking Date</div>
                            <div class="ticket-value">${formatDate(t.bookingDate)}</div>
                        </div>
                    </div>
                    
                    <div class="ticket-label" style="margin-top:1.5rem">Ticket ID</div>
                    <div class="ticket-id">${t.ticketId}</div>
                </div>
                <div class="ticket-stub">
                    ${t.status}
                </div>
            </div>
        `).join('');
    } catch (err) {
        showToast('Failed to load tickets', 'error');
    }
}

async function loadAdminDashboard() {
    const body = document.getElementById('adminTableBody');
    const statsBody = document.getElementById('adminStatsBody');

    try {
        // Load Bookings
        const resBookings = await fetch(`${API_BASE}/bookings`, { credentials: 'include' });
        const bookings = await resBookings.json();

        body.innerHTML = bookings.map(b => `
            <tr>
                <td><code style="color: var(--primary)">${b.ticketId}</code></td>
                <td>
                    <div style="font-weight:600">${b.userName}</div>
                    <div style="font-size:0.8rem; color:var(--text-muted)">${b.userEmail}</div>
                </td>
                <td>${b.eventTitle}</td>
                <td>${formatDate(b.bookingDate)}</td>
                <td><span class="status-badge status-${b.status}">${b.status}</span></td>
            </tr>
        `).join('');

        // Load Event Stats
        const resEvents = await fetch(`${API_BASE}/events`, { credentials: 'include' });
        const events = await resEvents.json();

        statsBody.innerHTML = events.map(e => `
            <tr>
                <td><strong>${e.title}</strong></td>
                <td><span style="color: var(--primary); font-weight:700">${e.registeredCount}</span> registered</td>
                <td><span style="color: ${e.capacity > 0 ? '#22c55e' : '#ef4444'}; font-weight:700">${e.capacity}</span> remaining</td>
                <td>${e.registeredCount + e.capacity} seats</td>
            </tr>
        `).join('');

    } catch (err) {
        showToast('Failed to load admin data', 'error');
    }
}


// Modal Logic
function openCreateModal() { document.getElementById('createModal').style.display = 'flex'; }
function closeModal() { document.getElementById('createModal').style.display = 'none'; }

async function handleCreateEvent(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const params = new URLSearchParams(formData);

    try {
        const res = await fetch(`${API_BASE}/events`, {
            method: 'POST',
            body: params,
            credentials: 'include'
        });
        const data = await res.json();

        if (res.ok) {
            showToast('Event created successfully!', 'success');
            closeModal();
            loadEvents();
            loadAdminDashboard();
        } else {
            showToast(data.error || 'Failed to create event', 'error');
        }
    } catch (err) {
        showToast('Connection error', 'error');
    }
}

// Helpers
function formatDate(dateInput) {
    if (!dateInput) return 'N/A';

    // Handle Gson timestamp format (e.g., "Dec 24, 2025, 9:35:26 AM") or standard ISO
    const date = new Date(dateInput);

    if (isNaN(date.getTime())) {
        // Fallback for some specific formats if new Date() fails
        console.warn("Date parsing failed for:", dateInput);
        return dateInput;
    }

    return date.toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function showToast(msg, type = 'success') {
    const toast = document.getElementById('toast');
    toast.innerText = msg;
    toast.style.display = 'block';
    toast.style.background = type === 'success' ? 'var(--success)' : 'var(--error)';
    setTimeout(() => { toast.style.display = 'none'; }, 3000);
}
