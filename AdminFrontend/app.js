// Configuration
// Defaulting to the deployed API URL, but can be switched to localhost for local testing
const API_BASE = 'https://campusexchange-0uan.onrender.com/api/v1';

// DOM Elements
const loginView = document.getElementById('login-view');
const dashboardView = document.getElementById('dashboard-view');
const loginForm = document.getElementById('login-form');
const createBetForm = document.getElementById('create-bet-form');
const createStockForm = document.getElementById('create-stock-form');
const logoutBtn = document.getElementById('logout-btn');

// State
let token = localStorage.getItem('adminToken') || '';

// Initialize
if (token) {
    showDashboard();
}

function showDashboard() {
    loginView.classList.remove('active');
    dashboardView.classList.add('active');
}

function showLogin() {
    dashboardView.classList.remove('active');
    loginView.classList.add('active');
    localStorage.removeItem('adminToken');
    token = '';
}

function displayMsg(elementId, msg, isError = false) {
    const el = document.getElementById(elementId);
    el.textContent = msg;
    el.className = 'msg ' + (isError ? 'error' : 'success');
    setTimeout(() => { el.textContent = ''; }, 5000);
}

// Login
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    try {
        const res = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email: username, username, password })
        });
        
        const data = await res.json();
        
        if (res.ok) {
            // Check if user is admin
            if (data.data.user.role !== 'admin') {
                document.getElementById('login-error').textContent = 'Unauthorized: Admin access required.';
                return;
            }
            token = data.data.accessToken;
            localStorage.setItem('adminToken', token);
            showDashboard();
        } else {
            document.getElementById('login-error').textContent = data.message || 'Login failed';
        }
    } catch (err) {
        document.getElementById('login-error').textContent = 'Network error. Make sure backend is running.';
    }
});

// Logout
logoutBtn.addEventListener('click', showLogin);

// Create Bet
createBetForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const betId = document.getElementById('betId').value;
    const question = document.getElementById('betQuestion').value;
    const description = document.getElementById('betDesc').value;
    const resultStr = document.getElementById('betResult').value;
    const resultTime = document.getElementById('betTime').value;
    
    try {
        const res = await fetch(`${API_BASE}/bet/createbet`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                betId, question, description, result: resultStr, resultTime
            })
        });
        
        const data = await res.json();
        if (res.ok) {
            displayMsg('bet-msg', 'Bet created successfully!');
            createBetForm.reset();
        } else {
            displayMsg('bet-msg', data.message || 'Failed to create bet', true);
        }
    } catch (err) {
        displayMsg('bet-msg', 'Network error', true);
    }
});

// Create Stock
createStockForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const stockId = document.getElementById('stockId').value;
    const name = document.getElementById('stockName').value;
    const price = parseFloat(document.getElementById('stockPrice').value);
    
    try {
        const res = await fetch(`${API_BASE}/stocks/createstock`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ stockId, name, price })
        });
        
        const data = await res.json();
        if (res.ok) {
            displayMsg('stock-msg', 'Stock added successfully!');
            createStockForm.reset();
        } else {
            displayMsg('stock-msg', data.message || 'Failed to add stock', true);
        }
    } catch (err) {
        displayMsg('stock-msg', 'Network error', true);
    }
});
