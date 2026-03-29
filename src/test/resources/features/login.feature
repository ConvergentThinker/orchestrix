@login @smoke
Feature: User Login
  As a user
  I want to login to the application
  So that I can access my account

  Background:
    Given the app is launched

  @premium @TC_Kiki
  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I login with username "testuser@example.com" and password "Test@123"
    Then I should be logged in successfully
    And I should see the welcome message

  @premium @TC001
  Scenario: Successful login with valid credentials (parallel device 2)
    Given I am on the login page
    When I login with username "testuser@example.com" and password "Test@123"
    Then I should be logged in successfully
    And I should see the welcome message

  @standard @TC002
  Scenario: Successful login using step-by-step approach
    Given I am on the login page
    When I enter username "testuser@example.com" and password "Test@123"
    And I click the login button
    Then I should be logged in successfully
    And I should see the welcome message

  @regression @TC003
  Scenario: Failed login with invalid credentials
    Given I am on the login page with invalid credentials
    When I enter username "invalid@example.com" and password "wrongpassword"
    And I click the login button
    Then I should see an error message
    And I should remain on the login page

  @regression @TC004
  Scenario: Failed login with specific error message
    Given I am on the login page
    When I enter username "invalid@example.com" and password "wrongpassword"
    And I click the login button
    Then I should see error message "Invalid credentials"
    And I should remain on the login page

  @regression @TC005
  Scenario Outline: Login with multiple test data sets
    Given I am on the login page
    When I login with username "<username>" and password "<password>"
    Then I should see "<expected_result>"

    Examples:
      | username              | password  | expected_result    |
      | testuser@example.com  | Test@123  | welcome message    |
      | invalid@example.com  | wrongpass | error message      |
      | admin@example.com    | Admin123  | welcome message    |
