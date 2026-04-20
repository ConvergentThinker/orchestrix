package testorbit.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import testorbit.config.DeviceConfig;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExtentReports Manager
 * Thread-safe reporting for parallel execution
 * Creates timestamped folders for each test run
 */
public class ExtentReportManager {
    private static final Logger logger = LoggerFactory.getLogger(ExtentReportManager.class);
    private static ExtentReportManager instance;
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String currentReportFolder;
    
    // Track feature-level parent tests for hierarchical structure
    private static final ConcurrentHashMap<String, ExtentTest> featureTests = new ConcurrentHashMap<>();

    private ExtentReportManager() {
        initializeReport();
        
        // Add shutdown hook to flush report on JVM shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            flush();
        }));
        
        logger.info("✓ ExtentReportManager initialized with shutdown hook");
    }

    public static synchronized ExtentReportManager getInstance() {
        if (instance == null) {
            instance = new ExtentReportManager();
        }
        return instance;
    }

    private void initializeReport() {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        
        // Create timestamped folder structure: reports/extent/TestReport-<Timestamp>/
        currentReportFolder = "reports/extent/TestReport-" + timestamp;
        String reportPath = currentReportFolder + "/Report.html";
        
        // Create directories if they don't exist (screenshots will be in same folder as HTML)
        File reportDir = new File(currentReportFolder);
        
        try {
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            logger.info("📊 ExtentReport folder created: {}", currentReportFolder);
        } catch (Exception e) {
            logger.warn("Failed to create report directories: {}", e.getMessage());
        }

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Parallel Test Execution Report");
        spark.config().setReportName("Device-Wise Test Results - " + timestamp);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        
        extent.setSystemInfo("Framework", "Appium Parallel Framework");
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Report Generated", timestamp);
        extent.setSystemInfo("Report Location", reportPath);
        
        logger.info("📊 ExtentReport initialized: {}", reportPath);
    }

    /**
     * Create test in report with hierarchical feature structure
     * @param testName Test/scenario name
     * @param device Device configuration
     * @param featureName Feature name for grouping (optional, can be null)
     */
    public void createTest(String testName, DeviceConfig device, String featureName) {
        ExtentTest scenarioTest;
        
        // Create hierarchical structure: Feature -> Scenario
        if (featureName != null && !featureName.isEmpty()) {
            // Get or create feature-level parent test
            ExtentTest featureTest = featureTests.computeIfAbsent(featureName, key -> {
                ExtentTest parent = extent.createTest("Feature: " + featureName)
                    .assignCategory(featureName);
                logger.debug("Created feature parent test: {}", featureName);
                return parent;
            });
            
            // Create scenario as child node under feature
            String scenarioName = testName + " [" + device.getDeviceName() + "]";
            scenarioTest = featureTest.createNode(scenarioName)
                .assignDevice(device.getDeviceName())
                .assignCategory(featureName);
            
            logger.debug("Created scenario child test: {} under feature: {}", scenarioName, featureName);
        } else {
            // Fallback: create standalone test if no feature name
            String fullName = testName + " [" + device.getDeviceName() + "]";
            scenarioTest = extent.createTest(fullName)
                .assignDevice(device.getDeviceName())
                .assignCategory(device.getPlatformName());
        }
        
        // Assign platform as category for filtering
        scenarioTest.assignCategory(device.getPlatformName());
        
        test.set(scenarioTest);
    }
    
    /**
     * Create test in report (overloaded method for backward compatibility)
     */
    public void createTest(String testName, DeviceConfig device) {
        createTest(testName, device, null);
    }

    /**
     * Log message
     */
    public void log(Status status, String message) {
        ExtentTest currentTest = test.get();
        if (currentTest != null) {
            currentTest.log(status, message);
        }
    }

    /**
     * Add screenshot
     * Uses relative path from HTML file location (screenshots are in same folder as HTML)
     */
    public void addScreenshot(String screenshotPath) {
        ExtentTest currentTest = test.get();
        if (currentTest != null) {
            try {
                // Extract just the filename for relative path (screenshots are in same folder as HTML)
                File screenshotFile = new File(screenshotPath);
                String fileName = screenshotFile.getName();
                // Use relative path - just the filename since screenshot is in same folder as HTML
                currentTest.addScreenCaptureFromPath(fileName);
            } catch (Exception e) {
                logger.warn("Failed to attach screenshot: {}", e.getMessage());
                currentTest.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
            }
        }
    }

    /**
     * Flush report
     */
    public void flush() {
        if (extent != null) {
            extent.flush();
            logger.info("📊 ExtentReport flushed to: {}/Report.html", currentReportFolder);
        }
    }
    
    /**
     * Get current report folder path for screenshots and other assets
     */
    public static String getCurrentReportFolder() {
        return currentReportFolder;
    }
    
    /**
     * Get current screenshots folder path (same folder as HTML file)
     */
    public static String getCurrentScreenshotsFolder() {
        // Screenshots are stored in the same folder as the HTML file
        return currentReportFolder != null ? currentReportFolder : "reports/extent";
    }
}
