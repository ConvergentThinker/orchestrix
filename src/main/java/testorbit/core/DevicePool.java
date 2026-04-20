package testorbit.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import testorbit.config.DeviceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Device Pool Manager - FPGA Resource Block Pattern
 * Manages device allocation and release for parallel execution
 * 
 * KEY CONCEPTS:
 * - Singleton pattern ensures single pool instance
 * - Thread-safe using ConcurrentHashMap and ReentrantLock
 * - Devices organized by tier (premium/standard/basic)
 * - Automatic conflict prevention
 */
public class DevicePool {
    private static final Logger logger = LoggerFactory.getLogger(DevicePool.class);
    private static DevicePool instance;
    
    // Available devices (not currently in use)
    private final Map<String, DeviceConfig> availableDevices;
    
    // Allocated devices (currently in use by tests)
    private final Map<String, DeviceConfig> allocatedDevices;
    
    // Locks for thread-safe allocation
    private final Map<String, ReentrantLock> deviceLocks;
    
    private final Gson gson = new Gson();

    /**
     * Private constructor - Singleton pattern
     */
    private DevicePool() {
        this.availableDevices = new ConcurrentHashMap<>();
        this.allocatedDevices = new ConcurrentHashMap<>();
        this.deviceLocks = new ConcurrentHashMap<>();
        
        // Load device configuration
        loadDeviceConfiguration();
    }

    /**
     * Get singleton instance
     * Thread-safe lazy initialization
     */
    public static synchronized DevicePool getInstance() {
        if (instance == null) {
            instance = new DevicePool();
        }
        return instance;
    }

    /**
     * Load devices from configuration file
     * IMPORTANT: This defines your device farm
     */
    private void loadDeviceConfiguration() {
        try (FileReader reader = new FileReader("config/devices.json")) {
            // Parse JSON array of devices
            List<DeviceConfig> devices = gson.fromJson(reader, 
                new TypeToken<List<DeviceConfig>>(){}.getType());
            
            if (devices == null || devices.isEmpty()) {
                throw new RuntimeException("No devices found in config/devices.json");
            }
            
            // Port manager for auto-allocation
            PortManager portManager = PortManager.getInstance();
            
            // Add each device to available pool
            for (DeviceConfig device : devices) {
                if (device == null) {
                    logger.warn("Skipping null device in configuration");
                    continue;
                }
                
                // Auto-allocate Appium port if not specified (0 or negative means auto-allocate)
                // NOTE: systemPort/chromedriverPort are allocated per SESSION in DriverFactory,
                // not per device, because multiple parallel sessions on same device need unique ports
                // Only for local devices
                if (!device.isCloudDevice()) {
                    // Auto-allocate Appium port if not specified (one Appium server per device)
                    if (device.getAppiumPort() <= 0) {
                        int appiumPort = portManager.allocateAppiumPort();
                        device.setAppiumPort(appiumPort);
                        logger.debug("Auto-allocated Appium port {} for device: {}", 
                            appiumPort, device.getDeviceName());
                    }
                    
                    // iOS WDA port can be per device (one WDA per device)
                    if ("iOS".equalsIgnoreCase(device.getPlatformName())) {
                        if (device.getWdaLocalPort() <= 0) {
                            int wdaPort = portManager.allocateWdaPort();
                            device.setWdaLocalPort(wdaPort);
                            logger.debug("Auto-allocated WDA port {} for device: {}", 
                                wdaPort, device.getDeviceName());
                        }
                    }
                    // Android systemPort/chromedriverPort are allocated per SESSION in DriverFactory
                }
                
                // Generate unique key for device
                // For local devices: use UDID
                // For cloud devices: use deviceName + executionType + platformName
                String deviceKey = generateDeviceKey(device);
                
                if (deviceKey == null || deviceKey.isEmpty()) {
                    logger.error("Cannot generate device key for device: {}", device.getDeviceName());
                    continue;
                }
                
                // Store device
                availableDevices.put(deviceKey, device);
                
                // Create lock for this device
                deviceLocks.put(deviceKey, new ReentrantLock());
                
                // Log port information
                String portInfo = "";
                if (!device.isCloudDevice()) {
                    portInfo = String.format(" [Appium:%d", device.getAppiumPort());
                    if ("iOS".equalsIgnoreCase(device.getPlatformName())) {
                        portInfo += String.format(", WDA:%d", device.getWdaLocalPort());
                    }
                    // Android systemPort/chromedriverPort allocated per session, not logged here
                    portInfo += "]";
                }
                
                logger.info("Loaded device: {} ({}) - Tier: {} - Execution: {}{}", 
                    device.getDeviceName(), 
                    deviceKey, 
                    device.getTier(),
                    device.getExecutionType(),
                    portInfo);
            }
            
            if (availableDevices.isEmpty()) {
                throw new RuntimeException("No valid devices loaded from config/devices.json");
            }
            
            logger.info("✓ Device pool initialized with {} devices", 
                availableDevices.size());
            
        } catch (Exception e) {
            logger.error("✗ Failed to load device configuration", e);
            throw new RuntimeException("Cannot start without devices!", e);
        }
    }
    
