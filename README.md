# BetShield 🛡️
### Open-Source Gambling & Betting Protection for Android

**BetShield** is a production-grade, privacy-first Android application designed for digital wellbeing, self-control, and gambling prevention. It empowers users to overcome gambling temptations by blocking access to online betting apps, casino games, sportsbooks, and gambling domains using legitimate, non-root Android APIs.

---

## 📑 Table of Contents
1. [Overview & Core Mission](#-overview--core-mission)
2. [Key Features](#-key-features)
3. [Android Architecture & Legitimate Blocking Mechanisms](#-android-architecture--legitimate-blocking-mechanisms)
4. [Platform Permissions Explained](#-platform-permissions-explained)
5. [Supported Android Versions](#-supported-android-versions)
6. [Beginner's Guide: Building the APK with GitHub Actions](#-beginners-guide-building-the-apk-with-github-actions)
7. [Building Locally from Source](#-building-locally-from-source)
8. [Installation & First-Time Device Configuration](#-installation--first-time-device-configuration)
9. [Known Android Platform Limitations](#-known-android-platform-limitations)
10. [Security & Privacy Guarantees](#-security--privacy-guarantees)
11. [License](#-license)

---

## 🌟 Overview & Core Mission

BetShield provides a robust self-exclusion shield for individuals seeking freedom from gambling addiction. Unlike predatory tools that spy on users or install risky decryption certificates, BetShield operates entirely on-device with zero surveillance:

- **No Root Required**: Functions out of the box on standard consumer Android devices.
- **Zero Traffic Snooping**: Does NOT decrypt HTTPS traffic, does NOT install custom root certificates, and does NOT monitor personal messages or passwords.
- **Offline-First Resilience**: Blocking operates continuously even without an internet connection using local Room database storage.

---

## ✨ Key Features

- **🌐 Local DNS Website Filtering**: Intercepts gambling domain queries on a local loopback VPN TUN interface, returning `0.0.0.0` or `NXDOMAIN` for sportsbooks, casinos, poker, lotteries, and betting exchanges.
- **📱 Real-Time App Blocking**: Detects the launch of installed betting and gambling apps via Android's Accessibility API, immediately redirecting the user to a supportive "Access Blocked" reflection screen.
- **🔒 Protection PIN & Delay Lock**: Prevents impulsive tampering or hasty deactivation by requiring a hashed PIN and accountability cooling-off countdown.
- **🎯 Custom Blocklists**: Add personalized website URLs and choose specific device applications to block.
- **📊 Privacy-Conscious Dashboard & Statistics**: Track days protected, total blocked attempts, and weekly trends without ever storing full browsing histories.
- **🤝 Accountability Mode**: Designate an optional trusted contact or accountability partner for support.
- **🔄 Local Database with Remote Sync Ready**: Bundled with hundreds of known gambling domains and package IDs categorized by region (Sports Betting, Casino, Poker, Crypto Gambling, Slots).

---

## 🏗️ Android Architecture & Legitimate Blocking Mechanisms

BetShield follows **Clean Architecture** and **MVVM** built with 100% pure Kotlin and Jetpack Compose (Material Design 3):

```
┌─────────────────────────────────────────────────────────────┐
│                       Jetpack Compose UI                    │
│   Dashboard  •  Blocking Screen  •  Rules  •  PIN  •  Stats │
└──────────────────────────────▲──────────────────────────────┘
                               │ StateFlow / Events
┌──────────────────────────────┴──────────────────────────────┐
│                        View Models                          │
│   DashboardViewModel  •  ProtectionViewModel  •  StatsVM    │
└──────────────────────────────▲──────────────────────────────┘
                               │
┌──────────────────────────────┴──────────────────────────────┐
│                    Repository & Domain Layer                │
│    BlockingRepository  •  PinManager  •  DatabaseSyncManager │
└──────────────────┬───────────────────────────┬──────────────┘
                   │                           │
        ┌──────────┴───────────┐    ┌──────────┴───────────┐
        │   Room Local SQLite  │    │   Platform Services   │
        │ - Blocked Domains    │    │ - Local DnsVpnService│
        │ - Blocked Packages   │    │ - AppDetectorService │
        │ - Privacy Event Log  │    │ - Overlay / Redir    │
        └──────────────────────┘    └──────────────────────┘
```

### 1. Website & Domain Filtering (`DnsVpnService`)
- Uses Android's official `VpnService` API to establish a **local virtual network interface (TUN)**.
- Captures UDP packets directed to port 53 (DNS).
- Inspects solely the requested domain name against the local database of betting domains.
- If a match is found: Instantly replies with `NXDOMAIN` / `0.0.0.0`.
- If safe: Forwards the query directly to secure upstream DNS (1.1.1.1 / 8.8.8.8).
- **Zero data leaves the device.** No external VPN server is involved.

### 2. Application Blocking (`AppBlockerAccessibilityService`)
- Uses Android's `AccessibilityService` API to listen for `TYPE_WINDOW_STATE_CHANGED` events.
- Evaluates the foreground package against registered gambling applications (e.g., DraftKings, FanDuel, BetMGM, Bet365, Stake, etc.).
- When a blocked app launches, it immediately triggers an intentional intent to bring forward `AccessBlockedActivity` or returns the user to the Android Home screen.

---

## 🛡️ Platform Permissions Explained

BetShield requests only the minimal permissions necessary:

| Permission | Purpose | Why It's Needed |
| ---------- | ------- | --------------- |
| `BIND_VPN_SERVICE` | Local DNS Interception | Enables the on-device TUN interface to filter gambling domains without routing traffic to external servers. |
| `BIND_ACCESSIBILITY_SERVICE` | Real-Time App Detection | Observes window transitions to detect when a gambling app opens. Prominently disclosed and strictly confined to package-name matching. |
| `SYSTEM_ALERT_WINDOW` | Supportive Block Screen | Displays the "Access Blocked" overlay over target betting apps. |
| `POST_NOTIFICATIONS` | Foreground Service Alert | Android requires a persistent notification when running long-term protection services. |
| `INTERNET` | Upstream DNS & Sync | Allows safe DNS queries to be resolved and updates the offline database when initiated. |

---

## 📱 Supported Android Versions

- **Minimum SDK:** Android 7.0 (API Level 24)
- **Target SDK:** Android 15 / 16 (API Level 36)
- **Tested Architectures:** `arm64-v8a`, `armeabi-v7a`, `x86_64`

---

## 🚀 Beginner's Guide: Building the APK with GitHub Actions

You do not need to install Android Studio or the Android SDK on your personal computer to build the app! GitHub Actions can compile the APK in the cloud for you:

### Step 1: Fork or Push to GitHub
1. Create a repository on GitHub (e.g. `your-username/betshield`).
2. Clone or push this repository's code to your GitHub repository:
   ```bash
   git remote add origin https://github.com/your-username/betshield.git
   git branch -M main
   git push -u origin main
   ```

### Step 2: Trigger the Build Workflow
1. Navigate to your repository in your web browser.
2. Click on the **Actions** tab at the top.
3. In the left sidebar, click **Build Android APK**.
4. Click the **Run workflow** dropdown on the right, leave the branch as `main`, and click the green **Run workflow** button.

### Step 3: Download the Generated APK
1. Wait approximately 2–3 minutes for the build to turn green (✓).
2. Click on the completed workflow run named **Build & Test Android APK**.
3. Scroll down to the **Artifacts** section at the bottom of the page.
4. Click on **gambling-protection-apk** to download the ZIP file containing `betshield-v1.0.0-debug.apk`.

---

## 💻 Building Locally from Source

If you have Java and Android SDK installed locally:

1. **Prerequisites:**
   - JDK 17 or JDK 21
   - Android SDK with Platforms 34–36 installed

2. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/betshield.git
   cd betshield
   ```

3. **Run Unit Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. **Build the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   The compiled APK will be located at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 📲 Installation & First-Time Device Configuration

1. Transfer `betshield-v1.0.0-debug.apk` to your Android device via USB, Google Drive, or direct download.
2. Open the file on your device and tap **Install** (if prompted, allow installing apps from your browser or file manager).
3. Open **BetShield**.
4. Complete the 3-step setup:
   - **Step 1:** Tap **Activate DNS Guard** and accept Android's system VPN prompt.
   - **Step 2:** Tap **Enable App Detection** and toggle on BetShield in Android Accessibility Settings.
   - **Step 3 (Optional):** Set a 4-to-6 digit **Protection PIN** to prevent impulsive disabling.

---

## ⚠️ Known Android Platform Limitations

Because BetShield complies with Android security and Google Play policies without requiring root access:

1. **Android Private DNS (DoT):** If your device has Android's "Private DNS" set to a custom TLS hostname (such as `dns.google`), Android routes DNS directly over port 853 with TLS encryption before apps can inspect it. To ensure complete domain blocking, set your device's **Settings → Network → Private DNS** to **Automatic** or **Off**.
2. **System Settings Access:** Android OS strictly prevents third-party apps from locking users out of the system Settings app. To make self-exclusion effective, BetShield uses a mandatory PIN protection screen and accountability delay if the user attempts to toggle off protection within the app.
3. **App Uninstallation:** Standard Android apps can be uninstalled through device settings. True uninstallation prevention requires Device Owner / MDM mode.

---

## 🔒 Security & Privacy Guarantees

- **No Remote Telemetry:** No user activity, visited sites, or app usage is sent to third-party tracking or advertising SDKs.
- **Hashed Credentials:** Protection PINs are hashed using SHA-256 and salted, never saved in plain text.
- **Open Source:** Every line of code is inspectable for full transparency and community auditing.

---

## 📄 License

Licensed under the **Apache License, Version 2.0**. See [LICENSE](LICENSE) for details.
