import { Router } from "express";
import { getallBet,enrolluser,getEnrolledBets,createBet } from "../controllers/bets.controller.js"

import { verifyJWT } from "../middlewares/auth.middleware.js"


const router = Router()

router.route("/allbets").get(getallBet)
router.route("/enroll").post(verifyJWT, enrolluser)
router.route("/mybets").get(verifyJWT, getEnrolledBets)
router.route("/createbet").post(createBet)

export default router