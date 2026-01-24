@e2e @regression
Feature: End-to-End User Journey
  As a user
  I want to complete a full shopping journey
  So that I can purchase products successfully

  Background:
    Given the app is launched

  @premium @TC011
  Scenario: Complete user journey from login to product purchase
    # Login
    Given I am on the login page
    When I login with username "testuser@example.com" and password "Test@123"
    Then I should be logged in successfully
    
    # Browse Products
    Given I am on the product page
    When I browse the products
    Then I should see at least 1 product
    
    # Select and Add Product
    When I select the first product
    And I add the product to cart
    Then I should see the product title
    
    # Verify Cart
    When I open the cart
    Then I should see products in the cart

  @standard @TC012
  Scenario: User journey with gestures
    Given I am on the login page
    When I login with username "testuser@example.com" and password "Test@123"
    Then I should be logged in successfully
    
    Given I am on the product page
    When I swipe down
    And I wait for 2 seconds
    When I swipe up
    And I browse the products
    Then I should see at least 1 product

  @regression @TC013
  Scenario Outline: Complete journey with different user types
    Given I am on the login page
    When I login with username "<username>" and password "<password>"
    Then I should be logged in successfully
    Given I am on the product page
    When I browse the products
    Then I should see at least <min_products> product

    Examples:
      | username              | password  | min_products |
      | testuser@example.com  | Test@123  | 1            |
      | admin@example.com     | Admin123  | 5            |
      | guest@example.com     | Guest123  | 3            |
