# Cucumber Feature Files

This directory contains Gherkin feature files for BDD (Behavior-Driven Development) testing.

## Feature Files

### login.feature
Contains login-related scenarios:
- Successful login with valid credentials
- Failed login with invalid credentials
- Login with multiple test data sets (Scenario Outline)

**Tags**: `@login`, `@smoke`, `@regression`

### product.feature
Contains product browsing and cart management scenarios:
- Browse products
- View product details
- Add products to cart
- Navigate to cart

**Tags**: `@product`, `@smoke`, `@regression`

### e2e.feature
Contains end-to-end user journey scenarios:
- Complete shopping journey
- User journey with gestures
- Journey with different user types

**Tags**: `@e2e`, `@regression`

## Running Feature Files

### Run all features
```bash
mvn clean test -DsuiteXmlFile=testng-cucumber.xml
```

### Run by tags
```bash
# Run only smoke tests
mvn clean test -DsuiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@smoke"

# Run only regression tests
mvn clean test -DsuiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@regression"

# Run specific feature
mvn clean test -DsuiteXmlFile=testng-cucumber.xml -Dcucumber.features="src/test/resources/features/login.feature"
```

### Run with device tier
```bash
# Premium devices
mvn clean test -DsuiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@premium"

# Standard devices
mvn clean test -DsuiteXmlFile=testng-cucumber.xml -Dcucumber.filter.tags="@standard"
```

## Tag Conventions

- `@smoke` - Quick smoke tests
- `@regression` - Full regression suite
- `@login` - Login-related tests
- `@product` - Product-related tests
- `@e2e` - End-to-end tests
- `@premium` - Run on premium devices
- `@standard` - Run on standard devices
- `@basic` - Run on basic devices
- `@TC001`, `@TC002`, etc. - Test case IDs

## Writing New Features

1. Create a new `.feature` file in this directory
2. Write scenarios using Gherkin syntax
3. Add appropriate tags
4. Implement step definitions in `com.parallel.appium.stepdefinitions` package
5. Run and verify

## Example Feature Structure

```gherkin
@feature-name @smoke
Feature: Feature Description
  As a user
  I want to perform some action
  So that I can achieve some goal

  Background:
    Given the app is launched

  @TC001
  Scenario: Scenario description
    Given some precondition
    When I perform some action
    Then I should see some result
```