    /**
     * Generate unique key for device
     * Local devices use UDID, cloud devices use composite key
     */
    private String generateDeviceKey(DeviceConfig device) {
        if (device == null) {
            logger.error("Cannot generate key for null device");
            return null;
        }
        
        // Local devices: use UDID
        if (!device.isCloudDevice()) {
            String udid = device.getUdid();
            if (udid != null && !udid.isEmpty()) {
                return udid;
            }
            // Local device without UDID - use device name as fallback
            logger.warn("Local device {} has no UDID, using device name as key", device.getDeviceName());
        }
        
        // Cloud devices: use composite key
        if (device.isCloudDevice()) {
            String deviceName = device.getDeviceName() != null ? device.getDeviceName() : "unknown";
            String executionType = device.getExecutionType() != null ? device.getExecutionType() : "unknown";
            String platformName = device.getPlatformName() != null ? device.getPlatformName() : "unknown";
            String platformVersion = device.getPlatformVersion() != null ? device.getPlatformVersion() : "unknown";
            
            String key = String.format("%s_%s_%s_%s", deviceName, executionType, platformName, platformVersion);
            return key.replaceAll("[^a-zA-Z0-9_]", "_");
        }
        
        // Fallback: use device name if available
        if (device.getDeviceName() != null && !device.getDeviceName().isEmpty()) {
            return device.getDeviceName().replaceAll("[^a-zA-Z0-9_]", "_");
        }
        
        logger.error("Cannot generate device key - device has no identifiable information");
        return null;
    }

    /**
     * Allocate a device from the pool with tier fallback
     * 
     * @param tier Device tier (premium/standard/basic)
     * @return Allocated device or null if none available
     * 
     * HOW IT WORKS:
     * 1. Try to find device matching requested tier
     * 2. If not available, fallback to lower tier (premium->standard->basic)
     * 3. Try to acquire lock on each device
     * 4. First successful lock gets the device
     * 5. Move device from available to allocated
     */
    public DeviceConfig allocateDevice(String tier) {
        // Define tier hierarchy for fallback
        List<String> tierFallback = getTierFallback(tier);
        
        // Try each tier in fallback order
        for (String fallbackTier : tierFallback) {
            DeviceConfig device = tryAllocateDeviceByTier(fallbackTier);
            if (device != null) {
                if (!fallbackTier.equalsIgnoreCase(tier)) {
                    logger.info("⚠ Allocated {} tier device for {} request (fallback)", 
                        fallbackTier, tier);
                }
                return device;
            }
        }

        logger.warn("✗ No available device for tier: {} (tried fallbacks). Current allocation: {}/{}", 
            tier, 
            allocatedDevices.size(), 
            availableDevices.size() + allocatedDevices.size());
        
        return null;
    }
    
