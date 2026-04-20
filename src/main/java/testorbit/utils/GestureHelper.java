package testorbit.utils;

import testorbit.core.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Gesture Helper Utility
 * Provides common gesture operations for mobile testing
 */
public class GestureHelper {
    private static final Logger logger = LoggerFactory.getLogger(GestureHelper.class);

    /**
     * Swipe from one point to another
     */
    public static void swipe(int startX, int startY, int endX, int endY) {
        AppiumDriver driver = DriverFactory.getDriver();
        TouchAction<?> touchAction = new TouchAction<>((PerformsTouchActions) driver);
        
        touchAction.press(PointOption.point(startX, startY))
                   .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                   .moveTo(PointOption.point(endX, endY))
                   .release()
                   .perform();
        
        logger.debug("Swiped from ({}, {}) to ({}, {})", startX, startY, endX, endY);
    }

    /**
     * Swipe down (scroll down)
     */
    public static void swipeDown() {
        AppiumDriver driver = DriverFactory.getDriver();
        Dimension size = driver.manage().window().getSize();
        
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        
        swipe(startX, startY, startX, endY);
    }

    /**
     * Swipe up (scroll up)
     */
    public static void swipeUp() {
        AppiumDriver driver = DriverFactory.getDriver();
        Dimension size = driver.manage().window().getSize();
        
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        
        swipe(startX, startY, startX, endY);
    }

    /**
     * Swipe left
     */
    public static void swipeLeft() {
        AppiumDriver driver = DriverFactory.getDriver();
        Dimension size = driver.manage().window().getSize();
        
        int startX = (int) (size.width * 0.8);
        int startY = size.height / 2;
        int endX = (int) (size.width * 0.2);
        
        swipe(startX, startY, endX, startY);
    }

    /**
     * Swipe right
     */
    public static void swipeRight() {
        AppiumDriver driver = DriverFactory.getDriver();
        Dimension size = driver.manage().window().getSize();
        
        int startX = (int) (size.width * 0.2);
        int startY = size.height / 2;
        int endX = (int) (size.width * 0.8);
        
        swipe(startX, startY, endX, startY);
    }

    /**
     * Long press on element
     */
    public static void longPress(WebElement element) {
        AppiumDriver driver = DriverFactory.getDriver();
        TouchAction<?> touchAction = new TouchAction<>((PerformsTouchActions) driver);
        
        touchAction.longPress(PointOption.point(element.getLocation()))
                   .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2)))
                   .release()
                   .perform();
        
        logger.debug("Long pressed on element");
    }

    /**
     * Tap on coordinates
     */
    public static void tap(int x, int y) {
        AppiumDriver driver = DriverFactory.getDriver();
        TouchAction<?> touchAction = new TouchAction<>((PerformsTouchActions) driver);
        
        touchAction.tap(PointOption.point(x, y)).perform();
        logger.debug("Tapped at ({}, {})", x, y);
    }
}
