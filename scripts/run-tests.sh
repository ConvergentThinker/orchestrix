#!/bin/bash

# Run Tests Script
# Convenience script to run tests with common configurations

echo "════════════════════════════════════════"
echo "  Running Parallel Appium Tests"
echo "════════════════════════════════════════"

# Default values
THREAD_COUNT=${1:-8}
DEVICE_TIER=${2:-all}
CACHE_ENABLED=${3:-false}

echo "Configuration:"
echo "  Thread Count: $THREAD_COUNT"
echo "  Device Tier: $DEVICE_TIER"
echo "  Cache Enabled: $CACHE_ENABLED"
echo ""

# Run tests
mvn clean test \
    -Dparallel.threads=$THREAD_COUNT \
    -Ddevice.tier=$DEVICE_TIER \
    -Dcache.enabled=$CACHE_ENABLED

echo ""
echo "✓ Tests completed!"
echo "View reports in: reports/extent/"
