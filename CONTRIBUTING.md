# Contributing to BetShield

Thank you for your interest in contributing to BetShield, an open-source digital wellbeing and gambling self-exclusion platform for Android.

## Code of Conduct

We are committed to providing a friendly, safe, and welcoming environment for everyone, regardless of gender identity, sexual orientation, disability, ethnicity, or religion.

## Development Workflow

1. **Fork & Clone**
   ```bash
   git clone https://github.com/your-username/betshield.git
   cd betshield
   ```

2. **Branching Strategy**
   - Create a feature branch: `git checkout -b feature/my-new-feature`
   - Keep commits focused, descriptive, and atomic.

3. **Code Style & Guidelines**
   - Pure Kotlin with modern Jetpack Compose.
   - Strictly follow Material 3 guidelines and minimum touch target requirements (48dp).
   - Zero root requirements: all features must use legitimate Android APIs (`VpnService`, `AccessibilityService`, `Room`).
   - Strict privacy: No HTTPS interception, no MITM, no keystroke tracking, no transmission of personal user data.

4. **Testing**
   - Run local JVM unit tests before submitting a Pull Request:
     ```bash
     ./gradlew testDebugUnitTest
     ```
   - Verify the build passes:
     ```bash
     ./gradlew assembleDebug
     ```

5. **Submitting a Pull Request**
   - Open a PR targeting the `main` branch.
   - Describe what the PR accomplishes and reference any related issues.
   - Ensure the automated GitHub Actions CI build passes.
