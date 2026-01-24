package com.parallel.appium.tests.base;

import com.parallel.appium.config.DeviceConfig;
import com.parallel.appium.core.DevicePool;
import com.parallel.appium.core.DriverFactory;
import com.parallel.appium.reporting.DeviceStatsManager;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base Test Class
 * All test classes should extend this
 * 
 * LIFECYCLE:
 * 1. BeforeMethod: Allocate device → Create driver
 * 2. Test executes with isolated driver
 * 3. AfterMethod: Quit driver → Release device
 */
public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    
    // Thread-safe storage
    protected ThreadLocal<DeviceConfig> deviceConfig = new ThreadLocal<>();
    
    // Managers
    protected DevicePool devicePool;

    @BeforeClass
    public void setupClass() {
        devicePool = DevicePool.getInstance();
        logger.info("══════════════════════════════════════");
        logger.info("Test Class: {} starting", this.getClass().getSimpleName());
        logger.info("══════════════════════════════════════");
    }

    @BeforeMethod
    @Parameters({"device-tier"})
    public void setUp(@Optional("standard") String tier) {
        logger.info("▶ Setting up test on thread: {}", Thread.currentThread().getName());
        
        // Step 1: Allocate device from pool
        DeviceConfig device = devicePool.allocateDevice(tier);
        
        if (device == null) {
            throw new RuntimeException("No available device for tier: " + tier);
        }
        
        deviceConfig.set(device);
        String deviceIdentifier = device.isCloudDevice() ? 
            (device.getCloudDeviceName() != null ? device.getCloudDeviceName() : device.getDeviceName()) :
            (device.getUdid() != null ? device.getUdid() : device.getDeviceName());
        logger.info("✓ Allocated device: {} ({})", device.getDeviceName(), deviceIdentifier);
        
        // Step 2: Create driver for this device
        try {
            DriverFactory.createDriver(device);
            logger.info("✓ Driver created for: {}", device.getDeviceName());
        } catch (Exception e) {
            // If driver creation fails, release the device immediately
            logger.error("✗ Driver creation failed, releasing device", e);
            devicePool.releaseDevice(device);
            deviceConfig.remove();
            throw new RuntimeException("Driver creation failed for device: " + device.getDeviceName(), e);
        }
        
        // Step 3: Record test start in device statistics
        DeviceStatsManager.getInstance().recordTestStart(device, 
            Thread.currentThread().getName() + "_Test");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        logger.info("▶ Tearing down test on thread: {}", Thread.currentThread().getName());
        
        DeviceConfig device = deviceConfig.get();
        
        // Step 1: Record test result in device statistics
        if (device != null) {
            String testName = result.getMethod().getMethodName();
            if (result.getStatus() == ITestResult.SUCCESS) {
                DeviceStatsManager.getInstance().recordTestPass(device, testName);
            } else {
                DeviceStatsManager.getInstance().recordTestFail(device, testName);
            }
        }
        
        // Step 2: Quit driver
        try {
            DriverFactory.quitDriver();
            logger.info("✓ Driver quit successfully");
        } catch (Exception e) {
            logger.error("Error quitting driver", e);
        }
        
        // Step 3: Release device back to pool
        if (device != null) {
            // Use releaseDevice(DeviceConfig) which handles both local and cloud devices
            devicePool.releaseDevice(device);
            logger.info("✓ Released device: {}", device.getDeviceName());
            deviceConfig.remove();
        }
    }

    /**
     * Get driver for current thread
     * Use this in test methods
     */
    protected AppiumDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Get device config for current thread
     */
    protected DeviceConfig getDeviceConfig() {
        return deviceConfig.get();
    }
}
