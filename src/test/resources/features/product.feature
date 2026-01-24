@product @smoke
Feature: Product Browsing and Cart Management
  As a user
  I want to browse products and add them to cart
  So that I can purchase items

  Background:
    Given the app is launched

  @standard @TC001
  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I login with username "testuser@example.com" and password "Test@123"
    Then I should be logged in successfully
    And I should see the welcome message


  @premium @TC006
  Scenario: Browse products and verify product list
    Given I am on the product page
    When I browse the products
    Then I should see at least 1 product

  @standard @TC007
  Scenario: View product details
    Given I am on the product page
    When I select the first product
    Then I should see the product title

  @regression @TC008
  Scenario: Add product to cart
    Given I am on the product page
    When I select the first product
    And I add the product to cart
    Then I should see the product title
    And the product title should contain "Product"

  @regression @TC009
  Scenario: Browse multiple products
    Given I am on the product page
    When I browse the products
    Then I should see at least 5 products

  @regression @TC010
  Scenario: Navigate to cart
    Given I am on the product page
    When I select the first product
    And I add the product to cart
    And I open the cart
    Then I should see products in the cart
