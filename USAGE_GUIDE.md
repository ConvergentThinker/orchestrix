# 📖 Complete Usage Guide with Real-World Examples

## Table of Contents
1. [Understanding the Framework](#understanding-the-framework)
2. [Writing Your First Test](#writing-your-first-test)
3. [TestNG Tests](#testng-tests)
4. [Cucumber BDD Tests](#cucumber-bdd-tests)
5. [Page Object Model](#page-object-model)
6. [Real-World Use Cases](#real-world-use-cases)
7. [Advanced Features](#advanced-features)
8. [Best Practices](#best-practices)

---

## Understanding the Framework

### How It Works (Simple Explanation)

Imagine you have a **restaurant** with multiple **chefs** (test threads) and multiple **cooking stations** (devices):

1. **DevicePool** = Kitchen Manager
   - Keeps track of available cooking stations
   - Assigns stations to chefs when they need them
   - Gets stations back when chefs are done

2. **DriverFactory** = Tool Manager
   - Gives each chef their own set of tools (driver)
   - Makes sure chefs don't share tools (thread isolation)
   - Collects tools when chefs finish

3. **PortManager** = Table Manager
   - Assigns unique table numbers (ports) to prevent conflicts
   - Makes sure no two chefs use the same table

4. **BaseTest** = Standard Recipe
   - Provides the standard way to start cooking (setup)
   - Provides the standard way to clean up (teardown)

### Key Concepts

#### 1. Parallel Execution
- Multiple tests run at the same time
- Each test gets its own device
- Tests don't interfere with each other

#### 2. Device Tiers
- **Premium**: High-end devices for critical tests
- **Standard**: Regular devices for normal tests
- **Basic**: Lower-end devices for compatibility tests

#### 3. Thread Safety
- Each thread has its own driver
- No sharing of resources
- Automatic cleanup

---

## Writing Your First Test

### Example 1: Simple Login Test

```java
package com.parallel.appium.tests.login;

import com.parallel.appium.tests.base.BaseTest;
import com.parallel.appium.pages.login.LoginPage;
import com.parallel.appium.pages.home.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    
    @Test(description = "TC001: Verify successful login with valid credentials")
    public void testValidLogin() {
        // Step 1: Navigate to login page
        LoginPage loginPage = new LoginPage();
        
        // Step 2: Perform login
        HomePage homePage = loginPage.login("testuser@example.com", "Test@123");
        
        // Step 3: Verify login success
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Home page should be loaded after successful login");
        Assert.assertTrue(homePage.isWelcomeMessageDisplayed(), 
            "Welcome message should be displayed");
    }
}
```

**What happens behind the scenes:**
1. `@BeforeMethod` allocates a device from pool
2. Creates driver for that device
3. Your test runs
4. `@AfterMethod` quits driver and releases device

### Example 2: Test with Multiple Steps

```java
@Test(description = "TC002: Complete user journey from login to checkout")
public void testCompleteUserJourney() {
    // Login
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("user@example.com", "password");
    Assert.assertTrue(homePage.isPageLoaded());
    
    // Browse products
    ProductPage productPage = homePage.navigateToProducts();
    productPage.browseProducts();
    Assert.assertTrue(productPage.hasProducts());
    
    // Add to cart
    productPage.selectFirstProduct();
    productPage.addToCart();
    Assert.assertTrue(productPage.isProductAddedToCart());
    
    // Checkout
    CartPage cartPage = productPage.openCart();
    cartPage.proceedToCheckout();
    Assert.assertTrue(cartPage.isCheckoutSuccessful());
}
```

---

## TestNG Tests

### Basic Test Structure

```java
public class MyTests extends BaseTest {
    
    @Test
    public void testMethod() {
        // Your test code
    }
}
```

### Test with Parameters

```java
@Test(description = "TC003: Login with different user types")
@Parameters({"username", "password"})
public void testLoginWithParameters(String username, String password) {
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login(username, password);
    Assert.assertTrue(homePage.isPageLoaded());
}
```

**In testng.xml:**
```xml
<test name="Login Tests">
    <parameter name="username" value="testuser@example.com"/>
    <parameter name="password" value="Test@123"/>
    <classes>
        <class name="com.parallel.appium.tests.login.LoginTests">
            <methods>
                <include name="testLoginWithParameters"/>
            </methods>
        </class>
    </classes>
</test>
```

### Test with Data Provider

```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return new Object[][] {
        {"user1@example.com", "Password1"},
        {"user2@example.com", "Password2"},
        {"admin@example.com", "Admin123"}
    };
}

@Test(dataProvider = "loginData", description = "TC004: Test login with multiple users")
public void testLoginWithDataProvider(String username, String password) {
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login(username, password);
    Assert.assertTrue(homePage.isPageLoaded());
}
```

### Test with Retry Logic

```java
@Test(retryAnalyzer = RetryAnalyzer.class, 
      description = "TC005: Test with automatic retry on failure")
public void testWithRetry() {
    // This test will retry up to 3 times if it fails
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("user@example.com", "password");
    Assert.assertTrue(homePage.isPageLoaded());
}
```

### Test Groups

```java
@Test(groups = {"smoke", "login"}, description = "TC006: Smoke test for login")
public void testSmokeLogin() {
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("user@example.com", "password");
    Assert.assertTrue(homePage.isPageLoaded());
}

@Test(groups = {"regression", "login"}, description = "TC007: Regression test for login")
public void testRegressionLogin() {
    // More comprehensive test
}
```

**Run specific groups:**
```bash
mvn clean test -Dgroups=smoke
```

---

## Cucumber BDD Tests

### Writing Feature Files

**File: `src/test/resources/features/login.feature`**

```gherkin
@login @smoke
Feature: User Login
  As a user
  I want to login to the application
  So that I can access my account

  Background:
    Given the app is launched

  @premium @TC001
  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I login with username "testuser@example.com" and password "Test@123"
    Then I should be logged in successfully
    And I should see the welcome message

  @standard @TC002
  Scenario: Failed login with invalid credentials
    Given I am on the login page
    When I login with username "invalid@example.com" and password "wrongpassword"
    Then I should see an error message
    And I should remain on the login page

  @regression @TC003
  Scenario Outline: Login with multiple test data sets
    Given I am on the login page
    When I login with username "<username>" and password "<password>"
    Then I should see "<expected_result>"

    Examples:
      | username              | password  | expected_result    |
      | testuser@example.com  | Test@123  | welcome message    |
      | invalid@example.com   | wrongpass | error message      |
      | admin@example.com     | Admin123  | welcome message    |
```

### Writing Step Definitions

**File: `src/test/java/com/parallel/appium/stepdefinitions/LoginStepDefinitions.java`**

```java
package com.parallel.appium.stepdefinitions;

import com.parallel.appium.pages.login.LoginPage;
import com.parallel.appium.pages.home.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginStepDefinitions {
    
    private LoginPage loginPage;
    private HomePage homePage;
    
    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        loginPage = new LoginPage();
        Assert.assertTrue(loginPage.isPageLoaded(), 
            "Login page should be loaded");
    }
    
    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        homePage = loginPage.login(username, password);
    }
    
    @Then("I should be logged in successfully")
    public void iShouldBeLoggedInSuccessfully() {
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Home page should be loaded after login");
    }
    
    @Then("I should see the welcome message")
    public void iShouldSeeTheWelcomeMessage() {
        Assert.assertTrue(homePage.isWelcomeMessageDisplayed(), 
            "Welcome message should be displayed");
    }
    
    @Then("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
    }
    
    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        Assert.assertTrue(loginPage.isPageLoaded(), 
            "Should still be on login page");
    }
}
```

### Running Cucumber Tests

```bash
# Run all Cucumber tests
mvn clean test -Pcucumber

# Run specific feature
mvn clean test -Pcucumber -Dcucumber.filter.tags="@TC001"

# Run specific tag
mvn clean test -Pcucumber -Dcucumber.filter.tags="@smoke"
```

---

## Page Object Model

### Creating Page Objects

**Base Page:**
```java
package com.parallel.appium.pages.base;

import com.parallel.appium.core.DriverFactory;
import com.parallel.appium.utils.WaitHelper;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class BasePage {
    protected AppiumDriver driver;
    protected WaitHelper waitHelper;
    
    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.waitHelper = new WaitHelper(driver);
    }
    
    protected WebElement findElement(By locator) {
        return waitHelper.waitForElement(locator);
    }
    
    protected void click(By locator) {
        findElement(locator).click();
    }
    
    protected void sendKeys(By locator, String text) {
        findElement(locator).sendKeys(text);
    }
    
    protected String getText(By locator) {
        return findElement(locator).getText();
    }
}
```

**Login Page:**
```java
package com.parallel.appium.pages.login;

import com.parallel.appium.pages.base.BasePage;
import com.parallel.appium.pages.home.HomePage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {
    
    // Locators
    private By usernameField = AppiumBy.id("com.app:id/username");
    private By passwordField = AppiumBy.id("com.app:id/password");
    private By loginButton = AppiumBy.id("com.app:id/login_button");
    private By errorMessage = AppiumBy.id("com.app:id/error_message");
    
    public LoginPage() {
        super();
    }
    
    public boolean isPageLoaded() {
        return findElement(usernameField).isDisplayed();
    }
    
    public LoginPage enterUsername(String username) {
        sendKeys(usernameField, username);
        return this;
    }
    
    public LoginPage enterPassword(String password) {
        sendKeys(passwordField, password);
        return this;
    }
    
    public HomePage clickLogin() {
        click(loginButton);
        return new HomePage();
    }
    
    // Fluent interface - method chaining
    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    public boolean isErrorMessageDisplayed() {
        try {
            return findElement(errorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
```

**Home Page:**
```java
package com.parallel.appium.pages.home;

import com.parallel.appium.pages.base.BasePage;
import com.parallel.appium.pages.product.ProductPage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class HomePage extends BasePage {
    
    private By welcomeMessage = AppiumBy.id("com.app:id/welcome_message");
    private By productsButton = AppiumBy.id("com.app:id/products_button");
    
    public boolean isPageLoaded() {
        return findElement(welcomeMessage).isDisplayed();
    }
    
    public boolean isWelcomeMessageDisplayed() {
        return findElement(welcomeMessage).isDisplayed();
    }
    
    public ProductPage navigateToProducts() {
        click(productsButton);
        return new ProductPage();
    }
}
```

---

## Real-World Use Cases

### Use Case 1: E-Commerce App Testing

**Scenario**: Test complete shopping flow across multiple devices

```java
public class ShoppingFlowTests extends BaseTest {
    
    @Test(description = "Complete shopping journey")
    public void testCompleteShoppingJourney() {
        // Login
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("customer@example.com", "password");
        
        // Browse and search
        ProductPage productPage = homePage.navigateToProducts();
        productPage.searchProduct("laptop");
        productPage.filterByPrice("500-1000");
        
        // Select product
        ProductDetailPage detailPage = productPage.selectProduct("MacBook Pro");
        Assert.assertTrue(detailPage.isProductDetailsDisplayed());
        
        // Add to cart
        detailPage.addToCart();
        CartPage cartPage = detailPage.viewCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 1);
        
        // Checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.enterShippingAddress("123 Main St", "City", "12345");
        checkoutPage.selectPaymentMethod("Credit Card");
        checkoutPage.enterCardDetails("1234567890", "12/25", "123");
        OrderConfirmationPage confirmation = checkoutPage.placeOrder();
        
        // Verify order
        Assert.assertTrue(confirmation.isOrderConfirmed());
        String orderId = confirmation.getOrderId();
        Assert.assertNotNull(orderId);
    }
}
```

### Use Case 2: Banking App - Multi-Step Transaction

```java
public class BankingTransactionTests extends BaseTest {
    
    @Test(description = "Transfer money between accounts")
    public void testMoneyTransfer() {
        // Login with biometric
        LoginPage loginPage = new LoginPage();
        loginPage.authenticateWithBiometric();
        DashboardPage dashboard = new DashboardPage();
        
        // Navigate to transfer
        TransferPage transferPage = dashboard.navigateToTransfer();
        
        // Select accounts
        transferPage.selectFromAccount("Savings Account");
        transferPage.selectToAccount("Checking Account");
        transferPage.enterAmount("500.00");
        transferPage.enterMemo("Monthly transfer");
        
        // Review and confirm
        ReviewPage reviewPage = transferPage.reviewTransfer();
        Assert.assertEquals(reviewPage.getAmount(), "$500.00");
        ConfirmationPage confirmation = reviewPage.confirmTransfer();
        
        // Verify transaction
        Assert.assertTrue(confirmation.isTransactionSuccessful());
        String transactionId = confirmation.getTransactionId();
        
        // Verify in transaction history
        TransactionHistoryPage history = dashboard.viewTransactionHistory();
        Assert.assertTrue(history.hasTransaction(transactionId));
    }
}
```

### Use Case 3: Social Media App - Content Creation

```java
public class ContentCreationTests extends BaseTest {
    
    @Test(description = "Create and publish a post")
    public void testCreatePost() {
        // Login
        LoginPage loginPage = new LoginPage();
        FeedPage feedPage = loginPage.login("user@social.com", "password");
        
        // Create post
        CreatePostPage createPost = feedPage.tapCreatePost();
        createPost.enterText("Hello World! This is my first post.");
        createPost.addPhoto("/path/to/photo.jpg");
        createPost.addLocation("New York, NY");
        createPost.addHashtags("#hello #world #firstpost");
        
        // Preview
        PreviewPage preview = createPost.preview();
        Assert.assertTrue(preview.isPostPreviewCorrect());
        
        // Publish
        FeedPage updatedFeed = preview.publish();
        
        // Verify post appears in feed
        Assert.assertTrue(updatedFeed.hasPost("Hello World!"));
        Assert.assertTrue(updatedFeed.hasPostWithImage());
    }
}
```

### Use Case 4: Healthcare App - Appointment Booking

```java
public class AppointmentBookingTests extends BaseTest {
    
    @Test(description = "Book a doctor appointment")
    public void testBookAppointment() {
        // Login
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("patient@health.com", "password");
        
        // Find doctor
        DoctorSearchPage searchPage = homePage.findDoctor();
        searchPage.searchBySpecialty("Cardiology");
        searchPage.filterByLocation("Within 5 miles");
        searchPage.filterByAvailability("This week");
        
        // Select doctor
        DoctorProfilePage profile = searchPage.selectDoctor("Dr. Smith");
        Assert.assertTrue(profile.isDoctorProfileDisplayed());
        
        // Book appointment
        AppointmentBookingPage booking = profile.bookAppointment();
        booking.selectDate("2026-02-15");
        booking.selectTime("10:00 AM");
        booking.enterReason("Annual checkup");
        booking.addInsurance("Blue Cross Blue Shield");
        
        // Confirm
        ConfirmationPage confirmation = booking.confirmAppointment();
        Assert.assertTrue(confirmation.isAppointmentConfirmed());
        String appointmentId = confirmation.getAppointmentId();
        
        // Verify in appointments list
        AppointmentsPage appointments = homePage.viewAppointments();
        Assert.assertTrue(appointments.hasAppointment(appointmentId));
    }
}
```

### Use Case 5: Parallel Testing Across Device Tiers

**Scenario**: Run same test on different device tiers simultaneously

```java
public class CrossDeviceCompatibilityTests extends BaseTest {
    
    @Test(description = "Test login on premium device", 
          groups = "premium")
    public void testLoginPremiumDevice() {
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("user@example.com", "password");
        Assert.assertTrue(homePage.isPageLoaded());
    }
    
    @Test(description = "Test login on standard device", 
          groups = "standard")
    public void testLoginStandardDevice() {
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("user@example.com", "password");
        Assert.assertTrue(homePage.isPageLoaded());
    }
    
    @Test(description = "Test login on basic device", 
          groups = "basic")
    public void testLoginBasicDevice() {
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("user@example.com", "password");
        Assert.assertTrue(homePage.isPageLoaded());
    }
}
```

**Run all tiers in parallel:**
```bash
mvn clean test -Ddevice.tier=all
```

---

## Advanced Features

### 1. Using Gestures

```java
import com.parallel.appium.utils.GestureHelper;

public class GestureTests extends BaseTest {
    
    @Test
    public void testSwipeGestures() {
        GestureHelper gestures = new GestureHelper(DriverFactory.getDriver());
        
        // Swipe down
        gestures.swipeDown();
        
        // Swipe up
        gestures.swipeUp();
        
        // Swipe left
        gestures.swipeLeft();
        
        // Swipe right
        gestures.swipeRight();
        
        // Long press
        gestures.longPress(element);
        
        // Pinch and zoom
        gestures.pinchZoom();
    }
}
```

### 2. Taking Screenshots

```java
import com.parallel.appium.utils.ScreenshotUtils;

public class ScreenshotTests extends BaseTest {
    
    @Test
    public void testWithScreenshot() {
        try {
            LoginPage loginPage = new LoginPage();
            loginPage.login("user@example.com", "password");
        } catch (Exception e) {
            // Screenshot automatically captured on failure
            String screenshotPath = ScreenshotUtils.captureScreenshot("testWithScreenshot");
            throw e;
        }
    }
}
```

### 3. Custom Waits

```java
import com.parallel.appium.utils.WaitHelper;

public class WaitTests extends BaseTest {
    
    @Test
    public void testWithCustomWait() {
        WaitHelper waitHelper = new WaitHelper(DriverFactory.getDriver());
        
        // Wait for element with custom timeout
        WebElement element = waitHelper.waitForElement(
            By.id("element_id"), 
            Duration.ofSeconds(30)
        );
        
        // Wait for element to be clickable
        WebElement clickableElement = waitHelper.waitForElementToBeClickable(
            By.id("button_id")
        );
    }
}
```

### 4. Cloud Device Testing

```java
// Configure cloud device in devices.json
{
  "deviceName": "Samsung Galaxy S21",
  "cloudDeviceName": "Galaxy S21",
  "platformName": "Android",
  "platformVersion": "12",
  "tier": "premium",
  "executionType": "lambdatest",
  "cloudProvider": "lambdatest",
  "tunnelId": "your-tunnel-id",
  "cloudAppUrl": "lt://your-app-id"
}

// Use same way as local devices
@Test
public void testOnCloudDevice() {
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("user@example.com", "password");
    Assert.assertTrue(homePage.isPageLoaded());
}
```

---

## Best Practices

### 1. Page Object Model
- ✅ Keep page objects focused on one page
- ✅ Use fluent interfaces for method chaining
- ✅ Store locators as constants
- ✅ Return next page object from actions

### 2. Test Organization
- ✅ One test class per feature
- ✅ Descriptive test names
- ✅ Use test descriptions
- ✅ Group related tests

### 3. Assertions
- ✅ Use meaningful assertion messages
- ✅ Assert one thing at a time
- ✅ Use appropriate assertion types

### 4. Data Management
- ✅ Use data providers for multiple test data
- ✅ Keep test data separate from test code
- ✅ Use environment variables for sensitive data

### 5. Error Handling
- ✅ Handle expected exceptions
- ✅ Use try-catch for cleanup
- ✅ Log errors with context

### 6. Performance
- ✅ Don't use hardcoded waits
- ✅ Use explicit waits
- ✅ Minimize test execution time

---

## Summary

This framework provides:
- ✅ Easy-to-use API for writing tests
- ✅ Support for both TestNG and Cucumber
- ✅ Page Object Model for maintainable code
- ✅ Real-world examples for common scenarios
- ✅ Advanced features for complex testing needs

**Next Steps:**
1. Write your first test using the examples
2. Customize for your application
3. Run tests in parallel
4. Review reports and improve tests

**Happy Testing!** 🚀
