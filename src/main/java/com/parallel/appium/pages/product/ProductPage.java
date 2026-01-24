package com.parallel.appium.pages.product;

import com.parallel.appium.pages.base.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Product Page Object
 */
public class ProductPage extends BasePage {

    @AndroidFindBy(accessibility = "product-list")
    @iOSXCUITFindBy(accessibility = "product-list")
    private List<WebElement> productList;

    @AndroidFindBy(accessibility = "product-title")
    @iOSXCUITFindBy(accessibility = "product-title")
    private WebElement productTitle;

    @AndroidFindBy(accessibility = "add-to-cart-button")
    @iOSXCUITFindBy(accessibility = "add-to-cart-button")
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = "cart-icon")
    @iOSXCUITFindBy(accessibility = "cart-icon")
    private WebElement cartIcon;

    /**
     * Get product count
     */
    public int getProductCount() {
        return productList.size();
    }

    /**
     * Click on first product
     */
    public ProductPage clickFirstProduct() {
        if (!productList.isEmpty()) {
            click(productList.get(0));
        }
        return this;
    }

    /**
     * Add product to cart
     */
    public ProductPage addToCart() {
        click(addToCartButton);
        return this;
    }

    /**
     * Get product title
     */
    public String getProductTitle() {
        return getText(productTitle);
    }

    /**
     * Open cart
     */
    public void openCart() {
        click(cartIcon);
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(productTitle);
    }
}
