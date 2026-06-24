import mongoose from "mongoose";
import dotenv from "dotenv";
import { User } from "./models/user.model.js";
import { Wallet } from "./models/wallet.model.js";
import { DB_NAME } from "./constant.js";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Load env from the root of Backend
dotenv.config({ path: path.join(__dirname, "../.env") });

const seedDB = async () => {
    try {
        console.log("Connecting to Database...");
        await mongoose.connect(`${process.env.MONGODB_URLI}/${DB_NAME}`);
        console.log("Connected to MongoDB.");

        const usersToSeed = [
            {
                username: "admin",
                email: "admin@campusexchange.com",
                fullName: "Super Admin",
                password: "adminpassword",
                role: "admin",
                college: "Campus Admin"
            },
            {
                username: "student1",
                email: "student1@example.com",
                fullName: "Student One",
                password: "password123",
                role: "user",
                college: "Tech College"
            },
            {
                username: "student2",
                email: "student2@example.com",
                fullName: "Student Two",
                password: "password123",
                role: "user",
                college: "Tech College"
            },
            {
                username: "student3",
                email: "student3@example.com",
                fullName: "Student Three",
                password: "password123",
                role: "user",
                college: "Tech College"
            }
        ];

        console.log("Creating Users and Wallets with 500 coins...");
        for (const userData of usersToSeed) {
            const existingUser = await User.findOne({ username: userData.username });
            if (!existingUser) {
                const newUser = new User(userData);
                await newUser.save();
                
                const newWallet = new Wallet({
                    username: newUser.username,
                    campusCoins: 500
                });
                await newWallet.save();
                console.log(`Created user and wallet: ${newUser.username}`);
            } else {
                console.log(`User ${userData.username} already exists. Skipping.`);
            }
        }

        console.log("Seeding Completed Successfully.");
        process.exit(0);
    } catch (error) {
        console.error("Error seeding database:", error);
        process.exit(1);
    }
};

seedDB();
