import express from "express"
import cors from "cors"
import cookieParser from "cookie-parser"

const app = express()

app.use(cors({
    origin: process.env.CORS_ORIGIN,
    credentials: true
}))

app.use(express.json({limit: "25kb"}))
app.use(express.urlencoded({ extended: true, limit: "16kb" }))
app.use(express.static("public"))
app.use(cookieParser())


//routes import
import userRouter from './routes/user.routes.js'
import walletRouter from './routes/wallet.routes.js'
import betRouter from './routes/bet.routes.js'
import stockRouter from './routes/stock.routes.js'

//routes declaration
app.use("/api/v1/users", userRouter)
app.use("/api/v1/wallets", walletRouter)
app.use("/api/v1/bet",betRouter)
app.use("/api/v1/stocks", stockRouter)
// http://localhost:8000/api/v1/users/register

// Global error handler
app.use((err, req, res, next) => {
    const statusCode = err.statusCode || 500;
    res.status(statusCode).json({
        success: err.success || false,
        message: err.message || "Internal Server Error",
        errors: err.errors || []
    });
});

export { app }