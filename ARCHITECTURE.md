# Obscura Architecture Documentation

This document provides an in-depth look at the architecture, design patterns, and implementation details of the Obscura game.

## Architecture Overview

Obscura follows the **MVVM (Model-View-ViewModel)** architecture pattern with a unidirectional data flow using Kotlin StateFlow.

```
┌─────────────┐
│     UI      │  Jetpack Compose Views
│  (Screens)  │
└──────┬──────┘
       │ Observes StateFlow
       │ Calls ViewModel methods
┌──────▼──────┐
│  ViewModel  │  Business Logic & State
│             │
└──────┬──────┘
       │ Calls Repository methods
       │
┌──────▼──────┐
│ Repository  │  Data Operations
│  (Firebase) │
└──────┬──────┘
       │ Reads/Writes
       │
┌──────▼──────┐
│   Firebase  │  Backend Database
│  Realtime   │
│     DB      │
└─────────────┘
```

## Layer Breakdown

### 1. Model Layer (`model/`)

Data classes representing the game entities. All models are Kotlin data classes optimized for Firebase serialization.

#### Player.kt
```kotlin
data class Player(
    val id: String,           // Unique player identifier
    val name: String,         // Display name
    val avatarId: String,     // Currently equipped avatar
    val accessoryIds: List<String>, // Owned accessories
    val coins: Int,           // In-game currency
    val isHost: Boolean,      // Party host status
    val isReady: Boolean      // Ready to play
)
```

#### GameParty.kt
```kotlin
data class GameParty(
    val partyCode: String,    // 6-character join code
    val hostId: String,       // Party creator
    val players: Map<String, Player>,  // All players in party
    val gameState: GameState, // Current game phase
    val currentRound: Int,    // Current hint round (1-3)
    val maxRounds: Int,       // Total rounds before voting
    val word: String,         // The secret word
    val category: String,     // Word category
    val imposterId: String,   // Player who is imposter
    val hints: Map<String, List<String>>, // Player hints
    val votes: Map<String, String>  // Voting results
)
```

#### GameState Enum
- **WAITING**: Lobby, waiting for players
- **STARTING**: Transitioning to game
- **HINT_ROUND**: Players giving hints
- **VOTING**: Players voting for imposter
- **GAME_OVER**: Game complete, showing results

### 2. Data Layer (`data/`)

#### FirebaseRepository.kt

Handles all Firebase operations. Key responsibilities:

**Player Management**
- `createPlayer()`: Creates new player in database
- `getPlayer()`: Retrieves player data
- `updatePlayerCoins()`: Updates player currency
- `updatePlayerAvatar()`: Changes equipped avatar
- `addPlayerAccessory()`: Adds purchased accessory

**Party Management**
- `createParty()`: Creates new game party with unique code
- `joinParty()`: Adds player to existing party
- `observeParty()`: Returns Flow for real-time party updates
- `generatePartyCode()`: Creates random 6-character code

**Game Flow**
- `startGame()`: Initializes game, assigns imposter, selects word
- `submitHint()`: Records player hint for current round
- `moveToVoting()`: Transitions from hints to voting
- `submitVote()`: Records vote and checks for game end

**Matchmaking**
- `joinMatchmaking()`: Finds or creates party for quick match

**Store**
- `getStoreItems()`: Retrieves all purchasable items
- `initializeStore()`: Populates initial store inventory

### 3. ViewModel Layer (`viewmodel/`)

#### GameViewModel.kt

Central business logic controller. Uses Kotlin StateFlow for reactive state management.

**State Management**
```kotlin
private val _currentPlayer = MutableStateFlow<Player?>(null)
val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()

private val _currentParty = MutableStateFlow<GameParty?>(null)
val currentParty: StateFlow<GameParty?> = _currentParty.asStateFlow()

private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Home)
val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()
```

**Key Features**
- **Automatic Navigation**: Changes screen based on game state
- **Error Handling**: Catches and displays Firebase errors
- **Real-time Sync**: Observes party changes and updates UI
- **State Persistence**: Maintains player and party data

