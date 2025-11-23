# Obscura

A shady-tone Android social deduction game where players create characters and join parties to find the Imposter among them!

## Game Overview

Obscura is a real-time multiplayer social deduction game with a dark, mysterious theme. Players must work together to identify the Imposter hiding among them, while the Imposter tries to blend in and avoid detection.

### How to Play

1. **Create a Character**: Enter the game by creating your character with a unique name
2. **Join or Create a Party**: 
   - Create a party and share the code with friends
   - Join an existing party with a code
   - Use Quick Match to find random players
3. **Game Start**: Once 3+ players are ready, the host can start the game
4. **Word Assignment**: 
   - All players except one receive the same word
   - One player is secretly designated as the Imposter and only knows the category
5. **Hint Rounds**: Players take turns giving hints about their word (3 rounds)
6. **Voting**: After hints, players vote on who they think is the Imposter
7. **Results**:
   - If the Imposter is caught: Innocent players win and earn 50 coins each
   - If the Imposter escapes: Imposter wins and earns 100 coins

### Features

- **Real-time Multiplayer**: Play with 3-8 players using Firebase real-time sync
- **Party System**: Create private parties with unique codes or join via Quick Match
- **Currency System**: Earn coins by winning games
- **Customization Store**: 
  - Buy avatars (Ninja, Spy, Detective, Thief)
  - Purchase accessories (Hats, Sunglasses, Masks, Capes)
- **Dark Theme**: Shady, mysterious aesthetic throughout the game

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with ViewModels and StateFlow
- **Backend**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog or later
- JDK 8 or higher
- Firebase account

### Firebase Configuration

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use an existing one
3. Add an Android app with package name: `com.obscura.game`
4. Download the `google-services.json` file
5. Replace the mock file at `app/google-services.json` with your actual file
6. Enable Firebase Realtime Database in your Firebase project
7. Set up database rules (for development):
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

### Building the Project

1. Clone the repository
2. Open the project in Android Studio
3. Replace the Firebase configuration file as described above
4. Sync Gradle files
5. Build and run on an emulator or physical device

```bash
./gradlew assembleDebug
```

### Running the App

1. Connect an Android device or start an emulator
2. Click Run in Android Studio or use:
```bash
./gradlew installDebug
```

## Project Structure

```
app/src/main/java/com/obscura/game/
├── MainActivity.kt              # Main activity and app navigation
├── model/                       # Data models
│   ├── Player.kt               # Player data class
│   ├── GameParty.kt            # Game party and state
│   ├── StoreItem.kt            # Store items
│   └── GameWords.kt            # Word categories
├── data/                        # Data layer
│   └── FirebaseRepository.kt   # Firebase operations
├── viewmodel/                   # ViewModels
│   └── GameViewModel.kt        # Main game logic
└── ui/                          # UI layer
    ├── theme/
    │   └── Theme.kt            # App theme
    └── screens/
        ├── HomeScreen.kt       # Character creation
        ├── MainMenuScreen.kt   # Main menu
        ├── LobbyScreen.kt      # Party lobby
        ├── HintRoundScreen.kt  # Hint giving phase
        ├── VotingScreen.kt     # Voting phase
        ├── GameOverScreen.kt   # Results screen
        └── StoreScreen.kt      # In-game store
```

## Game Flow

```
Home (Character Creation)
    ↓
Main Menu
    ├→ Quick Match → Lobby → Game
    ├→ Create Party → Lobby → Game
    ├→ Join Party → Lobby → Game
    └→ Store → Main Menu

Game Flow:
Lobby → Hint Round 1 → Hint Round 2 → Hint Round 3 → Voting → Game Over → Main Menu
```

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## License

This project is created for demonstration purposes.

## Notes

- The current Firebase configuration file is a placeholder. You must replace it with your own.
- For production use, implement proper authentication and security rules.
- The store items currently use text placeholders. Add actual images for better UX.
- Consider adding sound effects and animations for enhanced gameplay.

## Future Enhancements

- [ ] Add player profiles and statistics
- [ ] Implement chat system during games
- [ ] Add more word categories
- [ ] Create seasonal events and limited items
- [ ] Add friend system
- [ ] Implement leaderboards
- [ ] Add tutorial for new players
- [ ] Support for multiple languages