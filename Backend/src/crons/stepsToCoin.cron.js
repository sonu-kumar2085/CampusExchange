import cron from "node-cron"
import { Steps } from "../models/step.model.js"
import { DailySteps } from "../models/dailySteps.model.js"
import { Wallet } from "../models/wallet.model.js"

/** Returns yesterday's date as "YYYY-MM-DD" in UTC */
function yesterdayUTC() {
    const d = new Date()
    d.setUTCDate(d.getUTCDate() - 1)
    return d.toISOString().slice(0, 10)
}

const stepsToCoinCron = () => {
    cron.schedule('0 0 * * *', async () => {  // runs every midnight UTC
        console.log("Running steps to coin conversion...")

        try {
            const allSteps = await Steps.find({ stepsCount: { $gt: 0 } })
            const yesterday = yesterdayUTC()

            for (const step of allSteps) {
                const earnedCoins = Math.floor(step.stepsCount / 10)  // 10 steps = 1 coin

                if (earnedCoins > 0) {
                    // 1️⃣  Add coins to wallet
                    await Wallet.findOneAndUpdate(
                        { username: step.username },
                        { $inc: { campusCoins: earnedCoins } }
                    )

                    // 2️⃣  Archive coinsEarned into yesterday's DailySteps record
                    //     (upsert so we don't fail if the device never synced that day)
                    await DailySteps.findOneAndUpdate(
                        { username: step.username, date: yesterday },
                        {
                            $set: {
                                stepsCount: step.stepsCount,
                                coinsEarned: earnedCoins
                            }
                        },
                        { upsert: true }
                    )

                    // 3️⃣  Reset rolling steps to 0
                    await Steps.findOneAndUpdate(
                        { username: step.username },
                        { $set: { stepsCount: 0 } }
                    )
                }
            }

            console.log("Steps to coin conversion done!")

        } catch (error) {
            console.log("Cron job error:", error)
        }
    })
}

export { stepsToCoinCron }