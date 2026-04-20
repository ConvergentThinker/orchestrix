#!/bin/bash

# Setup Environment Script
# Verifies and sets up the testing environment

echo "════════════════════════════════════════"
echo "  Setting Up Test Environment"
echo "════════════════════════════════════════"

# Check Java
if ! command -v java &> /dev/null; then
    echo "✗ Java is not installed. Please install Java 17+"
    exit 1
else
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "✓ Java: $JAVA_VERSION"
fi

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "✗ Maven is not installed. Please install Maven 3.6+"
    exit 1
else
    MVN_VERSION=$(mvn --version | head -n 1)
    echo "✓ Maven: $MVN_VERSION"
fi

# Check Node.js
if ! command -v node &> /dev/null; then
    echo "✗ Node.js is not installed. Please install Node.js 18+"
    exit 1
else
    NODE_VERSION=$(node --version)
    echo "✓ Node.js: $NODE_VERSION"
fi

# Check Appium
if ! command -v appium &> /dev/null; then
    echo "⚠ Appium is not installed globally. Installing..."
    npm install -g appium
    appium driver install uiautomator2
    appium driver install xcuitest
else
    APPIUM_VERSION=$(appium --version)
    echo "✓ Appium: $APPIUM_VERSION"
fi

# Check Android SDK (optional)
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠ ANDROID_HOME is not set (optional for Android testing)"
else
    echo "✓ ANDROID_HOME: $ANDROID_HOME"
    if command -v adb &> /dev/null; then
        ADB_VERSION=$(adb version | head -n 1)
        echo "✓ ADB: $ADB_VERSION"
    fi
fi

# Create necessary directories
echo ""
echo "Creating directories..."
mkdir -p logs/appium
mkdir -p reports/{extent,consolidated,screenshots}
mkdir -p .test-cache

echo ""
echo "✓ Environment setup complete!"
