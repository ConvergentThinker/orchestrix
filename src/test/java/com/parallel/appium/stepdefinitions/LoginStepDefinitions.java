package com.parallel.appium.stepdefinitions;

import com.parallel.appium.pages.home.HomePage;
import com.parallel.appium.pages.login.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Login Step Definitions
 * Implements Cucumber steps for login scenarios
 */
public class LoginStepDefinitions {
    private static final Logger logger = LoggerFactory.getLogger(LoginStepDefinitions.class);
    
    private LoginPage loginPage;
    private HomePage homePage;

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        logger.info("Navigating to login page");
        loginPage = new LoginPage();
        //Assert.assertTrue(loginPage.isPageLoaded(), 
           // "Login page should be loaded");
    }

    @Given("I am on the login page with invalid credentials")
    public void i_am_on_the_login_page_with_invalid_credentials() {
        logger.info("Navigating to login page for invalid credentials test");
        loginPage = new LoginPage();
       // Assert.assertTrue(loginPage.isPageLoaded(), 
         //   "Login page should be loaded");
    }

    @When("I enter username {string} and password {string}")
    public void i_enter_username_and_password(String username, String password) {
        logger.info("Entering credentials - Username: {}, Password: {}", 
            username, password.replaceAll(".", "*"));
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @When("I click the login button")
    public void i_click_the_login_button() {
        logger.info("Clicking login button");
        homePage = loginPage.clickLogin();
    }

    @When("I login with username {string} and password {string}")
    public void i_login_with_username_and_password(String username, String password) {
        logger.info("Performing complete login - Username: {}", username);
        homePage = loginPage.login(username, password);
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        logger.info("Verifying successful login");
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Home page should be displayed after successful login");
        logger.info("✓ Login successful");
    }

    @Then("I should see the welcome message")
    public void i_should_see_the_welcome_message() {
        logger.info("Verifying welcome message");
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Welcome message should be displayed");
        String welcomeMessage = homePage.getWelcomeMessage();
        Assert.assertNotNull(welcomeMessage, "Welcome message should not be null");
        Assert.assertFalse(welcomeMessage.isEmpty(), "Welcome message should not be empty");
        logger.info("✓ Welcome message displayed: {}", welcomeMessage);
    }

    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        logger.info("Verifying error message");
        Assert.assertTrue(loginPage.isErrorDisplayed(), 
            "Error message should be displayed for invalid credentials");
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertNotNull(errorMessage, "Error message should not be null");
        logger.info("✓ Error message displayed: {}", errorMessage);
    }

    @Then("I should see error message {string}")
    public void i_should_see_error_message(String expectedErrorMessage) {
        logger.info("Verifying specific error message: {}", expectedErrorMessage);
        Assert.assertTrue(loginPage.isErrorDisplayed(), 
            "Error message should be displayed");
        String actualErrorMessage = loginPage.getErrorMessage();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, 
            "Error message should match expected");
        logger.info("✓ Error message matches: {}", actualErrorMessage);
    }

    @Then("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        logger.info("Verifying still on login page");
        Assert.assertTrue(loginPage.isPageLoaded(), 
            "Should remain on login page after failed login");
    }
}
