# 🚀 Complete Setup Guide for Beginners

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Step-by-Step Installation](#step-by-step-installation)
3. [Configuration](#configuration)
4. [Verification](#verification)
5. [First Test Run](#first-test-run)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### What You Need

Before starting, ensure you have the following installed:

#### 1. Java Development Kit (JDK) 17 or Higher

**Check if installed:**
```bash
java -version
```

**Expected output:**
```
openjdk version "17.0.x" 2024-xx-xx
```

**Installation:**

**macOS (using Homebrew):**
```bash
brew install openjdk@17
```

**Windows:**
1. Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)
2. Run installer
3. Set JAVA_HOME environment variable

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### 2. Maven 3.6 or Higher

**Check if installed:**
```bash
mvn -version
```

**Expected output:**
```
Apache Maven 3.9.x
Maven home: /path/to/maven
```

**Installation:**

**macOS (using Homebrew):**
```bash
brew install maven
```

**Windows:**
1. Download from [Maven Download](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH environment variable

**Linux:**
```bash
sudo apt install maven
```

#### 3. Node.js 18+ and npm

**Check if installed:**
```bash
node -version
npm -version
```

**Installation:**

**macOS (using Homebrew):**
```bash
brew install node
```

**Windows/Linux:**
1. Download from [Node.js Official Site](https://nodejs.org/)
2. Run installer

#### 4. Appium 2.x

**Installation:**
```bash
npm install -g appium@latest
```

**Verify installation:**
```bash
appium --version
```

**Install Appium drivers:**
```bash
# For Android
appium driver install uiautomator2

# For iOS (macOS only)
appium driver install xcuitest
```

#### 5. Android SDK (For Android Testing)

**Installation:**

**macOS/Linux:**
1. Download Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Install Android Studio
3. Open Android Studio → SDK Manager
4. Install Android SDK Platform Tools
5. Set environment variables:
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

**Windows:**
1. Download Android Studio
2. Install Android Studio
3. Set environment variables:
   - `ANDROID_HOME`: `C:\Users\YourName\AppData\Local\Android\Sdk`
   - Add to PATH: `%ANDROID_HOME%\platform-tools`

**Verify:**
```bash
adb version
```

#### 6. Xcode (For iOS Testing - macOS Only)

1. Install from Mac App Store
2. Accept license:
```bash
sudo xcodebuild -license accept
```
3. Install command line tools:
```bash
xcode-select --install
```

---

## Step-by-Step Installation

### Step 1: Clone the Repository

```bash
git clone <your-repository-url>
cd orchestrix
```

### Step 2: Build the Project

```bash
mvn clean install
```

**What this does:**
- Downloads all dependencies
- Compiles the source code
- Runs unit tests
- Creates the project JAR

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Step 3: Verify Project Structure

Your project should look like this:

```
orchestrix/
├── config/
│   ├── devices.json
│   └── retry-config.json
├── src/
│   ├── main/java/...
│   └── test/java/...
├── scripts/
│   ├── start-appium-nodes.sh
│   └── stop-appium-nodes.sh
├── pom.xml
└── README.md
```

---

## Configuration

### 1. Configure Devices

Edit `config/devices.json`:

#### For Local Android Emulator:

```json
[
  {
    "deviceName": "Pixel_7_Emulator",
    "udid": "emulator-5554",
    "platformName": "Android",
    "platformVersion": "13",
    "tier": "premium",
    "appiumPort": 4723,
    "systemPort": 8200,
    "chromedriverPort": 9515,
    "app": "/path/to/your/app.apk"
  }
]
```

**How to get UDID:**
```bash
adb devices
```

**Output:**
```
List of devices attached
emulator-5554    device
```

#### For Local iOS Simulator (macOS):

```json
[
  {
    "deviceName": "iPhone 15 Pro",
    "udid": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "platformName": "iOS",
    "platformVersion": "17.0",
    "tier": "premium",
    "appiumPort": 4723,
    "bundleId": "com.yourcompany.yourapp",
    "app": "/path/to/your/app.app"
  }
]
```

**How to get UDID:**
```bash
xcrun simctl list devices
```

#### For Cloud Devices (LambdaTest):

```json
[
  {
    "deviceName": "Samsung Galaxy S21",
    "cloudDeviceName": "Galaxy S21",
    "platformName": "Android",
    "platformVersion": "12",
    "tier": "premium",
    "executionType": "lambdatest",
    "cloudProvider": "lambdatest",
    "tunnelId": "your-tunnel-id",
    "cloudAppUrl": "lt://your-app-id"
  }
]
```

**Setup LambdaTest:**
1. Sign up at [LambdaTest](https://www.lambdatest.com/)
2. Get your username and access key
3. Create `.env` file:
```bash
LAMBDATEST_USERNAME=your-username
LAMBDATEST_ACCESS_KEY=your-access-key
```

### 2. Configure Environment Variables

Create `.env` file in project root:

```bash
# LambdaTest Credentials (if using cloud)
LAMBDATEST_USERNAME=your-username
LAMBDATEST_ACCESS_KEY=your-access-key

# BrowserStack Credentials (if using BrowserStack)
BROWSERSTACK_USERNAME=your-username
BROWSERSTACK_ACCESS_KEY=your-access-key

# App Paths
ANDROID_APP_PATH=/path/to/android/app.apk
IOS_APP_PATH=/path/to/ios/app.app
```

### 3. Configure Test Execution

Edit `pom.xml` properties (optional):

```xml
<properties>
    <!-- Number of parallel threads -->
    <parallel.threads>4</parallel.threads>
    
    <!-- Retry count for failed tests -->
    <retry.count>3</retry.count>
</properties>
```

### 4. Configure TestNG Suite

Edit `testng.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="Test Suite" parallel="methods" thread-count="4">
    <test name="Tests">
        <classes>
            <class name="com.parallel.appium.tests.login.LoginTests"/>
        </classes>
    </test>
</suite>
```

---

## Verification

### 1. Verify Java Installation

```bash
java -version
javac -version
```

### 2. Verify Maven Installation

```bash
mvn -version
```

### 3. Verify Appium Installation

```bash
appium --version
appium driver list
```

### 4. Verify Android SDK

```bash
adb version
adb devices
```

### 5. Verify iOS Setup (macOS only)

```bash
xcrun simctl list devices
instruments -s devices
```

### 6. Verify Project Build

```bash
mvn clean compile
```

Should complete without errors.

---

## First Test Run

### Option 1: Run with Scripts (Recommended)

#### 1. Make scripts executable:

```bash
chmod +x scripts/*.sh
```

#### 2. Start Appium servers:

```bash
./scripts/start-appium-nodes.sh
```

**What this does:**
- Starts Appium servers for each device
- Allocates unique ports
- Waits for servers to be ready

#### 3. Run tests:

```bash
./scripts/run-tests.sh
```

Or run specific tests:

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginTests

# Run with specific thread count
mvn clean test -Dparallel.threads=2

# Run specific device tier
mvn clean test -Ddevice.tier=premium
```

#### 4. Stop Appium servers:

```bash
./scripts/stop-appium-nodes.sh
```

### Option 2: Run with Maven Directly

#### 1. Start Appium manually:

```bash
# Terminal 1
appium --port 4723

# Terminal 2 (if multiple devices)
appium --port 4724
```

#### 2. Run tests:

```bash
mvn clean test
```

### Option 3: Run Cucumber BDD Tests

```bash
# Run with Cucumber profile
mvn clean test -Pcucumber

# Or run specific feature
mvn clean test -Pcucumber -Dcucumber.filter.tags="@TC001"
```

---

## Viewing Results

### 1. View Reports

After test execution, reports are generated in:

```
reports/
  ├── consolidated/
  │   └── Device_summary<timestamp>.html
  └── extent/
      └── TestReport-<timestamp>/
          ├── Report.html
          └── screenshots/
```

**Open reports:**

**macOS:**
```bash
open reports/extent/TestReport-*/Report.html
open reports/consolidated/Device_summary*.html
```

**Windows:**
```bash
start reports\extent\TestReport-*\Report.html
```

**Linux:**
```bash
xdg-open reports/extent/TestReport-*/Report.html
```

### 2. View Logs

Logs are in the console output. For detailed logs, check:

```
logs/
  └── appium-framework.log
```

---

## Troubleshooting

### Issue 1: "Java not found"

**Solution:**
```bash
# Set JAVA_HOME
export JAVA_HOME=/path/to/java
export PATH=$JAVA_HOME/bin:$PATH
```

### Issue 2: "Maven not found"

**Solution:**
```bash
# Add Maven to PATH
export PATH=/path/to/maven/bin:$PATH
```

### Issue 3: "Appium command not found"

**Solution:**
```bash
# Reinstall Appium globally
npm install -g appium@latest
```

### Issue 4: "No devices found"

**Solution:**
```bash
# For Android
adb devices

# Start emulator
emulator -avd Pixel_7_API_33

# For iOS (macOS)
xcrun simctl boot "iPhone 15 Pro"
```

### Issue 5: "Port already in use"

**Solution:**
```bash
# Kill processes on ports
lsof -ti:4723 | xargs kill -9

# Or use the stop script
./scripts/stop-appium-nodes.sh
```

### Issue 6: "Device allocation failed"

**Solution:**
- Check `config/devices.json` is valid JSON
- Verify device UDID is correct
- Ensure device is connected/available
- Check device tier matches test requirements

### Issue 7: "Driver creation failed"

**Solution:**
- Verify Appium server is running
- Check device is unlocked
- Verify app path is correct
- Check capabilities in device config

### Issue 8: "Tests hang/freeze"

**Solution:**
- Reduce thread count
- Check device availability
- Verify Appium server logs
- Check for port conflicts

### Issue 9: "Build fails"

**Solution:**
```bash
# Clean and rebuild
mvn clean install -U

# Check for dependency issues
mvn dependency:tree
```

### Issue 10: "Cloud provider authentication failed"

**Solution:**
- Verify `.env` file exists
- Check credentials are correct
- Verify tunnel is running (for LambdaTest)
- Check network connectivity

---

## Next Steps

1. **Read the Usage Guide**: See [USAGE_GUIDE.md](USAGE_GUIDE.md) for how to write and run tests
2. **Read the Technical Guide**: See [TECHNICAL_GUIDE.md](TECHNICAL_GUIDE.md) for architecture details
3. **Explore Examples**: Check `src/test/java` for test examples
4. **Customize Configuration**: Adjust `config/devices.json` for your devices

---

## Quick Reference

### Common Commands

```bash
# Build project
mvn clean install

# Run all tests
mvn clean test

# Run specific test
mvn clean test -Dtest=LoginTests

# Run with custom threads
mvn clean test -Dparallel.threads=4

# Start Appium servers
./scripts/start-appium-nodes.sh

# Stop Appium servers
./scripts/stop-appium-nodes.sh

# View latest report
open reports/extent/TestReport-*/Report.html
```

### Environment Variables

```bash
# Java
export JAVA_HOME=/path/to/java

# Android
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Project
export LAMBDATEST_USERNAME=your-username
export LAMBDATEST_ACCESS_KEY=your-key
```

---

## Getting Help

If you encounter issues:

1. Check the [Troubleshooting](#troubleshooting) section
2. Review logs in `logs/` directory
3. Check Appium server logs
4. Verify all prerequisites are installed
5. Ensure configuration files are correct

---

**Congratulations!** 🎉 You've successfully set up the framework. Now you're ready to write and run tests!