    /**
     * Get tier fallback hierarchy.
     * Downward: premium → standard → basic.
     * Upward: when no same/lower-tier device exists, allow using a higher-tier device
     * (e.g. standard request can use premium if no standard/basic).
     */
    private List<String> getTierFallback(String tier) {
        List<String> fallback = new ArrayList<>();
        fallback.add(tier); // Always try requested tier first
        
        if ("premium".equalsIgnoreCase(tier)) {
            fallback.add("standard");
            fallback.add("basic");
        } else if ("standard".equalsIgnoreCase(tier)) {
            fallback.add("basic");
            fallback.add("premium"); // Use premium device when no standard/basic
        } else if ("basic".equalsIgnoreCase(tier)) {
            fallback.add("standard");
            fallback.add("premium"); // Use standard/premium when no basic
        }
        
        return fallback;
    }
    
    /**
     * Try to allocate a device from a specific tier
     * @param tier The tier to allocate from
     * @return Allocated device or null if none available
     */
    private DeviceConfig tryAllocateDeviceByTier(String tier) {
        // Find candidate devices matching tier
        List<DeviceConfig> candidates = availableDevices.values().stream()
            .filter(d -> d != null && d.getTier() != null && d.getTier().equalsIgnoreCase(tier))
            .collect(Collectors.toList());

        // Try to acquire first available device
        for (DeviceConfig device : candidates) {
            String deviceKey = generateDeviceKey(device);
            if (deviceKey == null) {
                continue;
            }
            
            ReentrantLock lock = deviceLocks.get(deviceKey);
            if (lock == null) {
                logger.warn("No lock found for device key: {}", deviceKey);
                continue;
            }
            
            // Try to acquire lock without waiting
            if (lock.tryLock()) {
                try {
                    // Double-check device is still available
                    if (availableDevices.containsKey(deviceKey)) {
                        // Move from available to allocated
                        availableDevices.remove(deviceKey);
                        allocatedDevices.put(deviceKey, device);
                        
                        logger.info("✓ Allocated device: {} ({}) [tier: {}] to thread: {}", 
                            device.getDeviceName(), 
                            deviceKey,
                            device.getTier(),
                            Thread.currentThread().getName());
                        
                        return device;
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        
        return null;
    }

    /**
     * Release device back to pool
     * 
     * @param deviceIdentifier Device unique identifier (UDID for local, composite key for cloud)
     * 
     * CRITICAL: Always call this in @AfterMethod to prevent leaks!
     */
    public void releaseDevice(String deviceIdentifier) {
        if (deviceIdentifier == null || deviceIdentifier.isEmpty()) {
            return;
        }

        ReentrantLock lock = deviceLocks.get(deviceIdentifier);
        if (lock != null) {
            lock.lock();
            try {
                // Move from allocated back to available
                DeviceConfig device = allocatedDevices.remove(deviceIdentifier);
                
                if (device != null) {
                    availableDevices.put(deviceIdentifier, device);
                    
                    logger.info("✓ Released device: {} ({}) from thread: {}", 
                        device.getDeviceName(), 
                        deviceIdentifier,
                        Thread.currentThread().getName());
                } else {
                    logger.warn("Device {} was not allocated", deviceIdentifier);
                }
            } finally {
                lock.unlock();
            }
        } else {
            logger.warn("No lock found for device identifier: {}", deviceIdentifier);
        }
    }
    
    /**
     * Release device by DeviceConfig object
     * Convenience method that generates the key automatically
     */
    public void releaseDevice(DeviceConfig device) {
        if (device == null) {
            return;
        }
        String deviceKey = generateDeviceKey(device);
        if (deviceKey != null) {
            releaseDevice(deviceKey);
        }
    }

    /**
     * Get pool statistics
     * Useful for monitoring and debugging
     */
    public Map<String, Object> getPoolStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        int total = availableDevices.size() + allocatedDevices.size();
        int available = availableDevices.size();
        int allocated = allocatedDevices.size();
        
        stats.put("total", total);
        stats.put("available", available);
        stats.put("allocated", allocated);
        stats.put("utilization", total > 0 ? (double) allocated / total * 100 : 0);
        
        return stats;
    }

    /**
     * Get all available devices (for display purposes)
     */
    public List<DeviceConfig> getAvailableDevices() {
        return new ArrayList<>(availableDevices.values());
    }

    /**
     * Get all allocated devices (for monitoring)
     */
    public List<DeviceConfig> getAllocatedDevices() {
        return new ArrayList<>(allocatedDevices.values());
    }
    
    /**
     * Get total number of devices in pool
     * Useful for determining optimal thread count
     */
    public int getTotalDeviceCount() {
        return availableDevices.size() + allocatedDevices.size();
    }
    
    /**
     * Get number of available devices
     */
    public int getAvailableDeviceCount() {
        return availableDevices.size();
    }
    
    /**
     * Wait for device to become available with intelligent timeout logic
     * 
     * Wait Strategy:
     * - Poll every 10 seconds to try allocating device
     * - Check if other tests are running every 30 seconds
     * - If other tests are running: wait indefinitely (devices will be released)
     * - If no other tests are running: enforce 60 second timeout
     * 
     * @param tier Device tier
     * @return Allocated device or null if timeout (only when no other tests running)
     */
    public DeviceConfig allocateDeviceWithWait(String tier) {
        final long POLL_INTERVAL_MS = 10000; // Poll every 10 seconds
        final long OTHER_TESTS_CHECK_INTERVAL_MS = 30000; // Check other tests every 30 seconds
        final long TIMEOUT_WHEN_NO_OTHER_TESTS_MS = 60000; // 60 seconds timeout when no other tests
        
        long startTime = System.currentTimeMillis();
        long lastOtherTestsCheck = startTime;
        boolean hasSeenOtherTestsRunning = false;
        
        logger.info("⏳ Waiting for device (tier: {}). Polling every {}s, checking other tests every {}s", 
            tier, POLL_INTERVAL_MS / 1000, OTHER_TESTS_CHECK_INTERVAL_MS / 1000);
        
        while (true) {
            // Try to allocate device
            DeviceConfig device = allocateDevice(tier);
            if (device != null) {
                if (hasSeenOtherTestsRunning) {
                    logger.info("✓ Device allocated after waiting (other tests were running)");
                }
                return device;
            }
            
            // Check if other tests are running (every 30 seconds)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastOtherTestsCheck >= OTHER_TESTS_CHECK_INTERVAL_MS) {
                boolean otherTestsRunning = areOtherTestsRunning();
                
                if (otherTestsRunning) {
                    hasSeenOtherTestsRunning = true;
                    logger.debug("Other tests are running - will wait indefinitely for device release");
                } else {
                    // No other tests running - check timeout
                    long elapsedTime = currentTime - startTime;
                    if (elapsedTime >= TIMEOUT_WHEN_NO_OTHER_TESTS_MS) {
                        logger.warn("✗ Timeout waiting for device (tier: {}) after {}ms. " +
                            "No other tests running, so no devices will be released.", 
                            tier, TIMEOUT_WHEN_NO_OTHER_TESTS_MS);
                        return null;
                    } else {
                        logger.debug("No other tests running. Timeout in {}ms if no device becomes available", 
                            TIMEOUT_WHEN_NO_OTHER_TESTS_MS - elapsedTime);
                    }
                }
                
                lastOtherTestsCheck = currentTime;
            }
            
            // Wait before next poll
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted while waiting for device");
                return null;
            }
        }
    }
    
    /**
     * Check if other tests are currently running
     * Determined by checking if any devices are allocated
     * 
     * @return true if other tests are running (devices allocated), false otherwise
     */
    private boolean areOtherTestsRunning() {
        // If there are allocated devices, other tests are running
        boolean otherTestsRunning = !allocatedDevices.isEmpty();
        
        if (otherTestsRunning) {
            logger.debug("Other tests detected: {} device(s) currently allocated", 
                allocatedDevices.size());
        } else {
            logger.debug("No other tests running: all devices available");
        }
        
        return otherTestsRunning;
    }
}
