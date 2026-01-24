package com.parallel.appium.tests.product;

import com.parallel.appium.pages.product.ProductPage;
import com.parallel.appium.retry.RetryAnalyzer;
import com.parallel.appium.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Product Test Suite
 * Demonstrates product browsing and cart functionality
 */
public class ProductTests extends BaseTest {

    @Test(priority = 1, description = "TC003: Browse Products", retryAnalyzer = RetryAnalyzer.class)
    public void testBrowseProducts() {
        logger.info("Starting TC003 on device: {}", getDeviceConfig().getDeviceName());
        
        ProductPage productPage = new ProductPage();
        
        // Verify page loaded
        Assert.assertTrue(productPage.isPageLoaded(), 
            "Product page should be loaded");
        
        // Verify products are displayed
        int productCount = productPage.getProductCount();
        Assert.assertTrue(productCount > 0, 
            "At least one product should be displayed");
        
        logger.info("✓ TC003 passed on device: {} - Found {} products", 
            getDeviceConfig().getDeviceName(), productCount);
    }

    @Test(priority = 2, description = "TC004: Add Product to Cart", retryAnalyzer = RetryAnalyzer.class)
    public void testAddProductToCart() {
        logger.info("Starting TC004 on device: {}", getDeviceConfig().getDeviceName());
        
        ProductPage productPage = new ProductPage();
        
        // Click first product
        productPage.clickFirstProduct();
        
        // Add to cart
        productPage.addToCart();
        
        // Verify product title is displayed
        String title = productPage.getProductTitle();
        Assert.assertNotNull(title, "Product title should be displayed");
        Assert.assertFalse(title.isEmpty(), "Product title should not be empty");
        
        logger.info("✓ TC004 passed on device: {} - Added product: {}", 
            getDeviceConfig().getDeviceName(), title);
    }
}
