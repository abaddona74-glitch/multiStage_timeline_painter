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

### Troubleshooting
*   If you see errors about missing `gradlew`, opening the project in Android Studio usually resolves this by generating the wrapper automatically.
*   Ensure your `local.properties` file (created automatically by Android Studio) points to your Android SDK location.

