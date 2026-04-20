package testorbit.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    @Given("the login screen is displayed")
    public void loginScreenDisplayed() {
        // TODO: implement app-specific screen validation.
    }

    @When("I enter valid credentials")
    public void enterValidCredentials() {
        // TODO: implement app-specific credential entry.
    }

    @Then("I should see the home screen")
    public void verifyHomeScreen() {
        // TODO: implement app-specific post-login assertion.
    }
}
