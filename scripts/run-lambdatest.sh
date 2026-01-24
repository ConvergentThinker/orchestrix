#!/bin/bash

# Run Tests on LambdaTest
# Convenience script for LambdaTest execution

echo "════════════════════════════════════════"
echo "  Running Tests on LambdaTest"
echo "════════════════════════════════════════"

# LambdaTest Credentials
LT_USERNAME="${LT_USERNAME:-extern.arun_pillai@geojit.com}"
LT_ACCESS_KEY="${LT_ACCESS_KEY:-LT_4ScCEN138hrbsA4nLUZ4lQhXn9sQGaQhAkoErRpUvESfz96}"

# App URL
CLOUD_APP_URL="${CLOUD_APP_URL:-lt://APP10160341531766737548177104}"

# Tunnel ID (from devices.json or override)
LT_TUNNEL_ID="${LT_TUNNEL_ID:-GFSLHO15366-i1ez8h0d4gs}"

# Execution options
EXECUTION_TYPE="${1:-lambdatest}"
TEST_SUITE="${2:-testng.xml}"

echo "Configuration:"
echo "  Username: $LT_USERNAME"
echo "  Access Key: ${LT_ACCESS_KEY:0:20}..."
echo "  App URL: $CLOUD_APP_URL"
echo "  Tunnel ID: $LT_TUNNEL_ID"
echo "  Execution Type: $EXECUTION_TYPE"
echo "  Test Suite: $TEST_SUITE"
echo ""

# Run tests
mvn clean test \
  -Dlt.username="$LT_USERNAME" \
  -Dlt.accesskey="$LT_ACCESS_KEY" \
  -Dlt.tunnel.id="$LT_TUNNEL_ID" \
  -Dcloud.app.url="$CLOUD_APP_URL" \
  -Ddevice.execution.type="$EXECUTION_TYPE" \
  -DsuiteXmlFile="$TEST_SUITE" \
  -Dlt.build="LambdaTest Build $(date +%Y%m%d-%H%M%S)" \
  -Dlt.name="Parallel Test Execution" \
  -Dlt.project="Appium Framework Tests"

echo ""
echo "✓ Tests completed!"
echo "View reports in: reports/extent/"
