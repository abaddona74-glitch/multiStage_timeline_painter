# MultiStage Timeline Painter

## Feature Goal
Implement a UI that displays a multi-stage festival schedule using a scrollable and zoomable grid-based timeline layout.

## Requirements

### Overall Structure
*   **Time axis (Y-axis):** from 12:00 to 23:00, with 1-hour intervals.
*   **Horizontal columns:** each stage (e.g., Main, Rock, Electro) occupies a separate column.
*   **Performance Blocks:**
    *   Positioned according to start time and stage.
    *   Height reflects duration.
    *   Includes the artist's name.
    *   Must not overlap other blocks.
    *   Scales proportionally when zooming.
*   **Initial State:** The full timeline must fit the screen width.

### Initial Overlay Hint
*   On first launch, an overlay hint should appear: **“Pinch to Zoom”**.
*   Displayed over a blurred/darkened version of the timeline.
*   Automatically disappears after 2–3 seconds.

### Scrolling and Pinch Zoom
*   **Scrolling:** Horizontal and vertical scrolling must be supported.
*   **Zoom:** Handled exclusively via **pinch-to-zoom** gesture. No buttons or sliders.
*   **Scaling:** All elements (blocks, text, grid) must scale proportionally.
*   **Range:** Allowed zoom range from **100% to 250%**.

### Zoom Indicator
*   Displays current zoom level (e.g., "Zoom: 120%").
*   Appears centered at the bottom of the screen.
*   Updates in real time but shows values only in **10% increments** (e.g., 110%, 120%).

### Initial Data For Testing
1.  **DJ A** | Electro Stage | 12:00–13:00
2.  **Band X** | Main Stage | 13:00–14:30
3.  **RockZ** | Rock Stage | 14:00–15:00
4.  **Ambient Line** | Electro Stage | 15:00–16:30
5.  **Florence + The Machine** | Main Stage | 16:30–18:00
6.  **The National** | Rock Stage | 17:00–18:00
7.  **Jamie xx** | Electro Stage | 18:00–19:00
8.  **Tame Impala** | Main Stage | 19:00–20:30
9.  **Arctic Monkeys** | Rock Stage | 20:00–21:30
10. **Radiohead** | Main Stage | 21:30–23:00

## Technical Constraints
*   **Allowed:** Standard Android/Jetpack libraries.
*   **Forbidden:** 3rd party libraries.
*   **AI Usage:** Strictly prohibited for the submission (this code is for educational/reference purposes).

## Submission
1.  Gist link with code.
2.  Screen recording (max 20s) showing:
    *   Initial launch with overlay.
    *   Zooming in (indicator updates, scaling works).
    *   Scrolling at zoomed level.
    *   Zooming back to 100%.

## How to Run

### Prerequisites
*   **Android Studio** (Ladybug or newer recommended)
*   **Android SDK** (minSdk 26, targetSdk 34)
*   **JDK 17** or newer

### Steps
1.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select **Open** and navigate to the project root directory.
    *   Wait for Gradle sync to complete. If prompted to generate a Gradle Wrapper, allow it.

2.  **Run the App:**
    *   Connect an Android device or create an Android Virtual Device (AVD).
    *   Click the green **Run** button (Shift+F10).

### Deploy Over Wi‑Fi (ADB)

If you prefer installing over your device's IP (after a one‑time USB setup):

1. One‑time (with USB plugged):
     - Enable Developer options and USB debugging on the phone.
     - Verify device:
         - `adb devices`
     - Switch to TCP/IP:
         - `adb tcpip 5555`
     - Find the phone IP (or use device Wi‑Fi settings):
         - `adb shell ip addr show wlan0`
     - Connect over Wi‑Fi:
         - `adb connect <PHONE_IP>:5555`

2. Build an APK (first time or after changes):
     - In Android Studio: Build → Build APK(s). The debug APK will be at `app\build\outputs\apk\debug\app-debug.apk`.

3. Install to the Wi‑Fi device:
     - Replace with your actual IP (example shown):
         - `adb -s 192.168.1.181:5555 install -r app\build\outputs\apk\debug\app-debug.apk`

4. Reconnect after device/PC reboot (if needed):
     - `adb connect <PHONE_IP>:5555`

Alternatively, use the provided Windows helper script:

- `scripts\install_over_ip.cmd <PHONE_IP> [port] [apk_path]`
    - Example:
        - `scripts\install_over_ip.cmd 192.168.1.181 5555 app\build\outputs\apk\debug\app-debug.apk`
    - If you omit the IP, the script will try to use the first connected `:5555` device from `adb devices`.

### Build from CMD (no Android Studio)

Since this repo currently has no Gradle Wrapper, either:

1) Install a system Gradle and generate the wrapper

On Windows (CMD):

```
winget install Gradle
gradle -v
gradle wrapper --gradle-version 8.4 --distribution-type all
.\gradlew.bat --version
.\gradlew.bat assembleDebug
```

If `winget` is unavailable, download Gradle from https://gradle.org/releases/ and add `bin` to PATH, then run the same commands.

2) Use the build+install script (works with wrapper or system Gradle)

```
scripts\build_and_install_over_ip.cmd 192.168.1.181 5555 debug
```

This will build the debug APK and install it to the specified IP.

### Use Cached Gradle (no install)

If you already built Android projects on this machine, you likely have cached Gradle distributions under `%USERPROFILE%\.gradle\wrapper\dists`. You can leverage that cache to generate the wrapper and build:

```
scripts\use_cached_gradle_and_build.cmd
```

This script:
- Finds a cached Gradle (prefers 8.2.1 which matches AGP 8.2.0)
- Runs `gradle wrapper` to add `.\gradlew.bat`
- Builds: `.\gradlew.bat assembleDebug`

Then install over Wi‑Fi:

```
adb -s 192.168.1.181:5555 install -r app\build\outputs\apk\debug\app-debug.apk
```

### Troubleshooting
*   If you see errors about missing `gradlew`, opening the project in Android Studio usually resolves this by generating the wrapper automatically.
*   Ensure your `local.properties` file (created automatically by Android Studio) points to your Android SDK location.

