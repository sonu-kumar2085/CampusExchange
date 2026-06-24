import cron from "node-cron"
import { StockTrade } from "../models/stocktrade.model.js"
import { UserStocks } from "../models/userstocks.model.js"
import { Wallet } from "../models/wallet.model.js"
import { Stock } from "../models/stock.model.js"

const stockTradeCron = () => {
    cron.schedule('* * * * *', async () => {
        console.log("Running stock trade execution...")

        try {
            // get all unique stocks that have pending orders
            const pendingOrders = await StockTrade.find({ status: "pending" })
            const stockIds = [...new Set(pendingOrders.map(o => o.stockId))]

            for (const stockId of stockIds) {
                // get sell orders — sorted by limitPrice low→high (cheapest seller first)
                const sellOrders = await StockTrade.find({
                    stockId,
                    type: "sell",
                    status: "pending"
                }).sort({ limitPrice: 1, createdAt: 1 })

                // get buy orders — sorted by limitPrice high→low (highest bidder first)
                const buyOrders = await StockTrade.find({
                    stockId,
                    type: "buy",
                    status: "pending"
                }).sort({ limitPrice: -1, createdAt: 1 })

                // match orders
                let buyIndex = 0
                let sellIndex = 0

                while (buyIndex < buyOrders.length && sellIndex < sellOrders.length) {
                    const buyOrder = buyOrders[buyIndex]
                    const sellOrder = sellOrders[sellIndex]

                    // match condition — buyer willing to pay >= seller's asking price
                    if (buyOrder.limitPrice >= sellOrder.limitPrice) {

                        // execute at seller's price (lower price)
                        const executionPrice = sellOrder.limitPrice
                        const matchedQty = Math.min(buyOrder.quantity, sellOrder.quantity)
                        const totalCost = matchedQty * executionPrice

                        // Check buyer wallet
                        const buyerWallet = await Wallet.findOne({ username: buyOrder.username })
                        if (!buyerWallet || buyerWallet.campusCoins < totalCost) {
                            console.log(`Match failed: Buyer ${buyOrder.username} has insufficient coins. Cancelling buy order.`)
                            await StockTrade.findByIdAndUpdate(buyOrder._id, { status: "cancelled" })
                            buyIndex++
                            continue
                        }

                        // Check seller shares
                        const sellerStock = await UserStocks.findOne({ username: sellOrder.username, stockId })
                        if (!sellerStock || sellerStock.quantity < matchedQty) {
                            console.log(`Match failed: Seller ${sellOrder.username} has insufficient shares. Cancelling sell order.`)
                            await StockTrade.findByIdAndUpdate(sellOrder._id, { status: "cancelled" })
                            sellIndex++
                            continue
                        }

                        // 1. deduct coins from buyer
                        await Wallet.findOneAndUpdate(
                            { username: buyOrder.username },
                            { $inc: { campusCoins: -totalCost } }
                        )

                        // 2. add coins to seller
                        await Wallet.findOneAndUpdate(
                            { username: sellOrder.username },
                            { $inc: { campusCoins: totalCost } }
                        )

                        // 3. add shares to buyer
                        const buyerStock = await UserStocks.findOne({
                            username: buyOrder.username,
                            stockId
                        })

                        if (buyerStock) {
                            const newQty = buyerStock.quantity + matchedQty
                            const newAvgPrice = ((buyerStock.quantity * buyerStock.avgPrice) + (matchedQty * executionPrice)) / newQty
                            await UserStocks.findOneAndUpdate(
                                { username: buyOrder.username, stockId },
                                { 
                                    $set: { 
                                        quantity: newQty,
                                        avgPrice: newAvgPrice
                                    } 
                                }
                            )
                        } else {
                            await UserStocks.create({
                                username: buyOrder.username,
                                stockId,
                                quantity: matchedQty,
                                avgPrice: executionPrice
                            })
                        }

                        // 4. deduct shares from seller
                        await UserStocks.findOneAndUpdate(
                            { username: sellOrder.username, stockId },
                            { $inc: { quantity: -matchedQty } }
                        )

                        // 5. update stock price
                        await Stock.findOneAndUpdate(
                            { stockId },
                            { $set: { price: executionPrice } }
                        )

                        // 6. mark orders as executed & handle partial matching
                        if (buyOrder.quantity === matchedQty) {
                            await StockTrade.findByIdAndUpdate(buyOrder._id, { status: "executed" })
                            buyIndex++
                        } else {
                            await StockTrade.create({
                                username: buyOrder.username,
                                stockId,
                                quantity: buyOrder.quantity - matchedQty,
                                limitPrice: buyOrder.limitPrice,
                                type: "buy",
                                status: "pending"
                            })
                            await StockTrade.findByIdAndUpdate(buyOrder._id, { 
                                quantity: matchedQty,
                                status: "executed"
                            })
                            buyIndex++
                        }

                        if (sellOrder.quantity === matchedQty) {
                            await StockTrade.findByIdAndUpdate(sellOrder._id, { status: "executed" })
                            sellIndex++
                        } else {
                            await StockTrade.create({
                                username: sellOrder.username,
                                stockId,
                                quantity: sellOrder.quantity - matchedQty,
                                limitPrice: sellOrder.limitPrice,
                                type: "sell",
                                status: "pending"
                            })
                            await StockTrade.findByIdAndUpdate(sellOrder._id, { 
                                quantity: matchedQty,
                                status: "executed"
                            })
                            sellIndex++
                        }

                        console.log(`Matched: ${buyOrder.username} bought ${matchedQty} ${stockId} from ${sellOrder.username} @ ${executionPrice}`)

                    } else {
                        // no match possible — buyer's price too low
                        break
                    }
                }
            }

            console.log("Stock trade execution done!")

        } catch (error) {
            console.log("Stock trade cron error:", error)
        }
    }, {
        timezone: "Asia/Kolkata"
    })
}

export { stockTradeCron }