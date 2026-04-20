@smoke @login
Feature: Login
  As a mobile app user
  I want to log in successfully
  So that I can access my account

  Scenario: Valid user can login
    Given the login screen is displayed
    When I enter valid credentials
    Then I should see the home screen
