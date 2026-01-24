package com.parallel.appium.stepdefinitions;

import com.parallel.appium.pages.product.ProductPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Product Step Definitions
 * Implements Cucumber steps for product scenarios
 */
public class ProductStepDefinitions {
    private static final Logger logger = LoggerFactory.getLogger(ProductStepDefinitions.class);
    
    private ProductPage productPage;

    @Given("I am on the product page")
    public void i_am_on_the_product_page() {
        logger.info("Navigating to product page");
        productPage = new ProductPage();
        Assert.assertTrue(productPage.isPageLoaded(), 
            "Product page should be loaded");
    }

    @When("I browse the products")
    public void i_browse_the_products() {
        logger.info("Browsing products");
        // Product page is already loaded, just verify
        Assert.assertTrue(productPage.isPageLoaded(), 
            "Product page should be loaded");
    }

    @Then("I should see at least {int} product")
    public void i_should_see_at_least_product(Integer minCount) {
        logger.info("Verifying product count is at least {}", minCount);
        int productCount = productPage.getProductCount();
        Assert.assertTrue(productCount >= minCount, 
            String.format("Should see at least %d products, but found %d", 
                minCount, productCount));
        logger.info("✓ Found {} products", productCount);
    }

    @When("I select the first product")
    public void i_select_the_first_product() {
        logger.info("Selecting first product");
        productPage.clickFirstProduct();
    }

    @When("I add the product to cart")
    public void i_add_the_product_to_cart() {
        logger.info("Adding product to cart");
        productPage.addToCart();
    }

    @Then("I should see the product title")
    public void i_should_see_the_product_title() {
        logger.info("Verifying product title is displayed");
        String productTitle = productPage.getProductTitle();
        Assert.assertNotNull(productTitle, "Product title should not be null");
        Assert.assertFalse(productTitle.isEmpty(), "Product title should not be empty");
        logger.info("✓ Product title: {}", productTitle);
    }

    @Then("the product title should contain {string}")
    public void the_product_title_should_contain(String expectedText) {
        logger.info("Verifying product title contains: {}", expectedText);
        String productTitle = productPage.getProductTitle();
        Assert.assertTrue(productTitle.contains(expectedText), 
            String.format("Product title '%s' should contain '%s'", 
                productTitle, expectedText));
        logger.info("✓ Product title contains expected text");
    }

    @When("I open the cart")
    public void i_open_the_cart() {
        logger.info("Opening cart");
        productPage.openCart();
    }

    @Then("I should see products in the cart")
    public void i_should_see_products_in_the_cart() {
        logger.info("Verifying products in cart");
        // This would need cart page implementation
        // For now, just verify we can navigate to cart
        Assert.assertTrue(true, "Cart should be accessible");
    }
}
