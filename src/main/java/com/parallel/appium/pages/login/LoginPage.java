package com.parallel.appium.pages.login;

import com.parallel.appium.pages.base.BasePage;
import com.parallel.appium.pages.home.HomePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Login Page Object
 * 
 * ENCAPSULATES:
 * - Login screen elements
 * - Login actions
 * - Validation methods
 */
public class LoginPage extends BasePage {

    // Cross-platform element locators
    @AndroidFindBy(accessibility = "username-field")
    @iOSXCUITFindBy(accessibility = "username-field")
    private WebElement usernameField;

    @AndroidFindBy(accessibility = "password-field")
    @iOSXCUITFindBy(accessibility = "password-field")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "login-button")
    @iOSXCUITFindBy(accessibility = "login-button")
    private WebElement loginButton;

    @AndroidFindBy(accessibility = "error-message")
    @iOSXCUITFindBy(accessibility = "error-message")
    private WebElement errorMessage;

    @AndroidFindBy(id = "com.app:id/login_title")
    @iOSXCUITFindBy(id = "loginTitle")
    private WebElement loginTitle;

    /**
     * Enter username
     */
    public LoginPage enterUsername(String username) {
        sendKeys(usernameField, username);
        return this;
    }

    /**
     * Enter password
     */
    public LoginPage enterPassword(String password) {
        sendKeys(passwordField, password);
        return this;
    }

    /**
     * Click login and navigate to home
     */
    public HomePage clickLogin() {
        click(loginButton);
        return new HomePage();
    }

    /**
     * Complete login (fluent API)
     */
    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    /**
     * Get error message
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Check if error is displayed
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(loginTitle) && isDisplayed(loginButton);
    }
}
