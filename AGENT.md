# Agent Instructions for FPS Simulation Project

## Core Directives
1. **Always read this file** at the start of every session to ensure consistency and prevent regressions.
2. **Do not deviate** from the project structure or build requirements without a strong technical reason.

## Project Structure (Flat Layout)
- `menu.py`: Main User Interface implemented with Kivy. This is the heart of the simulation display.
- `aimbot.py`: Contains mathematical logic for World-to-Screen (W2S) transformation and Aimbot angle (Yaw/Pitch) calculations.
- `memory_reader.py`: Simulates the technical process of reading game data from RAM (Entity List, LocalPlayer).
- `radar.py`: Implements the 2D transformation logic for the external radar system.
- `main.py`: Small shim that imports and runs `menu.py`. This is the default entry point for Buildozer.
- `buildozer.spec`: Configuration for the Android build process.
- `.github/workflows/build.yml`: CI/CD pipeline for automated APK generation.

## Build Requirements
- **Environment**: CI must run on `ubuntu-22.04` (Jammy). Do not use 24.04+ as `libncurses5` (required by Android SDK) is deprecated and difficult to install there.
- **Python**: Use `python-version: '3.11'`. Python 3.12+ introduces PEP 668 restrictions that complicate global pip installs in CI.
- **Android API**: Target `android.api = 34` in `buildozer.spec` for maximum compatibility with the current toolchain.
- **Architecture**: Always include `arm64-v8a` in `android.archs`.

## Critical Dependency Fixes
- **GStreamer Conflict**: `libunwind-dev` must be installed manually *before* `libgstreamer1.0-dev` to resolve versioning conflicts on Ubuntu runners.
- **Android SDK Tools**: `libncurses5`, `libstdc++6`, and `zlib1g` are essential for `aidl` and other Android build tools to function correctly.
- **License Acceptance**: The command `yes | buildozer android p4a -- accept-sdk-license` must be run before the build step in CI.

## Memory / Technical Learnings
- **Docker vs Native**: Docker-based build actions (like `ArtemSBulgakov/buildozer-action`) often fail with `chown` permission errors in specific environments. Manual installation on the runner's OS is more reliable.
- **PEP 668**: If forced to use Python 3.12+, set the environment variable `PIP_BREAK_SYSTEM_PACKAGES: 1`.
- **Aidl Missing**: If `aidl` is reported missing despite being in the SDK, it usually means a missing 32-bit/compat library like `libncurses5`.
