package testorbit.core;

import testorbit.cloud.CloudProvider;
import testorbit.cloud.CloudProviderFactory;
import testorbit.config.DeviceConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Driver Factory - ThreadLocal Pattern Implementation
 * Creates and manages isolated driver instances per thread
 * 
 * KEY CONCEPTS:
 * - ThreadLocal stores one driver per thread
 * - Each thread gets its own driver instance
 * - No sharing = no conflicts
 * - Must remove() after use to prevent memory leaks
 */
public class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    
    // ThreadLocal storage for drivers
    // Each thread gets its own driver instance
    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    
    // ThreadLocal storage for session-specific ports (Android systemPort/chromedriverPort)
    // These need to be released when driver quits
    private static ThreadLocal<int[]> sessionPorts = new ThreadLocal<>();
    
    /**
     * Create driver for device
     * 
     * @param device Device configuration
     * @return AppiumDriver instance (Android or iOS)
     * 
     * CRITICAL: This method is thread-safe
     * Each thread calling this gets its own driver
     */
    public static AppiumDriver createDriver(DeviceConfig device) {
        logger.info("Creating driver for device: {} on thread: {}", 
            device.getDeviceName(), 
            Thread.currentThread().getName());

        try {
            // Build capabilities
            DesiredCapabilities capabilities = buildCapabilities(device);
            
            // Note: LambdaTest credentials are set in LambdaTestProvider.buildCapabilities()
            // They are set both in lt:options and at root level
            // Appium 2.x will namespace root-level capabilities, but lt:options should work
            
            // Create driver based on platform
            AppiumDriver appiumDriver = createPlatformDriver(device, capabilities);
            
            // Configure timeouts
            appiumDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));
            
            // Store in ThreadLocal
            driver.set(appiumDriver);
            
            logger.info("✓ Driver created successfully for: {}", device.getDeviceName());
            
            return appiumDriver;
            
        } catch (Exception e) {
            logger.error("✗ Failed to create driver for: {}", device.getDeviceName(), e);
            RuntimeException toThrow = new RuntimeException("Driver creation failed", e);
            if (!device.isCloudDevice() && isConnectException(e)) {
                int port = resolveAppiumPort(device);
                String hint = String.format(
                    "Could not connect to Appium at http://127.0.0.1:%d. Is Appium running? " +
                    "Start with: appium --port %d  OR  ./scripts/start-appium-nodes.sh  " +
                    "If using default appium (port 4723), run: mvn test -Dappium.port=4723",
                    port, port);
                toThrow = new RuntimeException("Driver creation failed: " + hint, e);
            }
            throw toThrow;
        }
    }

    private static boolean isConnectException(Throwable t) {
        for (Throwable x = t; x != null; x = x.getCause()) {
            if (x instanceof ConnectException || (x.getMessage() != null && x.getMessage().contains("ConnectException"))) {
                return true;
            }
            if (x instanceof java.io.UncheckedIOException && x.getCause() instanceof ConnectException) {
                return true;
            }
        }
        return false;
    }

    private static int resolveAppiumPort(DeviceConfig device) {
        int port = device.getAppiumPort();
        String override = System.getProperty("appium.port");
        if (override != null && !override.isEmpty()) {
            try {
                return Integer.parseInt(override.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return port;
    }

    /**
     * Build desired capabilities for device
     * Supports both local and cloud devices
     */
    private static DesiredCapabilities buildCapabilities(DeviceConfig device) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        // Common capabilities
        capabilities.setCapability("platformName", device.getPlatformName());
        capabilities.setCapability("platformVersion", device.getPlatformVersion());
        
        // Use cloud device name if available, otherwise use regular device name
        String deviceName = device.getCloudDeviceName() != null && !device.getCloudDeviceName().isEmpty() 
            ? device.getCloudDeviceName() 
            : device.getDeviceName();
        capabilities.setCapability("deviceName", deviceName);
        
        // UDID only for local devices
        if (!device.isCloudDevice() && device.getUdid() != null) {
            capabilities.setCapability("udid", device.getUdid());
        }
        
        capabilities.setCapability("newCommandTimeout", 300);
        capabilities.setCapability("noReset", false);
        
        // Platform-specific capabilities (only for local devices)
        if (!device.isCloudDevice()) {
            if ("Android".equalsIgnoreCase(device.getPlatformName())) {
                // Android capabilities
                capabilities.setCapability("automationName", "UiAutomator2");
                
                // Allocate systemPort and chromedriverPort per SESSION (not per device)
                // Multiple parallel sessions on same device need unique ports
                // Always allocate per session to avoid conflicts, even if device config has ports
                PortManager portManager = PortManager.getInstance();
                int systemPort = portManager.allocateSystemPort();
                int chromePort = portManager.allocateChromedriverPort();
                
                capabilities.setCapability("systemPort", systemPort);
                capabilities.setCapability("chromedriverPort", chromePort);
                
                // Store ports for release when driver quits
                sessionPorts.set(new int[]{systemPort, chromePort});
                
                logger.debug("Allocated session ports: systemPort={}, chromedriverPort={} (device config ignored for parallel safety)", 
                    systemPort, chromePort);
                String appPath = getAndroidAppPath();
                if (appPath != null && !appPath.isEmpty()) {
                    capabilities.setCapability("app", appPath);
                }
                // appPackage/appActivity used for already-installed app, or with app
                String pkg = device.getAppPackage() != null && !device.getAppPackage().isEmpty()
                    ? device.getAppPackage() : "com.yourapp.package";
                String act = device.getAppActivity() != null && !device.getAppActivity().isEmpty()
                    ? device.getAppActivity() : "com.yourapp.MainActivity";
                capabilities.setCapability("appPackage", pkg);
                capabilities.setCapability("appActivity", act);
                
            } else if ("iOS".equalsIgnoreCase(device.getPlatformName())) {
                // iOS capabilities
                capabilities.setCapability("automationName", "XCUITest");
                capabilities.setCapability("wdaLocalPort", device.getWdaLocalPort());
                capabilities.setCapability("app", getIOSAppPath());
                capabilities.setCapability("bundleId", "com.yourapp.bundle");
            }
        } else {
            // Cloud devices - set automation name
            if ("Android".equalsIgnoreCase(device.getPlatformName())) {
                capabilities.setCapability("automationName", "UiAutomator2");
            } else if ("iOS".equalsIgnoreCase(device.getPlatformName())) {
                capabilities.setCapability("automationName", "XCUITest");
            }
            
            // Cloud-specific app handling (if app URL provided)
            // Priority: Device config (cloudAppUrl) > System property > Device capabilities > Default
            String appUrl = device.getCloudAppUrl();
            if (appUrl == null || appUrl.isEmpty()) {
                appUrl = System.getProperty("cloud.app.url");
            }
            if (appUrl == null || appUrl.isEmpty()) {
                // Check if app URL is in device capabilities
                if (device.getCapabilities() != null && device.getCapabilities().containsKey("app")) {
                    appUrl = device.getCapabilities().get("app").toString();
                }
            }
            if (appUrl != null && !appUrl.isEmpty()) {
                capabilities.setCapability("app", appUrl);
                logger.info("Using cloud app URL: {} (from: {})", 
                    appUrl, 
                    device.getCloudAppUrl() != null ? "device config" : "system property");
            } else {
                logger.warn("No app URL provided for cloud device. Tests may fail if app is required.");
            }
        }
        
        // Apply cloud provider enhancements if cloud device
        if (device.isCloudDevice()) {
            // Get credentials with priority: Device config > System property > Environment variable
            String username = device.getCloudUsername();
            if (username == null || username.isEmpty()) {
                username = null; // Will use env/system property in provider
            }
            
            String accessKey = device.getCloudAccessKey();
            if (accessKey == null || accessKey.isEmpty()) {
                accessKey = null; // Will use env/system property in provider
            }
            
            String tunnelId = device.getTunnelId();
            if (tunnelId == null || tunnelId.isEmpty()) {
                tunnelId = null; // Will use env/system property in provider
            }
            
            CloudProvider cloudProvider = CloudProviderFactory.createProvider(
                device.getCloudProvider() != null ? device.getCloudProvider() : device.getExecutionType(),
                username,
                accessKey,
                tunnelId
            );
            
            if (cloudProvider != null) {
                if (!cloudProvider.validateCredentials()) {
                    throw new RuntimeException("Invalid cloud provider credentials for: " + 
                        cloudProvider.getProviderName() + ". Please check credentials in devices.json or environment variables.");
                }
                capabilities = cloudProvider.buildCapabilities(capabilities);
                logger.info("Applied {} cloud capabilities (credentials from: {})", 
                    cloudProvider.getProviderName(),
                    device.getCloudUsername() != null ? "device config" : "env/system property");
            }
        }
        
        logger.debug("Built capabilities for: {} (execution: {})", 
            device.getDeviceName(), device.getExecutionType());
        return capabilities;
    }

    /**
     * Create platform-specific driver
     * Supports both local and cloud devices
     */
    private static AppiumDriver createPlatformDriver(
            DeviceConfig device, 
            DesiredCapabilities capabilities) throws MalformedURLException {
        
        URL url;
        
        // Determine server URL based on execution type
        if (device.isCloudDevice()) {
            // Cloud device - use cloud provider URL
            CloudProvider cloudProvider = CloudProviderFactory.createProvider(
                device.getCloudProvider() != null ? device.getCloudProvider() : device.getExecutionType(),
                device.getCloudUsername(),
                device.getCloudAccessKey(),
                device.getTunnelId() // Pass tunnel ID for LambdaTest
            );
            
            if (cloudProvider == null) {
                throw new RuntimeException("Cloud provider not configured for device: " + 
                    device.getDeviceName());
            }
            
            url = new URL(cloudProvider.getServerUrl());
            // Log base URL only (credentials may be embedded)
            String logUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() > 0 ? ":" + url.getPort() : "") + url.getPath();
            logger.info("Using {} cloud server: {}", cloudProvider.getProviderName(), logUrl);
            
        } else {
            // Local device - use local Appium server
            // Allow override via -Dappium.port=4723 when running default `appium` (port 4723)
            int port = resolveAppiumPort(device);
            if (System.getProperty("appium.port") != null && !System.getProperty("appium.port").isEmpty()) {
                logger.info("Using Appium port override: {} (-Dappium.port)", port);
            }
            String appiumUrl = String.format("http://127.0.0.1:%d", port);
            url = new URL(appiumUrl);
            logger.info("Using local Appium server: {}", url);
        }
        
        // Create driver based on platform
        if ("Android".equalsIgnoreCase(device.getPlatformName()) || 
            "android".equalsIgnoreCase(device.getPlatformName())) {
            return new AndroidDriver(url, capabilities);
        } else if ("iOS".equalsIgnoreCase(device.getPlatformName()) || 
                   "ios".equalsIgnoreCase(device.getPlatformName())) {
            return new IOSDriver(url, capabilities);
        } else {
            throw new IllegalArgumentException("Unsupported platform: " + 
                device.getPlatformName());
        }
    }

    /**
     * Get current thread's driver
     * 
     * USAGE: 
     * AppiumDriver driver = DriverFactory.getDriver();
     * driver.findElement(...);
     */
    public static AppiumDriver getDriver() {
        AppiumDriver currentDriver = driver.get();
        
        if (currentDriver == null) {
            throw new IllegalStateException(
                "Driver not initialized for thread: " + Thread.currentThread().getName() + 
                ". Call createDriver() first!");
        }
        
        return currentDriver;
    }

    /**
     * Quit driver and remove from ThreadLocal
     * 
     * CRITICAL: Always call this in @AfterMethod
     * Prevents memory leaks
     */
    public static void quitDriver() {
        AppiumDriver currentDriver = driver.get();
        
        if (currentDriver != null) {
            try {
                currentDriver.quit();
                logger.info("✓ Driver quit for thread: {}", 
                    Thread.currentThread().getName());
            } catch (Exception e) {
                logger.error("Error quitting driver", e);
            } finally {
                // Release session-specific ports if they were auto-allocated
                int[] ports = sessionPorts.get();
                if (ports != null && ports.length == 2) {
                    PortManager portManager = PortManager.getInstance();
                    portManager.releasePort(ports[0]);  // systemPort
                    portManager.releasePort(ports[1]);  // chromedriverPort
                    logger.debug("Released session ports: systemPort={}, chromedriverPort={}", 
                        ports[0], ports[1]);
                }
                
                // CRITICAL: Remove from ThreadLocal to prevent memory leak
                driver.remove();
                sessionPorts.remove();
            }
        }
    }

    /**
     * Get Android app path.
     * Use -Dandroid.app.path=/path/to/app.apk to install and launch app.
     * If not set, only appPackage/appActivity are used (already-installed app).
     */
    private static String getAndroidAppPath() {
        return System.getProperty("android.app.path", "");
    }

    /**
     * Get iOS app path
     * Override with system property: -Dios.app.path=/path/to/app.ipa
     */
    private static String getIOSAppPath() {
        return System.getProperty("ios.app.path", "apps/ios/sample-app.ipa");
    }
}
