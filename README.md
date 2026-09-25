# Smart Pantry Manager

## Description
Smart Pantry Manager is an Android application designed to help users reduce food waste. It tracks the ingredients users actually have at home (their "pantry") and suggests recipes they can cook using strictly those available ingredients - no shopping trip required. A recipe is only suggested if the user genuinely already has everything it needs.

## Database Choice
This project uses **SQLite** (via `SQLiteOpenHelper`) for persistent data storage.
SQLite was chosen because:
- It is built into the Android platform, requiring no external dependencies or internet connection.
- It is lightweight and highly efficient for local querying, which is perfect for running our strict-matching algorithm against the pantry items.
- It perfectly meets the assignment's requirement for full CRUD functionality and data persistence across app sessions without the complexity of managing a cloud backend.

## Setup and Run Instructions
1. Clone the repository from GitHub.
2. Open the project in Android Studio (ensure you are using Java, not Kotlin).
3. Allow Gradle to sync and download necessary dependencies.
4. Set up an Android Virtual Device (AVD) or connect a physical device via USB/Wi-Fi debugging.
5. Click the "Run" (Play) button in Android Studio to build and deploy the app to your device.
6. On first run, the app will automatically seed the database with 16 default recipes. Add some ingredients to the pantry to start seeing recipe suggestions!