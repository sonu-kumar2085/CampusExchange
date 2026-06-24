// require('dotenv').config({path: './env'})
import dotenv from "dotenv"
import connectDB from "./db/db.js";
import {app} from './app.js'
dotenv.config({
    path: './.env'
})

import { betResultCron } from "./crons/betResult.cron.js"
import { stockTradeCron } from "./crons/stockTrade.cron.js"
import { stepsToCoinCron } from "./crons/stepsToCoin.cron.js"

connectDB()
.then(() => {
    betResultCron()    // bets Result
    stockTradeCron()   
    stepsToCoinCron()  // steps to coins conversion
    app.listen(process.env.PORT || 8000, () => {
        console.log(`⚙️ Server is running at port : ${process.env.PORT}`);
    })
})
.catch((err) => {
    console.log("MONGO db connection failed !!! ", err);
})




