# StreetEmu Android — launcher foundation 0.2

StreetEmu is a user-facing launcher foundation for a native Android port of **Need for Speed: Underground 1**. The game itself is not included.

## Implemented in this source project
- Dark, mobile-first launcher UI.
- Android Storage Access Framework folder picker with persisted access permission.
- Top-level validation for `default.xbe`, `CARS/`, and `TRACKS/`.
- Local settings for quality preset, internal-resolution preference, FPS cap, and touch-control style.
- Device information (Android version and primary ABI).
- Explicit launch-readiness diagnostics and clear messaging when the engine is missing.

## Important status — read before use
**This is not a playable APK yet.** The native NFS Underground engine is not part of this project, the Launch Game action is deliberately blocked, and the performance controls currently store preferences only. They do not change rendering or FPS until they are connected to the engine. No APK build or on-device test is claimed by this repository snapshot.

## Engine target
The intended engine target is the open-source Android port `https://github.com/cherepavel/nfsu1-android`. That project targets ARM64 / Android 11+ and requires the supported original Xbox NTSC-U game executable and extracted game data from the user's own copy. Check its upstream README for current prerequisites, version compatibility, build instructions, and GPL-3.0-or-later obligations.

The launcher currently checks the expected top-level layout only. It cannot prove the game edition is compatible, validate all files, or launch the native engine. Do not use PC, PS2, or another Xbox edition unless the selected engine explicitly supports it.

## Build in Android Studio
1. Open this folder in Android Studio.
2. Use JDK 21 and install Android SDK Platform 35 for this launcher module.
3. Sync Gradle and run `:app:assembleDebug`.
4. The APK output, if the build succeeds, is `app/build/outputs/apk/debug/app-debug.apk`.

This environment did not have Android SDK / build-tools available, so this snapshot has **not been built or tested into an APK** here.

## Planned next work
1. Vendor or fetch the upstream engine source and initialize its pinned submodules.
2. Add the exact game-executable path configuration and native recompilation task.
3. Connect Android lifecycle, SDL/Vulkan surface, input/touch overlays, and save-data paths to the native runtime.
4. Wire the saved settings to real renderer/runtime controls.
5. Build and test on an ARM64 Android phone; measure frame pacing, thermal behavior, and compatibility before claiming performance gains.

## Licensing
StreetEmu launcher code is provided under GPL-3.0-or-later to match the intended integration direction. The original game's executable, data, art, music, logos, and trademarks are not included and remain the rights holders' property. Review all upstream and third-party notices before redistributing a combined build.

## v0.3 preflight additions
- The device panel now reports Android API/ARM64 baseline compatibility and Vulkan feature flags advertised by Android.
- These are preliminary checks only; they do not prove the specific GPU driver can run the port.
- See `ENGINE_INTEGRATION_BLOCKERS_TH.md` for exact missing components and the current build limitation.

## Build APK via GitHub Actions
This repository includes `.github/workflows/build-apk.yml` to build the **launcher APK only** in a hosted CI runner. Push the project to a GitHub repository, open **Actions → Build StreetEmu launcher APK → Run workflow**, then download the `StreetEmu-launcher-debug-apk` artifact from the completed run. This workflow has not been executed from this environment. It does not include or build the native game engine, so the resulting APK is a launcher/preflight app, not a playable NFSU1 port.
