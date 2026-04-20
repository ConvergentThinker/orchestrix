package testorbit.tests.base;

import testorbit.config.DeviceConfig;
import testorbit.core.DevicePool;
import testorbit.core.DriverFactory;
import testorbit.reporting.ExtentReportManager;
import testorbit.reporting.DeviceStatsManager;
import testorbit.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber Hooks
 * Handles setup and teardown for Cucumber scenarios
 * Integrates with the parallel framework's device pool and driver management
 */
public class CucumberHooks {
    private static final Logger logger = LoggerFactory.getLogger(CucumberHooks.class);
    
    private static DevicePool devicePool;
    private static ThreadLocal<DeviceConfig> deviceConfig = new ThreadLocal<>();
    private static ThreadLocal<String> scenarioName = new ThreadLocal<>();

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        logger.info("══════════════════════════════════════");
        logger.info("Starting Scenario: {} on thread: {}", 
            scenario.getName(), Thread.currentThread().getName());
        logger.info("══════════════════════════════════════");
        
        scenarioName.set(scenario.getName());
        
        // Initialize device pool if not already done
        if (devicePool == null) {
            devicePool = DevicePool.getInstance();
        }
        
        // Get device tier from scenario tags or use default
        String tier = getDeviceTierFromTags(scenario);
        
        // Allocate device from pool with intelligent wait mechanism
        // - Waits indefinitely if other tests are running (devices will be released)
        // - Fails after 60 seconds only if no other tests are running
        DeviceConfig device = devicePool.allocateDeviceWithWait(tier);
        
        if (device == null) {
            logger.error("Failed to allocate device for tier: {} after waiting. Available devices: {}/{}", 
                tier, 
                devicePool.getAvailableDeviceCount(), 
                devicePool.getTotalDeviceCount());
            throw new RuntimeException("No available device for tier: " + tier + 
                " after waiting. Total devices: " + devicePool.getTotalDeviceCount() + 
                ", Available: " + devicePool.getAvailableDeviceCount());
        }
        
        deviceConfig.set(device);
        String deviceIdentifier = device.isCloudDevice() ? 
            device.getCloudDeviceName() != null ? device.getCloudDeviceName() : device.getDeviceName() :
            device.getUdid() != null ? device.getUdid() : device.getDeviceName();
        logger.info("✓ Allocated device: {} ({})", device.getDeviceName(), deviceIdentifier);
        
        // Create driver for this device
        DriverFactory.createDriver(device);
        logger.info("✓ Driver created for: {}", device.getDeviceName());
        
        // Extract feature name from scenario URI
        String featureName = extractFeatureName(scenario);
        
        // Create test in ExtentReports with feature grouping
        ExtentReportManager.getInstance().createTest(
            scenario.getName(), device, featureName);
        ExtentReportManager.getInstance().log(
            com.aventstack.extentreports.Status.INFO, 
            "Scenario started on device: " + device.getDeviceName());
            
        // Record test start in device statistics
        DeviceStatsManager.getInstance().recordTestStart(device, scenario.getName());
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        logger.info("▶ Tearing down scenario: {} on thread: {}", 
            scenario.getName(), Thread.currentThread().getName());
        
        // Get device for statistics recording
        DeviceConfig device = deviceConfig.get();
        
        // Capture screenshot on failure (only if driver was created)
        if (scenario.isFailed()) {
            try {
                // Check if driver exists before capturing screenshot
                DriverFactory.getDriver(); // This will throw if driver not initialized
                String screenshotPath = ScreenshotUtils.captureScreenshot(
                    scenario.getName().replaceAll("[^a-zA-Z0-9]", "_"));
                
                if (screenshotPath != null) {
                    ExtentReportManager.getInstance().addScreenshot(screenshotPath);
                    scenario.attach(
                        screenshotPath, 
                        "image/png", 
                        "Screenshot on Failure");
                }
            } catch (IllegalStateException e) {
                // Driver not initialized, skip screenshot
                logger.debug("Skipping screenshot - driver not initialized: {}", e.getMessage());
            } catch (Exception e) {
                logger.warn("Failed to capture screenshot: {}", e.getMessage());
            }
            
            ExtentReportManager.getInstance().log(
                com.aventstack.extentreports.Status.FAIL, 
                "Scenario failed: " + scenario.getStatus());
                
            // Record test failure in device statistics
            if (device != null) {
                DeviceStatsManager.getInstance().recordTestFail(device, scenario.getName());
            }
        } else {
            ExtentReportManager.getInstance().log(
                com.aventstack.extentreports.Status.PASS, 
                "Scenario passed successfully");
                
            // Record test pass in device statistics
            if (device != null) {
                DeviceStatsManager.getInstance().recordTestPass(device, scenario.getName());
            }
        }
        
        // Quit driver
        try {
            DriverFactory.quitDriver();
            logger.info("✓ Driver quit successfully");
        } catch (Exception e) {
            logger.error("Error quitting driver", e);
        }
        
        // Release device back to pool
        if (device != null) {
            // Use releaseDevice(DeviceConfig) which handles both local and cloud devices
            devicePool.releaseDevice(device);
            logger.info("✓ Released device: {}", device.getDeviceName());
            deviceConfig.remove();
        }
        
        scenarioName.remove();
    }

    /**
     * Extract device tier from scenario tags
     * Looks for @premium, @standard, @basic tags
     */
    private String getDeviceTierFromTags(Scenario scenario) {
        for (String tag : scenario.getSourceTagNames()) {
            if (tag.equals("@premium") || tag.equals("@standard") || tag.equals("@basic")) {
                return tag.substring(1); // Remove @ symbol
            }
        }
        return "standard"; // Default tier
    }

    /**
     * Extract feature name from scenario URI
     * Example: file:///path/to/login.feature -> "User Login" or "login"
     */
    private String extractFeatureName(Scenario scenario) {
        try {
            // Get URI from scenario
            java.net.URI uri = scenario.getUri();
            String uriString = uri.toString();
            
            // Extract feature file name from URI (e.g., login.feature)
            String fileName = uriString.substring(uriString.lastIndexOf('/') + 1);
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
            
            // Capitalize and format feature name (e.g., "login" -> "Login")
            if (fileName.isEmpty()) {
                return "Unknown Feature";
            }
            return fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
        } catch (Exception e) {
            logger.warn("Failed to extract feature name: {}", e.getMessage());
            return "Unknown Feature";
        }
    }

    /**
     * Get current device config (for use in step definitions)
     */
    public static DeviceConfig getCurrentDeviceConfig() {
        return deviceConfig.get();
    }
}
