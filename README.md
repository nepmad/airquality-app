# AirPulse — Air Quality App

An Android utility app built from the CP3406 / CP5307 starter template using Kotlin, Jetpack Compose and Material Design 3. It retains the single-activity structure and bottom navigation between AirPulse and Settings.

## Features

- A brief in-app splash screen.
- Model-derived air quality for Kathmandu, Brisbane, Sydney, London and Tokyo.
- City selection from the home screen or Settings.
- US and European AQI displays with category labels and general activity advice.
- PM2.5, PM10, dust and UV index readings when supplied by the API.
- Manual refresh, loading feedback and retry after errors.

## Run the project

1. Open the project root in Android Studio.
2. Install Android SDK 36 and use a compatible Gradle JDK (JDK 17 or later, subject to the configured Gradle toolchain).
3. Sync Gradle, then run on an emulator or device running Android 8.0 / API 26 or later.

The Gradle wrapper is included. From the project root, use:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

Internet access is required for dependency downloads and air quality requests. The current API setup requires no API key.

## Code structure

| Existing location | Responsibility |
| --- | --- |
| `MainActivity.kt` | Activity setup and in-app splash |
| `ui/UtilityApp.kt` | Bottom navigation and shared ViewModels |
| `ui/screens/` | Air quality display, city picker and settings |
| `ui/viewmodel/` | Settings state, request lifecycle and loading/result/error state |
| `data/api/` | Retrofit interface and shared HTTP client |
| `data/model/` | API response and settings models |
| `data/repository/` | Fetching, response validation and error handling |
| `ui/theme/` | Starter Compose theme resources |

## Data and behaviour

Data comes from [Open-Meteo Air Quality](https://air-quality-api.open-meteo.com/) / CAMS and represents model-derived conditions. Both AQI fields are requested together; changing the standard selects the corresponding returned field. The existing `EUROPEAN_CAQI` enum name refers to the European AQI field in this implementation.

Missing readings display as unavailable. “Fetched” shows the local request-completion time, which is separate from the API observation time. Selecting another city cancels the previous request to prevent an obsolete result from updating the display.

Settings are held in memory and are not persisted across a fresh app launch. The `autoRefresh` model property is not wired to a periodic refresh task or a settings control. Readings load on entering the home screen, changing city, or requesting a manual refresh.

## Verification and history

The included unit and instrumented tests are starter examples; they do not validate the air quality calculations or complete user flows. A full Android build and device check are still required before submission.

The project was imported from the supplied ZIP in grouped commits, followed by code-comment and documentation commits. Those import commits record existing work and do not represent an earlier development timeline.

## Template acknowledgement

Based on the MobileTech Utility App educational starter supplied for CP3406 / CP5307. Existing project filenames and source structure are retained.
