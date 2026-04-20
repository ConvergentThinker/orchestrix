#!/bin/bash

# Stop Appium Servers Script
# Stops all Appium servers started by start-appium-nodes.sh

echo "════════════════════════════════════════"
echo "  Stopping Appium Server Nodes"
echo "════════════════════════════════════════"

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is required but not installed."
    exit 1
fi

# Parse devices.json and stop servers
jq -c '.[]' config/devices.json | while read device; do
    PORT=$(echo "$device" | jq -r '.appiumPort')
    DEVICE_NAME=$(echo "$device" | jq -r '.deviceName')
    PID_FILE="logs/appium/appium-${PORT}.pid"
    
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null 2>&1; then
            echo "Stopping Appium for: $DEVICE_NAME (PID: $PID)"
            kill "$PID" 2>/dev/null || kill -9 "$PID" 2>/dev/null
            rm "$PID_FILE"
        else
            echo "Process $PID not found for $DEVICE_NAME"
            rm "$PID_FILE"
        fi
    fi
done

# Also kill any remaining Appium processes on known ports
for port in 4723 4724 4725 4726 4727 4728 4729 4730; do
    lsof -ti:$port | xargs kill -9 2>/dev/null || true
done

echo "✓ All Appium servers stopped!"
