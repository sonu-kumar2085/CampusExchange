import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { Stock } from "../models/stock.model.js";
import { StockTrade } from "../models/stocktrade.model.js";
import { UserStocks } from "../models/userstocks.model.js";
import { Wallet } from "../models/wallet.model.js";

// get all stocks with current price
const getAllStocks = asyncHandler(async (req, res) => {
    const stocks = await Stock.find()

    if (!stocks ) {
        throw new ApiError(404, "No stocks found")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, stocks, "Stocks fetched successfully"))
})

// get user's portfolio
const getUserStocks = asyncHandler(async (req, res) => {
    const username = req.user.username

    const userStocks = await UserStocks.find({ username })

    if (!userStocks || userStocks.length === 0) {
        throw new ApiError(404, "No stocks found for this user")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, userStocks, "User stocks fetched successfully"))
})

// place a buy or sell order
const placeOrder = asyncHandler(async (req, res) => {
    const { stockId, quantity, limitPrice, type } = req.body
    const username = req.user.username

    if (!stockId || !quantity || !limitPrice || !type) {
        throw new ApiError(400, "All fields are required")
    }

    // check stock exists
    const stock = await Stock.findOne({ stockId })
    if (!stock) {
        throw new ApiError(404, "Stock not found")
    }

    if (type === "sell") {
        // check user has enough shares to sell
        const userStock = await UserStocks.findOne({ username, stockId })
        if (!userStock || userStock.quantity < quantity) {
            throw new ApiError(400, "Insufficient shares to sell")
        }
    }

    if (type === "buy") {
        // check user has enough coins to buy
        const wallet = await Wallet.findOne({ username })
        const totalCost = quantity * limitPrice
        if (!wallet || wallet.campusCoins < totalCost) {
            throw new ApiError(400, `Insufficient campus coins. Need ${totalCost} coins`)
        }
    }

    // place order
    const order = await StockTrade.create({
        username,
        stockId,
        quantity,
        limitPrice,
        type,
        status: "pending"
    })

    return res
        .status(200)
        .json(new ApiResponse(200, order, `${type.toUpperCase()} order placed successfully`))
})

// get user's pending orders
const getMyOrders = asyncHandler(async (req, res) => {
    const orders = await StockTrade.find({
        username: req.user.username,
        status: "pending"
    })

    return res
        .status(200)
        .json(new ApiResponse(200, orders, "Orders fetched successfully"))
})

const createStock = asyncHandler(async (req, res) => {
    const { stockId, name, price } = req.body

    if (!stockId || !name || !price) {
        throw new ApiError(400, "stockId, name and price are required")
    }

    // check if stockId already exists
    const existingStock = await Stock.findOne({ stockId })
    if (existingStock) {
        throw new ApiError(409, "Stock with this stockId already exists")
    }

    const stock = await Stock.create({
        stockId,
        name,
        price,
        sharesct: 100   // default 100
    })

    return res
        .status(201)
        .json(new ApiResponse(201, stock, "Stock created successfully"))
})

export { getAllStocks, getUserStocks, placeOrder, getMyOrders , createStock}