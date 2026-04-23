import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { Steps } from "../models/step.model.js";
import { DailySteps } from "../models/dailySteps.model.js";

// ── Helpers ──────────────────────────────────────────────────────────────────

/** Returns today's date as "YYYY-MM-DD" in UTC */
function todayUTC() {
    return new Date().toISOString().slice(0, 10);
}

// ── Existing controllers (unchanged) ─────────────────────────────────────────

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
    const { stepsCount } = req.body
    const username = req.user.username

    if (!stepsCount || stepsCount < 0) {
        throw new ApiError(400, "Valid steps count is required")
    }

    const steps = await Steps.findOneAndUpdate(
        { username },
        { $set: { stepsCount } },
        { new: true }
    )

    if (!steps) {
        throw new ApiError(404, "Steps record not found")
    }

    return res
        .status(200)
        .json(new ApiResponse(200, steps, "Steps updated successfully"))
})

// ── New controllers ───────────────────────────────────────────────────────────

/**
 * POST /api/v1/users/steps/sync
 *
 * Called by Android whenever internet is available.
 * 1. Upserts today's record in DailySteps (sets stepsCount for today).
 * 2. Also updates the rolling Steps document (used by the midnight cron).
 *
 * Body: { stepsCount: Number }
 */
const upsertDailySteps = asyncHandler(async (req, res) => {
    const { stepsCount } = req.body
    const username = req.user.username
    const date = todayUTC()

    if (stepsCount === undefined || stepsCount === null || stepsCount < 0) {
        throw new ApiError(400, "Valid steps count is required")
    }

    // 1️⃣  Upsert the DailySteps record for today
    const dailyRecord = await DailySteps.findOneAndUpdate(
        { username, date },
        { $set: { stepsCount } },
        { new: true, upsert: true }
    )

    // 2️⃣  Keep the rolling Steps doc in sync (used by cron)
    await Steps.findOneAndUpdate(
        { username },
        { $set: { stepsCount } },
        { new: true }
    )

    return res
        .status(200)
        .json(new ApiResponse(200, dailyRecord, "Daily steps synced successfully"))
})

/**
 * GET /api/v1/users/steps/history
 *
 * Returns all DailySteps records for the authenticated user,
 * newest first. The Android dashboard uses this to render the
 * step-history chart.
 */
const getDailyStepsHistory = asyncHandler(async (req, res) => {
    const username = req.user.username

    const history = await DailySteps.find({ username })
        .sort({ date: -1 })
        .select("date stepsCount coinsEarned -_id")

    return res
        .status(200)
        .json(new ApiResponse(200, history, "Step history fetched successfully"))
})

export { getStepInfo, updateSteps, upsertDailySteps, getDailyStepsHistory }