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
    EXECUTION_TYPE=$(echo "$device" | jq -r '.executionType // "local"')
    
    # Only start Appium for local devices
    if [ "$EXECUTION_TYPE" = "local" ]; then
        # Check if port is already in use
        if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
            echo "⚠️  Port $PORT is already in use. Skipping $DEVICE_NAME"
            continue
        fi
        
        echo "Starting Appium for: $DEVICE_NAME on port $PORT"
        
        # Start Appium server
        appium --port "$PORT" \
               --allow-insecure chromedriver_autodownload \
               --log-timestamp \
               --log "logs/appium/appium-${PORT}.log" \
               --default-capabilities "{\"udid\":\"$UDID\"}" &
        
        # Save PID
        APPIUM_PID=$!
        echo $APPIUM_PID > "logs/appium/appium-${PORT}.pid"
        
        # Wait for server to start
        echo "Waiting for Appium server to start on port $PORT..."
        sleep 3
        
        # Verify server is running (Appium 2+ uses /status, older uses /wd/hub/status)
        if curl -sf "http://127.0.0.1:$PORT/status" > /dev/null 2>&1 || \
           curl -sf "http://127.0.0.1:$PORT/wd/hub/status" > /dev/null 2>&1; then
            echo "✓ Appium server started successfully on port $PORT"
        else
            echo "✗ Failed to start Appium server on port $PORT. Check logs: logs/appium/appium-${PORT}.log"
        fi
    else
        echo "⏭️  Skipping $DEVICE_NAME (cloud device: $EXECUTION_TYPE)"
    fi
done

echo ""
echo "════════════════════════════════════════"
echo "✓ Appium server startup complete!"
echo "Check logs in: logs/appium/"
echo "════════════════════════════════════════"
