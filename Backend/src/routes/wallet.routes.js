import { Router } from "express";
import { getLeaderBoard } from "../controllers/wallet.controller.js"


const router = Router()

router.route("/leaderboard").get(getLeaderBoard)

export default router