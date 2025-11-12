# IrisCV

Android app that displays front camera live video feed with maximum FPS, showing dynamic contrast and FPS overlay in orange color.

## Features

- Front camera live video feed with maximum FPS
- Real-time contrast calculation using OpenCV
- FPS display overlay in orange color
- Dynamic contrast display overlay in orange color

## Requirements

- Android SDK 24+
- OpenCV Android SDK (automatically downloaded by GitHub Actions workflow)

## Build

The app uses GitHub Actions to automatically download OpenCV SDK and build the APK. The workflow will:
1. Download OpenCV SDK if not present
2. Build the Android app
3. Generate APK artifact

## Package

- App Name: iriscv
- Package: com.qali.iriscv
