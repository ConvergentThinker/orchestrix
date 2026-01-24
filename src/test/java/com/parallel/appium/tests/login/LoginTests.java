package com.parallel.appium.tests.login;

import com.parallel.appium.pages.login.LoginPage;
import com.parallel.appium.pages.home.HomePage;
import com.parallel.appium.retry.RetryAnalyzer;
import com.parallel.appium.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Login Test Suite
 * Demonstrates parallel execution with proper isolation
 */
public class LoginTests extends BaseTest {

    @Test(priority = 1, description = "TC001: Valid Login", retryAnalyzer = RetryAnalyzer.class)
    public void testValidLogin() {
        logger.info("Starting TC001 on device: {}", getDeviceConfig().getDeviceName());
        
        // Using Page Object Model
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("testuser@example.com", "Test@123");
        
        // Verify login success
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Home page should be displayed after successful login");
        
        logger.info("✓ TC001 passed on device: {}", getDeviceConfig().getDeviceName());
    }

    @Test(priority = 2, description = "TC002: Invalid Login", retryAnalyzer = RetryAnalyzer.class)
    public void testInvalidLogin() {
        logger.info("Starting TC002 on device: {}", getDeviceConfig().getDeviceName());
        
        LoginPage loginPage = new LoginPage();
        loginPage.enterUsername("invalid@example.com")
                 .enterPassword("wrongpassword")
                 .clickLogin();
        
        // Verify error message
        Assert.assertTrue(loginPage.isErrorDisplayed(), 
            "Error message should be displayed for invalid credentials");
        
        logger.info("✓ TC002 passed on device: {}", getDeviceConfig().getDeviceName());
    }
}
