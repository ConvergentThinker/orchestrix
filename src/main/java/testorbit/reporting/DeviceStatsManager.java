package testorbit.reporting;

import testorbit.config.DeviceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Device Statistics Manager
 * Thread-safe collection of test execution statistics per device
 * Integrates with ConsolidatedReportBuilder for device-wise reports
 */
public class DeviceStatsManager {
    private static final Logger logger = LoggerFactory.getLogger(DeviceStatsManager.class);
    private static DeviceStatsManager instance;
    private final ConcurrentHashMap<String, ConsolidatedReportBuilder.DeviceStats> deviceStats;

    private DeviceStatsManager() {
        this.deviceStats = new ConcurrentHashMap<>();
        
        // Add shutdown hook to generate final consolidated report
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            generateConsolidatedReport();
        }));
        
        logger.info("✓ DeviceStatsManager initialized with shutdown hook");
    }

    public static synchronized DeviceStatsManager getInstance() {
        if (instance == null) {
            instance = new DeviceStatsManager();
        }
        return instance;
    }

    /**
     * Record test start for a device
     */
    public void recordTestStart(DeviceConfig device, String testName) {
        String deviceKey = getDeviceKey(device);
        deviceStats.computeIfAbsent(deviceKey, k -> new ConsolidatedReportBuilder.DeviceStats());
        
        synchronized (deviceStats.get(deviceKey)) {
            deviceStats.get(deviceKey).total++;
        }
        
        logger.debug("📊 Test started: {} on device: {} (Total: {})", 
            testName, deviceKey, deviceStats.get(deviceKey).total);
    }

    /**
     * Record test pass for a device
     */
    public void recordTestPass(DeviceConfig device, String testName) {
        String deviceKey = getDeviceKey(device);
        ConsolidatedReportBuilder.DeviceStats stats = deviceStats.get(deviceKey);
        
        if (stats != null) {
            synchronized (stats) {
                stats.passed++;
            }
            logger.debug("✅ Test passed: {} on device: {} (Passed: {}/{})", 
                testName, deviceKey, stats.passed, stats.total);
        }
    }

    /**
     * Record test failure for a device
     */
    public void recordTestFail(DeviceConfig device, String testName) {
        String deviceKey = getDeviceKey(device);
        ConsolidatedReportBuilder.DeviceStats stats = deviceStats.get(deviceKey);
        
        if (stats != null) {
            synchronized (stats) {
                stats.failed++;
            }
            logger.debug("❌ Test failed: {} on device: {} (Failed: {}/{})", 
                testName, deviceKey, stats.failed, stats.total);
        }
    }

    /**
     * Get device key for statistics tracking
     */
    private String getDeviceKey(DeviceConfig device) {
        if (device.isCloudDevice()) {
            return device.getCloudDeviceName() != null ? 
                device.getCloudDeviceName() : device.getDeviceName();
        }
        return device.getDeviceName();
    }

    /**
     * Generate consolidated device-wise report
     */
    public void generateConsolidatedReport() {
        if (deviceStats.isEmpty()) {
            logger.warn("⚠ No device statistics collected - skipping consolidated report");
            return;
        }
        
        logger.info("📊 Generating consolidated device-wise report...");
        
        // Create base reports directory if it doesn't exist
        try {
            java.nio.file.Files.createDirectories(
                java.nio.file.Paths.get("reports/consolidated"));
        } catch (Exception e) {
            logger.warn("Failed to create reports directory: {}", e.getMessage());
        }
        
        // Generate report
        ConsolidatedReportBuilder.generateReport(deviceStats);
        
        // Log summary
        logStatsSummary();
    }

    /**
     * Log statistics summary to console
     */
    private void logStatsSummary() {
        logger.info("📊 Device-Wise Test Execution Summary:");
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        deviceStats.forEach((deviceName, stats) -> {
            double passRate = stats.getPassRate();
            String status = passRate == 100.0 ? "✅" : passRate >= 80.0 ? "⚠️" : "❌";
            
            logger.info("{} {} - Total: {}, Passed: {}, Failed: {}, Pass Rate: {:.1f}%", 
                status, deviceName, stats.total, stats.passed, stats.failed, passRate);
        });
        
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Overall statistics
        int totalTests = deviceStats.values().stream().mapToInt(s -> s.total).sum();
        int totalPassed = deviceStats.values().stream().mapToInt(s -> s.passed).sum();
        double overallPassRate = totalTests > 0 ? (double) totalPassed / totalTests * 100 : 0;
        
        logger.info("📋 Overall Summary: {} devices, {} tests, {:.1f}% pass rate", 
            deviceStats.size(), totalTests, overallPassRate);
    }

    /**
     * Force generate report (for testing purposes)
     */
    public void forceGenerateReport() {
        generateConsolidatedReport();
    }

    /**
     * Get current device statistics (for debugging)
     */
    public ConcurrentHashMap<String, ConsolidatedReportBuilder.DeviceStats> getDeviceStats() {
        return new ConcurrentHashMap<>(deviceStats);
    }

    /**
     * Clear all statistics (for testing purposes)
     */
    public void clearStats() {
        deviceStats.clear();
        logger.info("📊 Device statistics cleared");
    }
}