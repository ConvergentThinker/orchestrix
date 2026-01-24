package com.parallel.appium.tests.reporting;

import com.parallel.appium.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class to demonstrate Device-Wise Reports functionality
 * These tests will run on different devices and generate device statistics
 */
public class DeviceReportsTest extends BaseTest {

    @Test(groups = {"device-reports", "premium"})
    public void testDeviceReporting_Pass() {
        logger.info("🧪 Running Device Reports Test (Pass) on: {}", getDeviceConfig().getDeviceName());
        
        // Simulate a successful test
        Assert.assertTrue(true, "This test should always pass");
        
        logger.info("✅ Device Reports Test passed on: {}", getDeviceConfig().getDeviceName());
    }

    @Test(groups = {"device-reports", "premium"})
    public void testDeviceReporting_AnotherPass() {
        logger.info("🧪 Running Another Device Reports Test (Pass) on: {}", getDeviceConfig().getDeviceName());
        
        // Simulate another successful test
        String deviceName = getDeviceConfig().getDeviceName();
        Assert.assertNotNull(deviceName, "Device name should not be null");
        Assert.assertTrue(deviceName.length() > 0, "Device name should not be empty");
        
        logger.info("✅ Another Device Reports Test passed on: {}", deviceName);
    }

    @Test(groups = {"device-reports", "standard"}, enabled = false)
    public void testDeviceReporting_Fail() {
        logger.info("🧪 Running Device Reports Test (Fail) on: {}", getDeviceConfig().getDeviceName());
        
        // Simulate a failed test (disabled by default to avoid noise)
        Assert.fail("This test is designed to fail for device statistics demonstration");
    }
}