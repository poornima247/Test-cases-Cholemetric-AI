# Cholemetric AI - Android E2E Automation Framework

## Overview
This is a complete Appium-based End-to-End automation framework for the Cholemetric AI Android Application.

## Setup Requirements
1. **Java Development Kit (JDK) 17**
2. **Android SDK & Emulator** (API Level 29 recommended)
3. **Node.js & NPM**
4. **Appium 2.x** (`npm install -g appium`)
5. **Appium UiAutomator2 Driver** (`appium driver install uiautomator2`)
6. **Maven**
7. **Python 3.x** (for report generation)

## Local Execution Guide
1. Ensure Android Emulator is running and device is visible via `adb devices`.
2. Start Appium server: `appium`
3. Verify Appium URL and Device Name in `config/config.properties`.
4. Run tests via Maven:
   ```bash
   mvn clean test
   ```
5. Generate Reports:
   ```bash
   python scripts/generate_reports.py
   ```

## CI/CD Execution Guide
This repository uses GitHub Actions for continuous integration.
- Workflow File: `.github/workflows/android-e2e.yml`
- Triggers: Push to `main`, Pull Requests, Nightly Schedule (2 AM UTC)
- Includes: Emulator setup, Appium startup, Test execution, Python reporting, Artifact upload, GitHub Pages deployment.

## Troubleshooting Guide
- **Emulator fails to start**: Ensure KVM/HAXM is enabled on the CI runner or local machine.
- **Appium connection refused**: Verify `APPIUM_URL` in config matches the running Appium server port (default 4723).
- **Element not found**: Add explicit waits or check if the app's view hierarchy has changed using Appium Inspector.

## Repository Configuration Guide
- **Page Objects**: `src/main/java/com/cholemetric/automation/pages/`
- **Tests**: `src/test/java/com/cholemetric/automation/tests/`
- **Configuration**: `config/config.properties`
- **TestNG Suite**: `testng.xml`

## Report Structure
Reports are generated in the `Test Results/` directory:
- `Excel/Automation_Test_Report.xlsx`: Comprehensive test execution data.
- `HTML/execution-report.html`: Visual dashboard with charts and test summary.
- `JSON/execution-results.json`: Raw test execution data.
- `Summary/summary.md`: Markdown summary.
- `Screenshots/`: Captured during failed test executions.
