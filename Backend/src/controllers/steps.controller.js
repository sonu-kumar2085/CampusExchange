import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { Steps } from "../models/step.model.js";
import { DailySteps } from "../models/dailySteps.model.js";
import { Wallet } from "../models/wallet.model.js";


function todayUTC() {
    return new Date().toISOString().slice(0, 10);
}


const getStepInfo = asyncHandler(async (req, res) => {
    const Step = await Steps.findOne({ username: req.user.username })

    if (!Step) {
        throw new ApiError(404, "Steps_Info not found")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, Step, "steps info fetched successfully"))
})

const updateSteps = asyncHandler(async (req, res) => {
    const { unconvertedSteps } = req.body
    const username = req.user.username

    if (unconvertedSteps === undefined || unconvertedSteps === null || unconvertedSteps < 0) {
        throw new ApiError(400, "Valid unconverted steps count is required")
    }

    const steps = await Steps.findOneAndUpdate(
        { username },
        { $set: { unconvertedSteps } },
        { new: true }
    )

    if (!steps) {
        throw new ApiError(404, "Steps record not found")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, steps, "Steps updated successfully"))
})


const upsertDailySteps = asyncHandler(async (req, res) => {
    const { stepsCount } = req.body
    const username = req.user.username
    const date = todayUTC()

    if (stepsCount === undefined || stepsCount === null || stepsCount < 0) {
        throw new ApiError(400, "Valid steps count is required")
    }

    const dailyRecord = await DailySteps.findOneAndUpdate(
        { username, date },
        { $set: { stepsCount } },
        { new: true, upsert: true }
    )

    return res
        .status(200)
        .json(new ApiResponse(200, dailyRecord, "Daily steps synced successfully"))
})


const getDailyStepsHistory = asyncHandler(async (req, res) => {
    const username = req.user.username

    const history = await DailySteps.find({ username })
        .sort({ date: -1 })
        .select("date stepsCount coinsEarned -_id")

    return res
        .status(200)
        .json(new ApiResponse(200, history, "Step history fetched successfully"))
})

const convertStepsToCoins = asyncHandler(async (req, res) => {
    const username = req.user.username

    const stepsRecord = await Steps.findOne({ username })

    if (!stepsRecord) {
        throw new ApiError(404, "Steps record not found")
    }

    const stepsCount = stepsRecord.unconvertedSteps

    if (stepsCount < 10) {
        throw new ApiError(400, "You need at least 10 steps to convert to coins")
    }

    const earnedCoins = Math.floor(stepsCount / 10)
    const remainderSteps = stepsCount % 10

    // Atomically reset steps to remainder to prevent double conversion
    const updatedSteps = await Steps.findOneAndUpdate(
        { username, unconvertedSteps: stepsCount },
        { $set: { unconvertedSteps: remainderSteps } },
        { new: true }
    )

    if (!updatedSteps) {
        throw new ApiError(409, "Conversion race condition detected. Please try again.")
    }

    // Add coins to wallet
    const wallet = await Wallet.findOneAndUpdate(
        { username },
        { $inc: { campusCoins: earnedCoins } },
        { new: true }
    )

    if (!wallet) {
        throw new ApiError(404, "Wallet not found")
    }

    // Update today's DailySteps record
    const date = todayUTC()
    await DailySteps.findOneAndUpdate(
        { username, date },
        { $inc: { coinsEarned: earnedCoins } },
        { upsert: true }
    )

    return res
        .status(200)
        .json(
            new ApiResponse(
                200,
                {
                    coinsEarned: earnedCoins,
                    walletCoins: wallet.campusCoins,
                    stepsLeft: remainderSteps
                },
                "Steps successfully converted to coins"
            )
        )
})

export { getStepInfo, updateSteps, upsertDailySteps, getDailyStepsHistory, convertStepsToCoins }