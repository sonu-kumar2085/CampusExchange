# 🏫 CampusExchange — Backend

> **IT Workshop Course Project**
> A gamified campus economy app where students earn coins by walking, then trade stocks and place bets using those coins.

> 📱 *Download the Android APK here:* [CampusExchange App](https://drive.google.com/file/d/1FU4GnnDQDqXdzTBoExHd4sO2cG6wBRmv/view?usp=sharing)

> 🌐 *Admin Panel:* [campus-exchange-admin.vercel.app](https://campus-exchange-admin.vercel.app)

---

## 📖 Table of Contents

- [About the Project](#about-the-project)
- [How It Works](#how-it-works)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Database Models](#database-models)
- [API Reference](#api-reference)
- [Role-Based Access Control](#role-based-access-control)
- [Admin Panel](#admin-panel)
- [Cron Jobs](#cron-jobs)
- [Environment Variables](#environment-variables)
- [Getting Started](#getting-started)

---

## About the Project

CampusExchange is a campus-themed economy platform where your daily steps become currency. Walk more → earn more CampusCoins → trade stocks or bet on events.

It's built as the backend REST API server, handling authentication, a step-to-coin economy, a stock trading engine with order matching, and a betting system with automatic reward distribution.

The platform supports two roles — **User** and **Admin**. Admins manage the platform (create stocks and bet events) through a dedicated web panel built in plain HTML/CSS/JS, served from the same repo.

---

## How It Works

### 🚶 Step Economy
- The mobile app tracks steps walked by the user throughout the day.
- Users can manually convert their steps to coins at any time via the app, or a cron job will automatically convert remaining steps at the end of each day.
- The conversion rate is always **10 steps = 1 CampusCoin**.

### 📈 Stock Trading
- Users can view available stocks and place **buy or sell orders** using their CampusCoins.
- A trading algorithm runs at scheduled intervals: it sorts all pending buy and sell orders, and **executes a trade when a buy price ≥ sell price** (price-match model). Unmatched orders remain pending.
- Users can track their portfolio and full order history.

### 🎲 Betting
- Admins create bet events (e.g., *"Will India win against Sri Lanka today? Yes / No"*).
- Users enroll and place a coin amount on their chosen option.
- After the result, the total prize pool is **distributed among winners proportionally** based on how much each winner bet.

### 🏆 Leaderboard
- A global leaderboard ranks all users by their current CampusCoin balance.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Node.js |
| Framework | Express.js v5 |
| Database | MongoDB (via Mongoose) |
| Authentication | JWT (Access + Refresh tokens) |
| Password Hashing | bcryptjs |
| Scheduled Jobs | node-cron |
| Admin Panel | Plain HTML/CSS/JS |
| Dev Server | Nodemon |

---

## Project Structure

```
Backend/
├── src/
│   ├── controllers/         # Business logic
│   │   ├── bets.controller.js
│   │   ├── steps.controller.js
│   │   ├── stock.controller.js
│   │   ├── user.controller.js
│   │   └── wallet.controller.js
│   │
│   ├── crons/               # Scheduled background jobs
│   │   ├── betResult.cron.js
│   │   ├── stepsToCoin.cron.js
│   │   └── stockTrade.cron.js
│   │
│   ├── db/
│   │   └── db.js            # MongoDB connection
│   │
│   ├── middlewares/
│   │   ├── auth.middleware.js    # JWT verification (verifyJWT)
│   │   └── admin.middleware.js   # Role check (admin only)
│   │
│   ├── models/              # Mongoose schemas
│   │   ├── bet.model.js
│   │   ├── enroll.model.js
│   │   ├── step.model.js
│   │   ├── stock.model.js
│   │   ├── stocktrade.model.js
│   │   ├── user.model.js
│   │   ├── userstocks.model.js
│   │   └── wallet.model.js
│   │
│   ├── routes/              # Route definitions
│   │   ├── bet.routes.js
│   │   ├── stock.routes.js
│   │   ├── user.routes.js
│   │   └── wallet.routes.js
│   │
│   ├── utils/               # Helper utilities
│   ├── app.js               # Express app setup, CORS, middleware
│   ├── constant.js          # App-wide constants
│   └── index.js             # Server entry point
│
├── public/                  # Static files
├── AdminFrontend/           # Admin panel (Plain HTML/CSS/JS, deployed on Vercel)
├── CampusExchangeApp/       # Android mobile app source
├── .env                     # Environment variables
└── package.json
```

---

## Database Models

| Model | Description |
|---|---|
| `user` | User account info, credentials, role (`user` / `admin`), refresh token |
| `wallet` | CampusCoin balance per user |
| `step` | Daily step count records per user |
| `stock` | Available stocks on the platform |
| `stocktrade` | Executed trade records |
| `userstocks` | Each user's stock portfolio holdings |
| `bet` | Bet events created by admin |
| `enroll` | User enrollments in a bet with chosen option and amount |

---

## API Reference

**Base URL:** `http://localhost:8000/api/v1`

> 🔒 Routes marked **[Auth]** require a valid JWT Bearer token (or cookie).
> ⚙️ Routes marked **[Admin]** require the authenticated user to have the `admin` role.

---

### 👤 Users — `/api/v1/users`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/register` | ❌ | Register a new user with required details |
| POST | `/login` | ❌ | Login with email or username + password |
| POST | `/logout` | 🔒 | Logout and invalidate session |
| POST | `/refresh-token` | ❌ | Get a new access token using refresh token |
| POST | `/change-password` | 🔒 | Change the current user's password |
| GET | `/current-user` | 🔒 | Get logged-in user's profile |
| PATCH | `/update-account` | 🔒 | Update account details |
| GET | `/wallet` | 🔒 | Get wallet info (CampusCoin balance) |
| GET | `/steps` | 🔒 | Get current user's step info |
| POST | `/steps/update` | 🔒 | Update step count (replaces with provided value) |

---

### 📈 Stocks — `/api/v1/stocks`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/` | ❌ | Get all available stocks |
| GET | `/portfolio` | 🔒 | Get the logged-in user's stock holdings |
| POST | `/order` | 🔒 | Place a buy or sell order |
| GET | `/orders` | 🔒 | Get all orders placed by the user |
| POST | `/createstock` | ⚙️ Admin | Create a new stock listing |

---

### 🎲 Bets — `/api/v1/bet`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/allbets` | ❌ | Get all available bet events |
| POST | `/enroll` | 🔒 | Enroll in a bet with chosen option and coin amount |
| GET | `/mybets` | 🔒 | Get all bets the user has enrolled in |
| POST | `/createbet` | ⚙️ Admin | Create a new bet event |

---

### 💰 Wallet — `/api/v1/wallets`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/leaderboard` | ❌ | Get the global leaderboard ranked by CampusCoin balance |

---

## Role-Based Access Control

CampusExchange supports two user roles:

| Role | Description |
|---|---|
| `user` | Default role. Can trade stocks, place bets, convert steps, and view the leaderboard. |
| `admin` | Can do everything a user can, plus create new stocks and bet events. |

The `role` field is stored on the `user` model. Protected admin routes are gated by a dedicated `admin.middleware.js` that verifies the authenticated user's role after JWT verification. Any attempt to access an admin route with a `user` role returns a `403 Forbidden` response.

---

## Admin Panel

Admins have a dedicated web interface (plain HTML/CSS/JS) served as a static page from the `public/admin/` directory. It allows admins to:

- **Log in** with their admin credentials
- **Create new stocks** to list on the platform
- **Create new bet events** with custom options for users to bet on

The panel communicates with the backend via the same REST API, sending the admin's JWT token with each request to pass the role-based access check.

> 🌐 **Live Admin Panel:** [campus-exchange-admin.vercel.app](https://campus-exchange-admin.vercel.app)

---

## Cron Jobs

These background jobs run automatically on a schedule:

| Cron File | Purpose |
|---|---|
| `stepsToCoin.cron.js` | Runs daily at midnight — automatically converts each user's remaining steps into CampusCoins (10 steps = 1 coin) |
| `stockTrade.cron.js` | Runs periodically — executes the order-matching trading algorithm (matches buy ≥ sell price) |
| `betResult.cron.js` | Runs after a bet closes — distributes the prize pool to winners proportionally |

---

## Environment Variables

Create a `.env` file in the `Backend/` root:

```env
PORT=8000
MONGODB_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/campusexchange
CORS_ORIGIN=http://localhost:3000

ACCESS_TOKEN_SECRET=your_access_token_secret
ACCESS_TOKEN_EXPIRY=1d

REFRESH_TOKEN_SECRET=your_refresh_token_secret
REFRESH_TOKEN_EXPIRY=10d
```

---

## Getting Started

### Prerequisites
- Node.js v18+
- MongoDB (local or Atlas)
- Docker & Docker Compose (optional, for containerized setup)

### Installation (Local Development)

```bash
# 1. Clone the repo
git clone https://github.com/sonu-kumar2085/campusexchange.git
cd campusexchange/Backend

# 2. Install dependencies
npm install

# 3. Set up environment variables
cp .env.example .env
# Fill in your values in .env

# 4. Start the development server
npm run dev
```

The server will start at `http://localhost:4000`.

### Running with Docker (Recommended for Containerized Dev)

You can run the entire backend stack (Node.js and MongoDB) using Docker:

```bash
# 1. Start the containers (builds the image on first run)
docker compose up --build

# 2. Seed the database (runs inside the container)
docker compose exec api node seed.js

# 3. Stop the containers
docker compose down
```

The Docker container will run the backend on `http://localhost:4000` and automatically configure a dedicated local MongoDB database.

---

## Authentication Flow

CampusExchange uses a **dual-token JWT system**:

- **Access Token** — short-lived, sent with every protected request
- **Refresh Token** — long-lived, stored in an HTTP-only cookie; used to issue new access tokens via `/refresh-token`

On logout, the refresh token is invalidated server-side.

---

*Built for IT Workshop — CampusExchange Backend*
