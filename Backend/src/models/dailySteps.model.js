import mongoose, { Schema } from "mongoose";

// Stores one record per user per calendar day (YYYY-MM-DD).
// The compound unique index on (username + date) makes upserts safe.
const dailyStepsSchema = new Schema(
    {
        username: {
            type: String,
            ref: "User",
            required: true,
            trim: true,
            lowercase: true,
        },
        date: {
            type: String,   // "YYYY-MM-DD"
            required: true,
            trim: true,
        },
        stepsCount: {
            type: Number,
            default: 0,
            min: [0, "Steps count cannot be negative"],
        },
        coinsEarned: {
            type: Number,
            default: 0,
            min: [0, "Coins earned cannot be negative"],
        },
    },
    {
        timestamps: true,
    }
);

// Ensure one record per user per day
dailyStepsSchema.index({ username: 1, date: 1 }, { unique: true });

export const DailySteps = mongoose.model("DailySteps", dailyStepsSchema);
