#!/bin/bash

# Start Appium Servers Script
# One server per device on unique port

echo "════════════════════════════════════════"
echo "  Starting Appium Server Nodes"
echo "════════════════════════════════════════"

# Create logs directory
mkdir -p logs/appium

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is required but not installed."
    echo "Install with: brew install jq (macOS) or apt-get install jq (Linux)"
    exit 1
fi

# Parse devices.json and start servers
jq -c '.[]' config/devices.json | while read device; do
    UDID=$(echo "$device" | jq -r '.udid')
    PORT=$(echo "$device" | jq -r '.appiumPort')
    DEVICE_NAME=$(echo "$device" | jq -r '.deviceName')
    PLATFORM=$(echo "$device" | jq -r '.platformName')
    
    echo "Starting Appium for: $DEVICE_NAME on port $PORT"
    
    # Start Appium server
    appium --port "$PORT" \
           --allow-insecure chromedriver_autodownload \
           --log-timestamp \
           --log "logs/appium/appium-${PORT}.log" \
           --default-capabilities "{\"udid\":\"$UDID\"}" &
    
    # Save PID
    echo $! > "logs/appium/appium-${PORT}.pid"
    
    sleep 2
done

echo "✓ All Appium servers started!"
echo "Check logs in: logs/appium/"
