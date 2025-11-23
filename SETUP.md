# Obscura Setup Guide

This guide will help you set up the Obscura game project for development and testing.

## Prerequisites

1. **Android Studio** - Download and install Android Studio Hedgehog (2023.1.1) or later
2. **JDK 8+** - Java Development Kit version 8 or higher
3. **Android SDK** - API Level 24 (Android 7.0) or higher
4. **Firebase Account** - Create a free account at [Firebase Console](https://console.firebase.google.com/)

## Firebase Setup

### Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name (e.g., "Obscura Game")
4. Follow the setup wizard (Analytics is optional)

### Step 2: Add Android App to Firebase

1. In your Firebase project, click the Android icon to add an Android app
2. Enter the package name: **com.obscura.game**
3. Enter an app nickname (optional): "Obscura"
4. Skip the Debug signing certificate SHA-1 for now (needed later for Auth)
5. Click "Register app"

### Step 3: Download Configuration File

1. Download the `google-services.json` file
2. Copy it to the `app/` directory in your project
3. The file should be at: `Obscura/app/google-services.json`

### Step 4: Enable Firebase Services

#### Realtime Database

1. In Firebase Console, go to "Realtime Database" in the left menu
2. Click "Create Database"
3. Choose a location (e.g., us-central1)
4. Start in **Test mode** for development:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

**Important**: For production, use proper security rules:
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null",
    "parties": {
      "$partyCode": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    },
    "players": {
      "$playerId": {
        ".read": "auth != null",
        ".write": "$playerId === auth.uid"
      }
    },
    "store": {
      ".read": "auth != null",
      ".write": false
    }
  }
}
```

#### Authentication (Optional but Recommended)

1. Go to "Authentication" in Firebase Console
2. Click "Get started"
3. Enable "Anonymous" sign-in method
4. This allows users to play without registration

Note: The current implementation uses the database without authentication. To add auth:
- Enable Anonymous authentication in Firebase Console
- Update FirebaseRepository to sign in anonymously before database operations

## Project Setup

### Step 1: Clone Repository

```bash
git clone https://github.com/Cloudbyte-code/Obscura.git
cd Obscura
```

### Step 2: Configure Firebase

```bash
# Copy your downloaded google-services.json to the app directory
cp ~/Downloads/google-services.json app/google-services.json
```

### Step 3: Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the Obscura directory and click "OK"
4. Wait for Gradle sync to complete

### Step 4: Build the Project

```bash
# Using Gradle
./gradlew build

# Or in Android Studio
# Build > Make Project (Ctrl+F9 / Cmd+F9)
```

## Running the App

### On Emulator

1. In Android Studio, click "AVD Manager" (phone icon in toolbar)
2. Create a new Virtual Device or use existing one
3. Recommended: Pixel 5 with API 34 (Android 14)
4. Click Run (green play button) or press Shift+F10

### On Physical Device

1. Enable Developer Options on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging in Developer Options
3. Connect device via USB
4. Select your device in Android Studio
5. Click Run

## Testing Multiplayer

To test multiplayer functionality:

1. Run the app on multiple devices/emulators simultaneously
2. On the first device:
   - Create a character
   - Create a party
   - Note the party code
3. On other devices:
   - Create characters
   - Join party using the code
4. Start the game when 3+ players are ready

### Testing Quick Match

1. Create a party on one device
2. Use Quick Match on another device
3. The matchmaking system should connect them

## Troubleshooting

### Gradle Sync Issues

```bash
# Clean and rebuild
./gradlew clean build
```

### Firebase Connection Issues

1. Verify `google-services.json` is in the correct location
2. Check Firebase Console that services are enabled
3. Ensure internet connectivity on device/emulator
4. Check Firebase Console for any quota limits

### Build Errors

1. Update Gradle plugin: Check `build.gradle.kts` versions
2. Invalidate caches: File > Invalidate Caches / Restart
3. Delete `.gradle` and `.idea` folders, then reopen project

### Runtime Crashes

1. Check Logcat in Android Studio for error messages
2. Verify Firebase rules allow read/write
3. Ensure device has internet connection
4. Check that all required permissions are granted

## Development Tips

### Code Structure

- **Models**: `model/` - Data classes for game entities
- **Data**: `data/` - Firebase repository and data operations
- **ViewModels**: `viewmodel/` - Business logic and state management
- **UI**: `ui/` - Compose screens and components

### Firebase Data Structure

```
firebase-database/
├── parties/
│   └── [partyCode]/
│       ├── players: Map<String, Player>
│       ├── gameState: String
│       ├── word: String
│       ├── category: String
│       ├── imposterId: String
│       ├── hints: Map<String, List<String>>
│       └── votes: Map<String, String>
├── players/
│   └── [playerId]/
│       ├── name: String
│       ├── coins: Int
│       ├── avatarId: String
│       └── accessoryIds: List<String>
└── store/
    └── [itemId]/
        ├── name: String
        ├── type: String
        ├── price: Int
        └── imageUrl: String
```

### Adding Features

1. Create/update models in `model/`
2. Add repository methods in `FirebaseRepository.kt`
3. Update ViewModel logic in `GameViewModel.kt`
4. Create/update UI in `ui/screens/`

### Debugging Firebase

1. Open Firebase Console
2. Go to Realtime Database
3. Watch data change in real-time as you test
4. Use Database Rules simulator to test security rules

## Production Deployment

Before releasing to production:

1. **Update Security Rules**: Use authenticated rules shown above
2. **Enable Authentication**: Implement proper user authentication
3. **Add ProGuard Rules**: Protect code in release builds
4. **Test Thoroughly**: Test all game flows with multiple users
5. **Optimize Database**: Add indexes for better query performance
6. **Set up Analytics**: Track user behavior and crashes
7. **Create Signing Key**: Generate release signing key for Play Store

## Support

For issues or questions:
- Check the [README.md](README.md) for general information
- Review Firebase documentation at https://firebase.google.com/docs
- Open an issue on GitHub

## License

This project is for demonstration purposes.