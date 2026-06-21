# 💰 Smart Expense Tracker

A simple, offline-first Android application that helps students and individuals track their daily expenses, understand their spending habits, and receive AI-powered financial advice — all without needing a complicated finance app.

---

## 📱 Overview

Many people, especially students, struggle to track daily expenses due to a lack of simple, user-friendly tools. **Smart Expense Tracker** solves this by offering a clean, modern interface for recording expenses, viewing spending summaries, and receiving personalized AI-driven financial tips — all backed by secure authentication and reliable offline local storage.

---

## ✨ Features

- 🔐 **Secure Authentication** — Firebase Email/Password login & registration
- ➕ **Full CRUD Operations** — Add, view, and delete expenses with real-time updates
- 📊 **Spending Summary** — Total expenses and category-wise breakdown with an interactive pie chart
- 🤖 **AI-Powered Financial Advice** — Personalized spending tips generated via Google's Gemini API
- 🔔 **Daily Reminders** — Local notifications encouraging consistent expense logging
- 📡 **Offline-First** — All expense data stored locally using Room Database; no internet required for core functionality
- 🎨 **Modern UI/UX** — Material Design components, custom typography, category-based color coding, and smooth screen transitions

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML, Material Design Components |
| Local Database | Room (SQLite) |
| Authentication | Firebase Authentication |
| AI Integration | Google Gemini API (via Retrofit + OkHttp) |
| Charts | MPAndroidChart |
| Architecture | Activity-based MVC-style structure |
| Build Tool | Gradle (Kotlin DSL) |

---

## 🤖 AI Feature — Spending Advice

### Purpose
The AI feature analyzes a user's recorded expenses and provides short, practical, encouraging financial tips tailored to their actual spending patterns — helping users make better day-to-day financial decisions.

### How It Works
1. The app retrieves the user's expense records and category-wise totals from the local Room database.
2. This data is formatted into a natural-language prompt summarizing the user's spending (e.g., category totals, number of recorded expenses).
3. The prompt is sent to Google's **Gemini API** (`gemini-2.5-flash` model) via a REST API call.
4. The model generates 3 short, personalized financial tips based on the provided spending summary.
5. The response is parsed and displayed directly in the app's AI Advice screen.

### API Integration
- **Endpoint:** `POST https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent`
- **Library:** Retrofit2 + Gson Converter for request/response serialization
- **Authentication:** API key passed as a query parameter, stored securely via `local.properties` and exposed through `BuildConfig` (never hardcoded in source)

### Inputs & Outputs
- **Input:** A text prompt containing category-wise expense totals and total record count
- **Output:** A plain-text response containing 3 short, friendly financial tips

---

## 📂 Project Structure
