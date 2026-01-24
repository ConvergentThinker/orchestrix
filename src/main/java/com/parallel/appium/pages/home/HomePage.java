package com.parallel.appium.pages.home;

import com.parallel.appium.pages.base.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Home Page Object
 */
public class HomePage extends BasePage {

    @AndroidFindBy(accessibility = "welcome-message")
    @iOSXCUITFindBy(accessibility = "welcome-message")
    private WebElement welcomeMessage;

    @AndroidFindBy(accessibility = "home-title")
    @iOSXCUITFindBy(accessibility = "home-title")
    private WebElement homeTitle;

    /**
     * Get welcome message text
     */
    public String getWelcomeMessage() {
        return getText(welcomeMessage);
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(homeTitle) && isDisplayed(welcomeMessage);
    }
}
