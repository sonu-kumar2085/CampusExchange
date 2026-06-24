import { Router } from "express";
import { getAllStocks, getUserStocks, placeOrder, getMyOrders , createStock } from "../controllers/stock.controller.js"
import { verifyJWT, restrictTo } from "../middlewares/auth.middleware.js";

const router = Router()

router.route("/").get(getAllStocks)
router.route("/portfolio").get(verifyJWT, getUserStocks)
router.route("/order").post(verifyJWT, placeOrder)
router.route("/orders").get(verifyJWT, getMyOrders)
router.route("/createstock").post(verifyJWT, restrictTo("admin"), createStock)
export default router