# Contributing to Obscura

Thank you for your interest in contributing to Obscura! This document provides guidelines for contributing to the project.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/Obscura.git`
3. Follow the [SETUP.md](SETUP.md) guide to configure your development environment
4. Create a new branch: `git checkout -b feature/your-feature-name`

## Development Guidelines

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused
- Add comments for complex logic
- Use `// TODO:` for items that need attention

### Architecture

- Follow MVVM architecture pattern
- Keep UI logic in Composables
- Keep business logic in ViewModels
- Keep data operations in Repository
- Use StateFlow for reactive state management

### Commit Messages

Use clear and descriptive commit messages:
- `feat: Add new hint timer feature`
- `fix: Resolve voting count issue`
- `docs: Update setup instructions`
- `refactor: Simplify game state logic`
- `test: Add unit tests for GameViewModel`

### Pull Requests

1. Ensure your code builds without errors
2. Test your changes thoroughly
3. Update documentation if needed
4. Write clear PR descriptions explaining:
   - What changed
   - Why it changed
   - How to test it

## Areas for Contribution

### Features

- [ ] Add timer for hint rounds
- [ ] Implement chat system
- [ ] Add sound effects and music
- [ ] Create tutorial for new players
- [ ] Add more word categories
- [ ] Implement friend system
- [ ] Add player statistics and profiles
- [ ] Create leaderboards
- [ ] Add seasonal events

### Improvements

- [ ] Enhance UI animations
- [ ] Improve error handling
- [ ] Add offline mode support
- [ ] Optimize database queries
- [ ] Add proper launcher icons
- [ ] Improve accessibility
- [ ] Add multiple language support
- [ ] Implement dark/light theme toggle

### Bug Fixes

Check the Issues page for reported bugs that need fixing.

### Documentation

- Improve existing documentation
- Add code examples
- Create video tutorials
- Write blog posts about the game

## Testing

### Before Submitting

1. **Build the project**: `./gradlew build`
2. **Run on emulator**: Test all game flows
3. **Test multiplayer**: Use multiple devices/emulators
4. **Check Firebase**: Verify data syncs correctly

### Writing Tests

- Add unit tests for ViewModels and Repository
- Add UI tests for screens
- Test edge cases and error conditions
- Aim for good code coverage

Example test:
```kotlin
@Test
fun `createCharacter updates currentPlayer`() {
    val viewModel = GameViewModel()
    viewModel.createCharacter("TestPlayer")
    
    advanceTimeBy(1000) // Wait for coroutine
    
    assertNotNull(viewModel.currentPlayer.value)
    assertEquals("TestPlayer", viewModel.currentPlayer.value?.name)
}
```

## Code Review Process

1. Submit your PR
2. Maintainers will review within 1-2 weeks
3. Address any feedback
4. Once approved, your PR will be merged

## Firebase Development

When working with Firebase:

1. Use test mode for development
2. Never commit real API keys
3. Test database rules in Firebase Console
4. Monitor usage to avoid quota limits
5. Use emulators when possible

## Security

- Never commit `google-services.json` with real credentials
- Report security issues privately
- Don't hardcode API keys or secrets
- Validate user input
- Follow Firebase security best practices

## Questions?

- Open an issue for questions
- Check existing issues and PRs
- Review documentation files

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

## Recognition

Contributors will be acknowledged in the project README and release notes.

Thank you for contributing to Obscura! 🎮