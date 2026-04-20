#!/bin/bash

# Calculate Optimal Thread Count Based on Available Devices
# This script reads devices.json and calculates the optimal thread count

echo "════════════════════════════════════════"
echo "  Calculating Optimal Thread Count"
echo "════════════════════════════════════════"

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is required but not installed."
    echo "Install with: brew install jq (macOS) or apt-get install jq (Linux)"
    exit 1
fi

# Count total devices in devices.json
TOTAL_DEVICES=$(jq '. | length' config/devices.json 2>/dev/null || echo "0")

if [ "$TOTAL_DEVICES" -eq "0" ]; then
    echo "⚠️  No devices found in config/devices.json"
    echo "Recommended thread-count: 1"
    exit 1
fi

echo "Total devices in config/devices.json: $TOTAL_DEVICES"
echo ""
echo "Recommended thread-count: $TOTAL_DEVICES"
echo "Recommended data-provider-thread-count: $TOTAL_DEVICES"
echo ""
echo "Update testng-cucumber.xml:"
echo "  thread-count=\"$TOTAL_DEVICES\""
echo "  data-provider-thread-count=\"$TOTAL_DEVICES\""
echo ""
echo "Or set Maven property:"
echo "  mvn clean test -Dparallel.threads=$TOTAL_DEVICES"
echo ""
echo "════════════════════════════════════════"
