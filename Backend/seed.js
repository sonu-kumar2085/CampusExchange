import mongoose from "mongoose";
import dotenv from "dotenv";

// Import your models
import { Stock } from "./src/models/stock.model.js";
import { Bet } from "./src/models/bet.model.js";

dotenv.config();

const dummyStocks = [
  { stockId: "TCHI", name: "Tech Innovation Index", price: 150.2, quantity: 10000, sharesct: 5000 },
  { stockId: "FITN", name: "Campus Fitness Fund", price: 85.5, quantity: 8000, sharesct: 2500 },
  { stockId: "CGPA", name: "Campus GPA Average", price: 320.0, quantity: 5000, sharesct: 1200 },
  { stockId: "HACK", name: "Hackathon Winners ETF", price: 410.75, quantity: 4000, sharesct: 3000 },
  { stockId: "SLEP", name: "Student Sleep Index", price: 12.4, quantity: 20000, sharesct: 18000 },
  { stockId: "CAFE", name: "Library Cafe Revenue", price: 55.0, quantity: 12000, sharesct: 6000 },
  { stockId: "SPRT", name: "Varsity Sports Performance", price: 210.5, quantity: 6000, sharesct: 4000 },
  { stockId: "CLUB", name: "Student Club Activity", price: 98.2, quantity: 9000, sharesct: 4500 },
  { stockId: "ALUM", name: "Alumni Donation Index", price: 505.0, quantity: 3000, sharesct: 1500 },
  { stockId: "CODE", name: "CS Majors Stress Level", price: 880.9, quantity: 2000, sharesct: 1900 }
];

const dummyBets = [
  {
    betId: "BET001",
    question: "Will the CS department win the inter-department hackathon?",
    description: "The annual campus hackathon is next week. CS is the defending champion, but Engineering has a strong team this year.",
    status: "open",
    totalPool: 1500,
    totalEnrolled: 42,
    resultTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // 7 days from now
    enrolledUsers: []
  },
  {
    betId: "BET002",
    question: "Will the library extend its hours to 24/7 during finals week?",
    description: "Student council has petitioned for 24/7 library access during finals. Administration is voting on Friday.",
    status: "open",
    totalPool: 3200,
    totalEnrolled: 115,
    resultTime: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000), // 3 days from now
    enrolledUsers: []
  },
  {
    betId: "BET003",
    question: "Will the campus basketball team win their away game this Saturday?",
    description: "Facing our rivals this weekend. Our star point guard is questionable with an ankle sprain.",
    status: "open",
    totalPool: 850,
    totalEnrolled: 28,
    resultTime: new Date(Date.now() + 4 * 24 * 60 * 60 * 1000), // 4 days from now
    enrolledUsers: []
  },
  {
    betId: "BET004",
    question: "Will the new dining hall open before the end of the semester?",
    description: "Construction has been delayed twice already. Contractor promises completion by next month.",
    status: "open",
    totalPool: 500,
    totalEnrolled: 18,
    resultTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30 days from now
    enrolledUsers: []
  },
  {
    betId: "BET005",
    question: "Did Professor Smith give a pop quiz in Intro to Bio today?",
    description: "He hinted at it last class. Let's see who was paying attention.",
    status: "closed",
    totalPool: 120,
    totalEnrolled: 8,
    resultTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), // 1 day ago
    result: "yes",
    enrolledUsers: []
  },
  {
    betId: "BET006",
    question: "Did it snow on campus during the winter festival?",
    description: "Forecast was calling for a 40% chance of flurries.",
    status: "closed",
    totalPool: 2400,
    totalEnrolled: 85,
    resultTime: new Date(Date.now() - 14 * 24 * 60 * 60 * 1000), // 14 days ago
    result: "no",
    enrolledUsers: []
  },
  {
    betId: "BET007",
    question: "Will the upcoming campus concert tickets sell out in under an hour?",
    description: "A major artist was just announced for the spring concert.",
    status: "open",
    totalPool: 4100,
    totalEnrolled: 156,
    resultTime: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000), // 2 days from now
    enrolledUsers: []
  },
  {
    betId: "BET008",
    question: "Will the average campus GPA be higher this semester than last?",
    description: "Based on mid-term reports, things are looking optimistic.",
    status: "open",
    totalPool: 600,
    totalEnrolled: 22,
    resultTime: new Date(Date.now() + 60 * 24 * 60 * 60 * 1000), // 60 days from now
    enrolledUsers: []
  }
];

const seedDatabase = async () => {
  try {
    const mongoUri = process.env.MONGODB_URLI || process.env.MONGODB_URI;
    if (!mongoUri) {
      console.error("MONGODB_URLI is not defined in .env");
      process.exit(1);
    }

    console.log("Connecting to MongoDB...");
    await mongoose.connect(mongoUri);
    console.log("Connected successfully!");

    console.log("Seeding Stocks...");
    for (const stock of dummyStocks) {
      await Stock.findOneAndUpdate({ stockId: stock.stockId }, stock, {
        upsert: true,
        new: true,
        setDefaultsOnInsert: true
      });
    }
    console.log(`Seeded ${dummyStocks.length} stocks.`);

    console.log("Seeding Bets...");
    for (const bet of dummyBets) {
      await Bet.findOneAndUpdate({ question: bet.question }, bet, {
        upsert: true,
        new: true,
        setDefaultsOnInsert: true
      });
    }
    console.log(`Seeded ${dummyBets.length} bets.`);

    console.log("Database seeded successfully!");
  } catch (error) {
    console.error("Error seeding database:", error);
  } finally {
    await mongoose.disconnect();
    console.log("Disconnected from MongoDB.");
    process.exit(0);
  }
};

seedDatabase();