### 4. UI Layer (`ui/`)

Built with **Jetpack Compose** using Material 3 design components.

#### Theme (`ui/theme/Theme.kt`)

Dark theme with purple/violet color scheme:
- Primary: Purple (#7B1FA2)
- Secondary: Deep Purple (#512DA8)
- Background: Dark (#121212)
- Surface: Light Dark (#1E1E1E)

#### Screens (`ui/screens/`)

**HomeScreen.kt**
- Character creation
- Player name input
- Entry point to game

**MainMenuScreen.kt**
- Main navigation hub
- Quick Match, Create Party, Join Party, Store options
- Displays player coins
- Join party dialog

**LobbyScreen.kt**
- Shows party code
- Lists all players in party
- Host can start game when 3+ players ready

**HintRoundScreen.kt**
- Displays word (or category for imposter)
- Hint input field
- Shows all submitted hints
- Host can advance to voting

**VotingScreen.kt**
- Shows all players (except self)
- Vote selection interface
- Tracks voting progress

**GameOverScreen.kt**
- Shows game result (win/loss)
- Reveals the imposter
- Displays coins earned
- Return to menu option

**StoreScreen.kt**
- Lists avatars and accessories
- Shows player coins
- Purchase interface
- Indicates owned items

## Game Flow Logic

### 1. Game Initialization

```
Player creates party
    ↓
Party code generated
    ↓
Other players join
    ↓
Host starts game (requires 3+ players)
    ↓
System assigns random imposter
    ↓
System selects random word + category
    ↓
Game state → HINT_ROUND
```

### 2. Hint Phase

```
Round 1-3:
    ↓
Each player submits hint
    ↓
Host sees "all submitted" indicator
    ↓
Host clicks "Move to Voting"
    ↓
Game state → VOTING
```

### 3. Voting Phase

```
Each player votes for suspected imposter
    ↓
System counts votes automatically
    ↓
When all votes received:
    ↓
Calculate most voted player
    ↓
If imposter caught: Innocents win
    ↓
If imposter survives: Check round count
    ↓
If max rounds reached: Imposter wins
    ↓
Otherwise: Next round
    ↓
Game state → GAME_OVER or HINT_ROUND
```

### 4. Rewards System

**Imposter Wins**: 100 coins
**Innocents Win**: 50 coins each
**Loss**: 0 coins

## Real-time Synchronization

### Firebase Realtime Database Structure

```
/parties
  /{partyCode}
    /players
      /{playerId}
        - name, avatarId, coins, etc.
    - gameState
    - word
    - category
    - imposterId
    /hints
      /{playerId}
        - [hint1, hint2, hint3]
    /votes
      /{voterId}: votedPlayerId

/players
  /{playerId}
    - name
    - coins
    - avatarId
    - accessoryIds[]

/store
  /{itemId}
    - name
    - type
    - price

/matchmaking
  /{queueId}: partyCode
```

### Observable Pattern

The app uses Kotlin Flow to observe Firebase changes:

```kotlin
fun observeParty(partyCode: String): Flow<GameParty?> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val party = snapshot.getValue(GameParty::class.java)
            trySend(party)  // Emit to Flow
        }
        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    partiesRef.child(partyCode).addValueEventListener(listener)
    awaitClose {
        partiesRef.child(partyCode).removeEventListener(listener)
    }
}
```

When party data changes in Firebase:
1. Firebase triggers `onDataChange`
2. New data flows to ViewModel
3. StateFlow updates
4. Compose UI recomposes automatically

## State Management Pattern

### Unidirectional Data Flow

```
User Action → ViewModel Method → Repository → Firebase
                                                   ↓
UI ← StateFlow ← ViewModel ← Flow ← Firebase Listener
```

Example: Submitting a hint

```kotlin
// 1. User types hint and clicks submit
HintRoundScreen: onClick = { viewModel.submitHint(hint) }

// 2. ViewModel calls repository
fun submitHint(hint: String) {
    viewModelScope.launch {
        repository.submitHint(partyCode, playerId, hint)
    }
}

// 3. Repository writes to Firebase
suspend fun submitHint(...) {
    partiesRef.child(partyCode).child("hints/$playerId").setValue(...)
}

// 4. Firebase listener detects change
observeParty() Flow emits new GameParty

// 5. ViewModel updates state
_currentParty.value = updatedParty

// 6. UI observes and recomposes
val currentParty by viewModel.currentParty.collectAsState()
```

## Security Considerations

### Current Implementation (Development)

Database rules allow public read/write for easy testing:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

### Production Recommendations

1. **Enable Firebase Authentication**
   - Use Anonymous Auth for seamless experience
   - Track users without registration

2. **Implement Proper Security Rules**
```json
{
  "rules": {
    "parties": {
      "$partyCode": {
        ".read": "auth != null && data.child('players').hasChild(auth.uid)",
        ".write": "auth != null && data.child('players').hasChild(auth.uid)"
      }
    },
    "players": {
      "$playerId": {
        ".read": "auth != null",
        ".write": "$playerId === auth.uid"
      }
    }
  }
}
```

3. **Validate Data Server-Side**
   - Use Firebase Cloud Functions
   - Validate hint submissions
   - Prevent vote manipulation
   - Check game state transitions

4. **Rate Limiting**
   - Limit party creation per user
   - Throttle hint submissions
   - Prevent spam voting

## Performance Optimizations

### Current Optimizations

1. **Efficient Queries**: Direct path access instead of queries
2. **Targeted Updates**: Update only changed fields
3. **Local State**: Cache player data in ViewModel
4. **Lazy Loading**: Store items loaded on demand

### Future Optimizations

1. **Pagination**: For large player lists or hint history
2. **Offline Support**: Cache critical data locally
3. **Connection Management**: Handle disconnects gracefully
4. **Data Compression**: Minimize hint/vote payload sizes

## Testing Strategy

### Unit Tests (Recommended)

- ViewModel logic
- Repository methods
- Data model transformations
- Game flow logic

### Integration Tests (Recommended)

- Firebase operations
- Real-time synchronization
- Multi-user scenarios

### UI Tests (Recommended)

- Screen navigation
- Form validation
- User interactions

Example test structure:
```kotlin
class GameViewModelTest {
    @Test
    fun `createCharacter updates currentPlayer`() {
        // Test ViewModel logic
    }
    
    @Test
    fun `submitVote triggers game end when all voted`() {
        // Test game flow
    }
}
```

## Scalability Considerations

### Current Limitations

- Max 8 players per party
- 3 hint rounds hardcoded
- Single word pool
- No pagination

### Scaling Improvements

1. **Sharding**: Distribute parties across multiple databases
2. **Caching**: Use Cloud CDN for static assets
3. **Load Balancing**: Handle high concurrent users
4. **Database Indexing**: Speed up queries
5. **Analytics**: Track popular features and bottlenecks

## Extension Points

### Adding New Features

**New Game Modes**
- Add new `GameState` enum values
- Create corresponding screens
- Update game flow in Repository

**Additional Store Items**
- Add to `ItemType` enum
- Create new StoreItem entries
- Handle in purchase logic

**Social Features**
- Add friends list to Player model
- Create social screens
- Implement friend invites

**Achievements**
- Add achievements collection to Firebase
- Track player statistics
- Display in profile screen

## Conclusion

Obscura is built with modern Android development practices:
- **Jetpack Compose** for declarative UI
- **Kotlin Coroutines** for async operations  
- **MVVM architecture** for separation of concerns
- **StateFlow** for reactive state management
- **Firebase** for real-time multiplayer

The architecture is designed to be:
- **Maintainable**: Clear separation of concerns
- **Testable**: Isolated components
- **Scalable**: Extensible design
- **Performant**: Efficient data flow