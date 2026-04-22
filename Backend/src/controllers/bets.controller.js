import { asyncHandler } from "../utils/asyncHandler.js";
import { ApiError } from "../utils/ApiError.js";
import { ApiResponse } from "../utils/ApiResponse.js";
import { Bet } from "../models/bet.model.js";
import { Enroll } from "../models/enroll.model.js";
import { Wallet } from "../models/wallet.model.js";


const getallBet = asyncHandler(async (req, res) => {
    const bets = await Bet.find()

    if (!bets || bets.length === 0) {
        throw new ApiError(404, "No bets found")
    }

    const filteredBets = bets.map(bet => {
        const betObj = bet.toObject()
        if (bet.status === "open") {
            delete betObj.result  // ✅ hide result if bet is open
        }
        return betObj
    })

    return res
        .status(200)
        .json(new ApiResponse(200, filteredBets, "Bets fetched successfully"))
})

    // getuser info and bet inifo , response , no of coins from req
    // if any feild is missing return error
    // check bet is open or closed  and already enrolled or not 
    // save it into enroll collection and deduct coin from wallet (if dont have enough coin send error )
    // update total enroll and total pool
    // return a success message

const enrolluser = asyncHandler(async (req, res) => {
    const { betId, response, campusCoins } = req.body
    const username = req.user.username

    // check if any field is missing
    if (!betId || !response || !campusCoins) {
        throw new ApiError(400, "betId, response and campusCoins are required")
    }

    // check if bet exists and is open
    const bet = await Bet.findOne({ betId })
    if (!bet) {
        throw new ApiError(404, "Bet not found")
    }
    if (bet.status === "closed") {
        throw new ApiError(400, "Bet is closed")
    }

    // check if user already enrolled in this bet
    const alreadyEnrolled = await Enroll.findOne({ betId, username })
    if (alreadyEnrolled) {
        throw new ApiError(400, "You are already enrolled in this bet")
    }

    // check if user has enough campus coins
    const wallet = await Wallet.findOne({ username })
    if (!wallet || wallet.campusCoins < campusCoins) {
        const have = wallet ? wallet.campusCoins.toFixed(2) : 0
        throw new ApiError(400, `Not enough campus coins. You have ${have} coins but tried to bet ${campusCoins} coins.`)
    }

    // save enroll
    const enroll = await Enroll.create({
        betId,
        username,
        campusCoins,
        response
    })

    // deduct coins from wallet
    await Wallet.findOneAndUpdate(
        { username },
        { $inc: { campusCoins: -campusCoins } }
    )

    // update bet stats
    await Bet.findOneAndUpdate(
        { betId },
        {
            $inc: {
                totalEnrolled: 1,
                totalPool: campusCoins
            }
        }
    )

    return res
        .status(200)
        .json(new ApiResponse(200, enroll, "Enrolled successfully"))
})


const getEnrolledBets = asyncHandler(async (req, res) => {
    const username = req.user.username

    // get all enrollments of user
    const enrollments = await Enroll.find({ username })

    if (!enrollments || enrollments.length === 0) {
        throw new ApiError(404, "No enrolled bets found")
    }

    // get all bet details for each enrollment
    const enrolledBets = await Promise.all(
        enrollments.map(async (enroll) => {
            const bet = await Bet.findOne({ betId: enroll.betId })
            const betObj = bet.toObject()

            // hide result if bet is still open
            if (bet.status === "open") {
                delete betObj.result
            }

            return {
                ...betObj,
                myResponse: enroll.response,       // what user answered
                myCoins: enroll.campusCoins,        // how many coins user bet
                enrolledAt: enroll.createdAt        // when user enrolled
            }
        })
    )

    return res
        .status(200)
        .json(new ApiResponse(200, enrolledBets, "Enrolled bets fetched successfully"))
})


const createBet = asyncHandler(async (req, res) => {
    const { betId, question, description, result, resultTime } = req.body

    if (!betId || !question || !result || !resultTime) {
        throw new ApiError(400, "betId, question, result and resultTime are required")
    }

    // check if betId already exists
    const existingBet = await Bet.findOne({ betId })
    if (existingBet) {
        throw new ApiError(409, "Bet with this betId already exists")
    }

    // convert IST to UTC
    // IST = UTC + 5:30, so UTC = IST - 5:30
    const istDate = new Date(resultTime)  // JS automatically handles timezone if passed correctly
    
    const bet = await Bet.create({
        betId,
        question,
        description,
        result,
        resultTime: istDate,   // stored as UTC in MongoDB automatically
        status: "open",
        totalEnrolled: 0,
        totalPool: 0
    })

    return res
        .status(201)
        .json(new ApiResponse(201, bet, "Bet created successfully"))
})

export {getallBet,enrolluser,getEnrolledBets,createBet}