package testorbit.pages.base;

import testorbit.core.DriverFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Base Page Object
 * All page classes extend this
 * 
 * PROVIDES:
 * - Common wait methods
 * - Gesture helpers
 * - Element interaction wrappers
 * - Automatic driver injection
 */
public class BasePage {
    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected AppiumDriver driver;
    protected WebDriverWait wait;

    /**
     * Constructor - initializes page elements
     */
    public BasePage() {
        // Get driver from ThreadLocal
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        // Initialize page elements using Appium PageFactory
        PageFactory.initElements(new AppiumFieldDecorator(
            driver, Duration.ofSeconds(30)), this);
        
        logger.debug("Initialized page: {}", this.getClass().getSimpleName());
    }

    /**
     * Wait for element to be visible
     */
    protected void waitForVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to be clickable
     */
    protected void waitForClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Safe click (waits for element)
     */
    protected void click(WebElement element) {
        waitForClickable(element);
        element.click();
        logger.debug("Clicked element");
    }

    /**
     * Safe send keys (waits for element)
     */
    protected void sendKeys(WebElement element, String text) {
        waitForVisible(element);
        element.clear();
        element.sendKeys(text);
        logger.debug("Entered text: {}", text);
    }

    /**
     * Get element text
     */
    protected String getText(WebElement element) {
        waitForVisible(element);
        return element.getText();
    }

    /**
     * Check if element is displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Hide keyboard
     */
    protected void hideKeyboard() {
        try {
            //driver.hideKeyboard();
        } catch (Exception e) {
            // Keyboard already hidden
        }
    }

}
