#!/bin/bash

echo "🚀 Timestamped Reports Demo"
echo "=============================="

echo ""
echo "📂 Current reports structure:"
echo "ExtentReports:"
ls -la reports/extent/ 2>/dev/null || echo "No previous ExtentReports found"
echo "Device-Wise Reports:"
ls -la reports/consolidated/ 2>/dev/null || echo "No previous Device-Wise reports found"

echo ""
echo "🧪 Running demo tests to generate timestamped reports..."
mvn clean test -Dtest=DeviceReportsTest -q

echo ""
echo "📊 New reports structure after test run:"
echo "ExtentReports:"
ls -la reports/extent/
echo "Device-Wise Reports:"
ls -la reports/consolidated/

echo ""
echo "📁 Latest report folder contents:"
LATEST_EXTENT_FOLDER=$(ls -t reports/extent/ 2>/dev/null | head -1)
LATEST_DEVICE_FOLDER=$(ls -t reports/consolidated/ 2>/dev/null | head -1)

if [ -n "$LATEST_EXTENT_FOLDER" ]; then
    echo "ExtentReport folder: reports/extent/$LATEST_EXTENT_FOLDER"
    ls -la "reports/extent/$LATEST_EXTENT_FOLDER/"
    
    echo ""
    echo "📱 Screenshots folder:"
    if [ -d "reports/extent/$LATEST_EXTENT_FOLDER/screenshots" ]; then
        ls -la "reports/extent/$LATEST_EXTENT_FOLDER/screenshots/"
    else
        echo "No screenshots generated (no test failures)"
    fi
else
    echo "❌ No ExtentReport folders found - check test execution"
fi

if [ -n "$LATEST_DEVICE_FOLDER" ]; then
    echo ""
    echo "Device-Wise Report folder: reports/consolidated/$LATEST_DEVICE_FOLDER"
    ls -la "reports/consolidated/$LATEST_DEVICE_FOLDER/"
else
    echo "❌ No Device-Wise Report folders found - check test execution"
fi

echo ""
echo "🎯 Quick access commands:"
if [ -n "$LATEST_EXTENT_FOLDER" ]; then
    echo "open reports/extent/$LATEST_EXTENT_FOLDER/reports/ExtentReport.html"
fi
if [ -n "$LATEST_DEVICE_FOLDER" ]; then
    echo "open reports/consolidated/$LATEST_DEVICE_FOLDER/DeviceReport.html"
fi

echo ""
echo "✅ Demo complete! Check the timestamped folder structure above."