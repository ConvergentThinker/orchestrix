# 📱 Local Device & Emulator Setup Guide

## Table of Contents
1. [Overview](#overview)
2. [Android Emulator Setup](#android-emulator-setup)
3. [Android Physical Device Setup](#android-physical-device-setup)
4. [iOS Simulator Setup (macOS)](#ios-simulator-setup-macos)
5. [iOS Physical Device Setup (macOS)](#ios-physical-device-setup-macos)
6. [Configuration Examples](#configuration-examples)
7. [Running Tests](#running-tests)
8. [Troubleshooting](#troubleshooting)
9. [Best Practices](#best-practices)

---

## Overview

This guide will help you set up and configure **local devices** (emulators, simulators, and physical devices) to run tests with Orchestrix. You can use:

- **Android Emulators** - Virtual Android devices
- **Android Physical Devices** - Real Android phones/tablets
- **iOS Simulators** - Virtual iOS devices (macOS only)
- **iOS Physical Devices** - Real iPhones/iPads (macOS only)

### Prerequisites

Before starting, ensure you have:
- ✅ Java 17+ installed
- ✅ Maven 3.6+ installed
- ✅ Appium 2.x installed
- ✅ Android SDK installed (for Android)
- ✅ Xcode installed (for iOS - macOS only)
- ✅ ADB installed and in PATH (for Android)
- ✅ Project cloned and built

---

## Android Emulator Setup

### Step 1: Install Android Studio

1. Download Android Studio from [developer.android.com/studio](https://developer.android.com/studio)
2. Install Android Studio
3. Open Android Studio → **SDK Manager**
4. Install:
   - Android SDK Platform Tools
   - Android SDK Build-Tools
   - Android Emulator
   - System images for your target Android versions

### Step 2: Set Environment Variables

**macOS/Linux:**
```bash
# Add to ~/.zshrc or ~/.bashrc
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
```

**Windows:**
1. Open System Properties → Environment Variables
2. Add new variable:
   - Variable: `ANDROID_HOME`
   - Value: `C:\Users\YourName\AppData\Local\Android\Sdk`
3. Add to PATH:
   - `%ANDROID_HOME%\platform-tools`
   - `%ANDROID_HOME%\emulator`
   - `%ANDROID_HOME%\tools`

**Verify installation:**
```bash
adb version
emulator -version
```

### Step 3: Create Android Virtual Device (AVD)

**Using Android Studio:**
1. Open Android Studio
2. Go to **Tools** → **Device Manager**
3. Click **Create Device**
4. Select device (e.g., Pixel 7)
5. Select system image (e.g., Android 13 - API 33)
6. Click **Finish**

**Using Command Line:**
```bash
# List available system images
sdkmanager --list | grep system-images

# Install system image
sdkmanager "system-images;android-33;google_apis;x86_64"

# Create AVD
avdmanager create avd -n Pixel_7_API_33 -k "system-images;android-33;google_apis;x86_64" -d "pixel_7"
```

### Step 4: Start Emulator

**Using Android Studio:**
1. Open **Device Manager**
2. Click **Play** button next to your AVD

**Using Command Line:**
```bash
# List available AVDs
emulator -list-avds

# Start emulator
emulator -avd Pixel_7_API_33 &

# Or with specific options
emulator -avd Pixel_7_API_33 -no-snapshot-load -wipe-data &
```

### Step 5: Verify Emulator is Running

```bash
# Check connected devices
adb devices
```

**Expected output:**
```
List of devices attached
emulator-5554    device
```

**Note the UDID**: `emulator-5554` (this is what you'll use in configuration)

### Step 6: Install Your App

```bash
# Install APK on emulator
adb -s emulator-5554 install /path/to/your/app.apk

# Verify installation
adb -s emulator-5554 shell pm list packages | grep your.package.name
```

---

## Android Physical Device Setup

### Step 1: Enable Developer Options

1. Open **Settings** on your Android device
2. Go to **About Phone**
3. Tap **Build Number** 7 times
4. You'll see "You are now a developer!"

### Step 2: Enable USB Debugging

1. Go back to **Settings**
2. Open **Developer Options**
3. Enable **USB Debugging**
4. Enable **Stay Awake** (optional, but recommended)
5. Enable **Install via USB** (if available)

### Step 3: Connect Device via USB

1. Connect your Android device to your computer via USB cable
2. On your device, you may see a prompt: **"Allow USB debugging?"**
3. Check **"Always allow from this computer"**
4. Tap **OK**

### Step 4: Verify Connection

```bash
# Check connected devices
adb devices
```

**Expected output:**
```
List of devices attached
R58M12345678    device
```

**Note the UDID**: `R58M12345678` (this is your device's unique identifier)

**If device shows as "unauthorized":**
- Check USB debugging is enabled
- Revoke USB debugging authorizations on device
- Reconnect and accept the prompt

### Step 5: Get Device Information

```bash
# Get device model
adb -s R58M12345678 shell getprop ro.product.model

# Get Android version
adb -s R58M12345678 shell getprop ro.build.version.release

# Get API level
adb -s R58M12345678 shell getprop ro.build.version.sdk

# Get device manufacturer
adb -s R58M12345678 shell getprop ro.product.manufacturer
```

adb -s bmqc4h7lmjn7ugrg shell getprop ro.product.model
adb -s bmqc4h7lmjn7ugrg shell getprop ro.build.version.release
adb -s bmqc4h7lmjn7ugrg shell getprop ro.build.version.sdk
adb -s bmqc4h7lmjn7ugrg shell getprop ro.product.manufacturer





### Step 6: Install Your App

```bash
# Install APK on device
adb -s R58M12345678 install /path/to/your/app.apk

# Or install via Android Studio
# Build → Install APK
```

### Step 7: Wireless Debugging (Optional)

For wireless connection (Android 11+):

1. On device: **Settings** → **Developer Options** → **Wireless Debugging**
2. Enable **Wireless Debugging**
3. Tap **Pair device with pairing code**
4. Note the IP address and port

```bash
# Pair device
adb pair <IP_ADDRESS>:<PORT>
# Enter pairing code when prompted

# Connect
adb connect <IP_ADDRESS>:<PORT>

# Verify
adb devices
```

---

## iOS Simulator Setup (macOS)

### Step 1: Install Xcode

1. Open **Mac App Store**
2. Search for **Xcode**
3. Click **Install** (this may take a while)
4. After installation, open Xcode
5. Accept license: `sudo xcodebuild -license accept`
6. Install command line tools: `xcode-select --install`

### Step 2: Install Simulator Runtime

1. Open Xcode
2. Go to **Xcode** → **Settings** → **Platforms** (or **Components**)
3. Download iOS simulators for your target versions
4. Wait for download to complete

### Step 3: List Available Simulators

```bash
# List all available simulators
xcrun simctl list devices

# List available runtimes
xcrun simctl list runtimes

# List devices for specific runtime
xcrun simctl list devices available
```

**Expected output:**
```
== Devices ==
iPhone 15 Pro (XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX) (Shutdown)
iPhone 14 (YYYYYYYY-YYYY-YYYY-YYYY-YYYYYYYYYYYY) (Shutdown)
```

### Step 4: Create Simulator (if needed)

```bash
# Create new simulator
xcrun simctl create "iPhone 15 Pro" "iPhone 15 Pro" "iOS17.0"

# List created simulators
xcrun simctl list devices
```

### Step 5: Boot Simulator

**Using Xcode:**
1. Open Xcode
2. Go to **Window** → **Devices and Simulators**
3. Select simulator
4. Click **Boot**

**Using Command Line:**
```bash
# Boot specific simulator
xcrun simctl boot "iPhone 15 Pro"

# Or boot by UDID
xcrun simctl boot XXXXXXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX

# Open Simulator app
open -a Simulator
```

### Step 6: Get Simulator UDID

```bash
# List booted simulators
xcrun simctl list devices | grep Booted

# Or get UDID of specific device
xcrun simctl list devices | grep "iPhone 15 Pro"
```

**Note the UDID**: `XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX`

### Step 7: Install Your App

```bash
# Install app on simulator
xcrun simctl install booted /path/to/your/app.app

# Or drag and drop .app file onto simulator
```

**Using Xcode:**
1. Build your app in Xcode
2. Select simulator as target
3. Click **Run** (⌘R)

---

## iOS Physical Device Setup (macOS)

### Step 1: Enable Developer Mode (iOS 16+)

1. On your iPhone/iPad: **Settings** → **Privacy & Security**
2. Scroll down to **Developer Mode**
3. Toggle **Developer Mode** ON
4. Restart device when prompted
5. Enter passcode to confirm

### Step 2: Trust Your Computer

1. Connect iPhone/iPad to Mac via USB
2. On device, tap **Trust This Computer**
3. Enter passcode

### Step 3: Register Device in Xcode

1. Open Xcode
2. Connect your device
3. Go to **Window** → **Devices and Simulators**
4. Select your device
5. Click **Use for Development**
6. Sign in with your Apple ID (or add team)

### Step 4: Get Device UDID

**Using Xcode:**
1. **Window** → **Devices and Simulators**
2. Select your device
3. Copy **Identifier** (this is the UDID)

**Using Command Line:**
```bash
# List connected devices
instruments -s devices

# Or using system_profiler
system_profiler SPUSBDataType | grep -A 11 iPhone
```

**Expected output:**
```
iPhone 15 Pro (XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX) [Connected]
```

### Step 5: Install App on Device

**Using Xcode:**
1. Open your project in Xcode
2. Select your device as target
3. Click **Run** (⌘R)
4. First time: Trust developer certificate on device

**Using Command Line:**
```bash
# Install via Xcode command line
xcodebuild -project YourApp.xcodeproj \
  -scheme YourApp \
  -destination 'platform=iOS,id=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX' \
  install
```

### Step 6: Trust Developer Certificate (First Time)

1. On device: **Settings** → **General** → **VPN & Device Management**
2. Tap your developer certificate
3. Tap **Trust**
4. Confirm

---

## Configuration Examples

### Android Emulator Configuration

Edit `config/devices.json`:

```json
[
  {
    "udid": "emulator-5554",
    "deviceName": "Pixel_7_Emulator",
    "platformName": "Android",
    "platformVersion": "13",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4723,
    "systemPort": 8200,
    "chromedriverPort": 9515,
    "app": "/absolute/path/to/your/app.apk",
    "appPackage": "com.yourcompany.yourapp",
    "appActivity": "com.yourcompany.yourapp.MainActivity"
  }
]
```

### Android Physical Device Configuration

```json
[
  {
    "udid": "R58M12345678",
    "deviceName": "Samsung Galaxy S21",
    "platformName": "Android",
    "platformVersion": "12",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4724,
    "systemPort": 8201,
    "chromedriverPort": 9516,
    "app": "/absolute/path/to/your/app.apk",
    "appPackage": "com.yourcompany.yourapp",
    "appActivity": "com.yourcompany.yourapp.MainActivity"
  }
]
```

### iOS Simulator Configuration

```json
[
  {
    "udid": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "deviceName": "iPhone 15 Pro",
    "platformName": "iOS",
    "platformVersion": "17.0",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4723,
    "wdaLocalPort": 8100,
    "app": "/absolute/path/to/your/app.app",
    "bundleId": "com.yourcompany.yourapp"
  }
]
```

### iOS Physical Device Configuration

```json
[
  {
    "udid": "YYYYYYYY-YYYY-YYYY-YYYY-YYYYYYYYYYYY",
    "deviceName": "iPhone 15 Pro",
    "platformName": "iOS",
    "platformVersion": "17.0",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4724,
    "wdaLocalPort": 8101,
    "app": "/absolute/path/to/your/app.app",
    "bundleId": "com.yourcompany.yourapp",
    "xcodeOrgId": "YOUR_TEAM_ID",
    "xcodeSigningId": "iPhone Developer"
  }
]
```

### Multiple Devices Configuration

```json
[
  {
    "udid": "emulator-5554",
    "deviceName": "Pixel_7_Emulator",
    "platformName": "Android",
    "platformVersion": "13",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4723,
    "systemPort": 8200,
    "chromedriverPort": 9515
  },
  {
    "udid": "emulator-5556",
    "deviceName": "Pixel_5_Emulator",
    "platformName": "Android",
    "platformVersion": "11",
    "tier": "standard",
    "executionType": "local",
    "appiumPort": 4724,
    "systemPort": 8201,
    "chromedriverPort": 9516
  },
  {
    "udid": "R58M12345678",
    "deviceName": "Samsung Galaxy S21",
    "platformName": "Android",
    "platformVersion": "12",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4725,
    "systemPort": 8202,
    "chromedriverPort": 9517
  },
  {
    "udid": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "deviceName": "iPhone 15 Pro",
    "platformName": "iOS",
    "platformVersion": "17.0",
    "tier": "premium",
    "executionType": "local",
    "appiumPort": 4726,
    "wdaLocalPort": 8100
  }
]
```

### Important Configuration Notes

1. **Unique Ports**: Each device must have unique ports
   - `appiumPort`: Base Appium server port (4723, 4724, 4725...)
   - `systemPort`: Android system port (8200, 8201, 8202...)
   - `chromedriverPort`: Android ChromeDriver port (9515, 9516, 9517...)
   - `wdaLocalPort`: iOS WebDriverAgent port (8100, 8101, 8102...)

2. **App Paths**: Use absolute paths for app files
   - Android: `/path/to/app.apk`
   - iOS: `/path/to/app.app`

3. **Package/Bundle IDs**: Required for app launch
   - Android: `appPackage` and `appActivity`
   - iOS: `bundleId`

---

## Running Tests

### Step 1: Start Appium Servers

**Automatic (Recommended):**
```bash
./scripts/start-appium-nodes.sh
```

**Manual:**
```bash
# Terminal 1 - First device
appium --port 4723 --base-path /wd/hub

# Terminal 2 - Second device
appium --port 4724 --base-path /wd/hub

# Terminal 3 - Third device (if needed)
appium --port 4725 --base-path /wd/hub
```

### Step 2: Verify Devices are Connected

```bash
# Android
adb devices

# iOS
xcrun simctl list devices | grep Booted
instruments -s devices
```

### Step 3: Run Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginTests

# Run with specific device tier
mvn clean test -Ddevice.tier=premium

# Run with custom thread count
mvn clean test -Dparallel.threads=2
```

### Step 4: View Reports

```bash
# Open latest report
open reports/extent/TestReport-*/Report.html
```

---

## Troubleshooting

### Android Issues

#### Issue 1: Device Not Detected

**Symptom:**
```bash
adb devices
# Shows: List of devices attached (empty)
```

**Solutions:**
1. Check USB cable connection
2. Enable USB debugging on device
3. Accept "Allow USB debugging" prompt
4. Try different USB port/cable
5. Restart ADB server:
   ```bash
   adb kill-server
   adb start-server
   adb devices
   ```

#### Issue 2: Emulator Won't Start

**Symptom:**
```bash
emulator -avd Pixel_7_API_33
# Error or hangs
```

**Solutions:**
1. Check if another emulator is running:
   ```bash
   adb devices
   ```
2. Kill existing emulator processes:
   ```bash
   pkill -f emulator
   ```
3. Start with clean state:
   ```bash
   emulator -avd Pixel_7_API_33 -wipe-data
   ```
4. Check available AVDs:
   ```bash
   emulator -list-avds
   ```

#### Issue 3: "Device Offline"

**Symptom:**
```bash
adb devices
# Shows: emulator-5554    offline
```

**Solutions:**
1. Restart ADB:
   ```bash
   adb kill-server
   adb start-server
   ```
2. Restart emulator/device
3. Check USB debugging is enabled
4. Reconnect device

#### Issue 4: App Installation Fails

**Symptom:**
```bash
adb install app.apk
# Error: INSTALL_FAILED_...
```

**Solutions:**
1. Uninstall existing app:
   ```bash
   adb uninstall com.yourcompany.yourapp
   ```
2. Install with reinstall flag:
   ```bash
   adb install -r app.apk
   ```
3. Check app is compatible with device/emulator
4. Enable "Install via USB" in Developer Options

#### Issue 5: Port Already in Use

**Symptom:**
```
Error: Port 4723 is already in use
```

**Solutions:**
1. Find process using port:
   ```bash
   # macOS/Linux
   lsof -i :4723
   
   # Windows
   netstat -ano | findstr :4723
   ```
2. Kill process:
   ```bash
   # macOS/Linux
   kill -9 <PID>
   
   # Windows
   taskkill /PID <PID> /F
   ```
3. Or use different port in `devices.json`

### iOS Issues

#### Issue 1: Simulator Won't Boot

**Symptom:**
```bash
xcrun simctl boot "iPhone 15 Pro"
# Error or hangs
```

**Solutions:**
1. Check if simulator is already booted:
   ```bash
   xcrun simctl list devices | grep Booted
   ```
2. Shutdown all simulators:
   ```bash
   xcrun simctl shutdown all
   ```
3. Reset simulator:
   ```bash
   xcrun simctl erase "iPhone 15 Pro"
   ```
4. Boot again:
   ```bash
   xcrun simctl boot "iPhone 15 Pro"
   open -a Simulator
   ```

#### Issue 2: Device Not Trusted

**Symptom:**
```
Device is not trusted
```

**Solutions:**
1. On device: **Settings** → **General** → **VPN & Device Management**
2. Trust your developer certificate
3. Reconnect device
4. In Xcode: **Window** → **Devices and Simulators** → **Use for Development**

#### Issue 3: App Won't Install on Physical Device

**Symptom:**
```
Failed to install app
```

**Solutions:**
1. Register device in Xcode
2. Sign app with valid certificate
3. Trust developer certificate on device
4. Check provisioning profile
5. Enable Developer Mode (iOS 16+)

#### Issue 4: WebDriverAgent Build Fails

**Symptom:**
```
WebDriverAgent build failed
```

**Solutions:**
1. Open WebDriverAgent in Xcode:
   ```bash
   open /path/to/appium/node_modules/appium-xcuitest-driver/node_modules/appium-webdriveragent
   ```
2. Select your development team
3. Build WebDriverAgent
4. Run tests on device

### General Issues

#### Issue: Tests Hang or Timeout

**Solutions:**
1. Check Appium server is running:
   ```bash
   curl http://localhost:4723/wd/hub/status
   ```
2. Verify device is connected:
   ```bash
   adb devices  # Android
   xcrun simctl list devices  # iOS
   ```
3. Check device is unlocked
4. Restart Appium server
5. Check logs in `logs/` directory

#### Issue: Port Conflicts

**Solutions:**
1. Use unique ports for each device
2. Stop all Appium servers:
   ```bash
   ./scripts/stop-appium-nodes.sh
   ```
3. Check for processes using ports:
   ```bash
   lsof -i :4723
   ```

---

## Best Practices

### 1. Device Management

- ✅ **Use descriptive device names** in configuration
- ✅ **Keep devices unlocked** during testing
- ✅ **Charge devices** or keep emulators running
- ✅ **Use device tiers** for test organization
- ✅ **Clean up** after test runs

### 2. Port Management

- ✅ **Use sequential ports** (4723, 4724, 4725...)
- ✅ **Document port assignments** in configuration
- ✅ **Check port availability** before starting
- ✅ **Use PortManager** for automatic allocation

### 3. App Management

- ✅ **Use absolute paths** for app files
- ✅ **Keep apps updated** on devices
- ✅ **Uninstall old versions** before installing new ones
- ✅ **Test app installation** manually first

### 4. Testing Strategy

- ✅ **Start with emulators** for development
- ✅ **Use physical devices** for final validation
- ✅ **Test on multiple OS versions**
- ✅ **Use device tiers** for test prioritization

### 5. Performance

- ✅ **Limit parallel threads** to available devices
- ✅ **Don't over-allocate** devices
- ✅ **Monitor device resources** during tests
- ✅ **Clean up** between test runs

---

## Quick Reference

### Android Commands

```bash
# List devices
adb devices

# Get device info
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release

# Install app
adb install app.apk

# Uninstall app
adb uninstall com.package.name

# Start emulator
emulator -avd AVD_NAME &

# List AVDs
emulator -list-avds
```

### iOS Commands

```bash
# List simulators
xcrun simctl list devices

# Boot simulator
xcrun simctl boot "Device Name"

# Shutdown simulator
xcrun simctl shutdown "Device Name"

# Install app
xcrun simctl install booted app.app

# List physical devices
instruments -s devices
```

### Appium Commands

```bash
# Check Appium status
curl http://localhost:4723/wd/hub/status

# Start Appium
appium --port 4723

# Start with specific driver
appium --port 4723 --use-drivers uiautomator2
```

---

## Next Steps

1. ✅ Configure your devices in `config/devices.json`
2. ✅ Start Appium servers
3. ✅ Run your first test
4. ✅ View reports
5. ✅ Explore [Usage Guide](USAGE_GUIDE.md) for writing tests

---

## Additional Resources

- [Complete Setup Guide](SETUP_GUIDE.md) - Full framework setup
- [Usage Guide](USAGE_GUIDE.md) - Writing tests
- [Device Configuration](config/README.md) - Detailed device config
- [Troubleshooting](SETUP_GUIDE.md#troubleshooting) - Common issues

---

**Need Help?** Check the [Documentation Index](DOCUMENTATION_INDEX.md) or open an issue on GitHub.

**Happy Testing!** 🚀
