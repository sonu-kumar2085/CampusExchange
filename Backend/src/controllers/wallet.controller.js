import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { Wallet } from "../models/wallet.model.js";

const getWalletInfo = asyncHandler(async (req, res) => {
    const wallet = await Wallet.findOne({ username: req.user.username })

    if (!wallet) {
        throw new ApiError(404, "Wallet not found")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, wallet, "Wallet fetched successfully"))
})


const getLeaderBoard = asyncHandler(async (req, res) => {
    const leaderboard = await Wallet.find()
        .sort({ campusCoins: -1 })  // highest coins first
        .select("username campusCoins")  // only send needed fields

    if (!leaderboard) {
        throw new ApiError(404, "No wallets found")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, leaderboard, "Leaderboard fetched successfully"))
})

export { getWalletInfo , getLeaderBoard }