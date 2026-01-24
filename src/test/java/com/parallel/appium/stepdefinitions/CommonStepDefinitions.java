package com.parallel.appium.stepdefinitions;

import com.parallel.appium.core.DriverFactory;
import com.parallel.appium.utils.GestureHelper;
import com.parallel.appium.utils.WaitHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Step Definitions
 * Shared steps used across multiple scenarios
 */
public class CommonStepDefinitions {
    private static final Logger logger = LoggerFactory.getLogger(CommonStepDefinitions.class);

    @Given("the app is launched")
    public void the_app_is_launched() {
        logger.info("Verifying app is launched");
        // Driver should already be initialized by hooks
        // Just verify driver is available
        DriverFactory.getDriver();
        logger.info("✓ App is launched");
    }

    @When("I wait for {int} seconds")
    public void i_wait_for_seconds(Integer seconds) {
        logger.info("Waiting for {} seconds", seconds);
        WaitHelper.wait(seconds);
    }

    @When("I swipe down")
    public void i_swipe_down() {
        logger.info("Swiping down");
        GestureHelper.swipeDown();
    }

    @When("I swipe up")
    public void i_swipe_up() {
        logger.info("Swiping up");
        GestureHelper.swipeUp();
    }

    @When("I swipe left")
    public void i_swipe_left() {
        logger.info("Swiping left");
        GestureHelper.swipeLeft();
    }

    @When("I swipe right")
    public void i_swipe_right() {
        logger.info("Swiping right");
        GestureHelper.swipeRight();
    }
}
