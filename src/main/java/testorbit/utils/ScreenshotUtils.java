package testorbit.utils;

import testorbit.core.DriverFactory;
import testorbit.reporting.ExtentReportManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot Utility
 * Captures screenshots on test failures
 * Saves to timestamped ExtentReport folder structure
 */
public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);

    /**
     * Capture screenshot
     * @param testName Test name for file naming
     * @return Path to screenshot file
     */
    public static String captureScreenshot(String testName) {
        try {
            AppiumDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            
            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            
            // Use timestamped screenshots folder from ExtentReportManager
            String screenshotFolder = ExtentReportManager.getCurrentScreenshotsFolder();
            String filePath = screenshotFolder + "/" + fileName;
            
            // Create directory if not exists
            new File(screenshotFolder).mkdirs();
            
            // Check if driver supports TakesScreenshot
            if (driver instanceof TakesScreenshot) {
                File screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);
                
                Files.copy(screenshot.toPath(), 
                          Paths.get(filePath),
                          StandardCopyOption.REPLACE_EXISTING);
                
                logger.info("📸 Screenshot captured: {}", filePath);
                return filePath;
            } else {
                logger.warn("Driver does not support TakesScreenshot interface");
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Screenshot failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
