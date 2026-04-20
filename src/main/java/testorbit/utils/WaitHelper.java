package testorbit.utils;

import testorbit.core.DriverFactory;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Wait Helper Utility
 * Provides common wait operations for tests
 */
public class WaitHelper {
    private static final Logger logger = LoggerFactory.getLogger(WaitHelper.class);
    private static final int DEFAULT_TIMEOUT = 30;

    /**
     * Wait for element to be visible
     */
    public static WebElement waitForVisible(WebElement element) {
        return waitForVisible(element, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to be visible with custom timeout
     */
    public static WebElement waitForVisible(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to be clickable
     */
    public static WebElement waitForClickable(WebElement element) {
        return waitForClickable(element, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to be clickable with custom timeout
     */
    public static WebElement waitForClickable(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to disappear
     */
    public static boolean waitForInvisible(WebElement element) {
        return waitForInvisible(element, DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to disappear with custom timeout
     */
    public static boolean waitForInvisible(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Wait for text to be present in element
     */
    public static boolean waitForText(WebElement element, String text) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /**
     * Static wait (use sparingly)
     */
    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted", e);
        }
    }
}
