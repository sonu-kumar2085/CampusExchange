import { Router } from "express";
import { 
    loginUser, 
    logoutUser, 
    registerUser, 
    refreshAccessToken, 
    changeCurrentPassword,
    getCurrentUser,
    updateAccountDetails,
} from "../controllers/user.controller.js";
import { verifyJWT } from "../middlewares/auth.middleware.js";

import { getWalletInfo } from "../controllers/wallet.controller.js"
import { getStepInfo, updateSteps, upsertDailySteps, getDailyStepsHistory } from "../controllers/steps.controller.js"


const router = Router()

router.route("/register").post(registerUser)

router.route("/login").post(loginUser)

//secured routes
router.route("/logout").post(verifyJWT,  logoutUser)
router.route("/refresh-token").post(refreshAccessToken)
router.route("/change-password").post(verifyJWT, changeCurrentPassword)
router.route("/current-user").get(verifyJWT, getCurrentUser)
router.route("/update-account").patch(verifyJWT, updateAccountDetails)
router.route("/wallet").get(verifyJWT,getWalletInfo)
router.route("/steps").get(verifyJWT,getStepInfo)
router.route("/steps/update").post(verifyJWT, updateSteps)          // legacy: plain steps update
router.route("/steps/sync").post(verifyJWT, upsertDailySteps)       // new: upsert today's DailySteps record
router.route("/steps/history").get(verifyJWT, getDailyStepsHistory) // new: get all past daily records

export default router