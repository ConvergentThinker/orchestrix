# 🚀 Orchestrix - Enterprise Parallel Appium Framework

<div align="center">

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Appium](https://img.shields.io/badge/Appium-2.x-green.svg)](https://appium.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Contributions Welcome](https://img.shields.io/badge/Contributions-Welcome-brightgreen.svg)](CONTRIBUTING.md)

**An FPGA-inspired enterprise-grade parallel mobile test automation framework**

[Website](https://your-website.com) • [Documentation](DOCUMENTATION_INDEX.md) • [Examples](REAL_WORLD_USE_CASES.md) • [Report Bug](https://github.com/your-username/orchestrix/issues) • [Request Feature](https://github.com/your-username/orchestrix/issues)

</div>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Why Orchestrix?](#-why-orchestrix)
- [Key Features](#-key-features)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Examples](#-examples)
- [Documentation](#-documentation)
- [Contributing](#-contributing)
- [Support](#-support)
- [License](#-license)

---

## 🎯 Overview

**Orchestrix** is a production-ready, enterprise-grade parallel mobile test automation framework built on Appium. It solves the most common pain points in parallel mobile testing by providing automatic port management, device pooling, thread-safe execution, and beautiful reporting.

### What Makes Orchestrix Special?

- 🏗️ **FPGA-Inspired Architecture**: Applies hardware parallelism concepts to software testing
- ⚡ **3-10x Faster Execution**: True parallel test execution across multiple devices
- 🔒 **100% Thread-Safe**: Production-ready concurrent execution with zero conflicts
- 📊 **Beautiful Reports**: Feature-grouped HTML reports with device-wise analytics
- 🎯 **Zero Configuration**: Automatic port allocation and device management
- ☁️ **Cloud Ready**: Built-in support for LambdaTest, BrowserStack, and more

---

## 💡 Why Orchestrix?

### The Problem

Traditional Appium parallel testing faces several challenges:
- ❌ Port conflicts when running multiple tests
- ❌ Manual device management and allocation
- ❌ Thread safety issues causing test failures
- ❌ Complex setup and configuration
- ❌ Limited reporting capabilities

### The Solution

Orchestrix provides:
- ✅ Automatic port allocation (no conflicts)
- ✅ Intelligent device pooling with tier-based allocation
- ✅ Thread-safe execution using proven design patterns
- ✅ Simple JSON-based configuration
- ✅ Comprehensive reporting with feature grouping

---

## ✨ Key Features

### Core Capabilities

| Feature | Description |
|---------|-------------|
| **🚀 Parallel Execution** | Run tests simultaneously across multiple devices with automatic resource management |
| **🔌 Zero Port Conflicts** | Automatic port allocation prevents conflicts in parallel execution |
| **🔒 Perfect Isolation** | ThreadLocal pattern ensures complete test isolation |
| **🔄 Smart Retry Logic** | Auto-retry transient failures with exponential backoff |
| **💾 Result Caching** | Skip unchanged tests to save 30-70% execution time |
| **📊 Device-Wise Reports** | Beautiful HTML reports with feature grouping and device breakdown |
| **📱 Page Object Model** | Maintainable, reusable test code structure |
| **🔗 CI/CD Ready** | Jenkins & GitHub Actions integration included |
| **☁️ Cloud Support** | LambdaTest, BrowserStack, and custom cloud providers |

### Advanced Features

- **Tier-Based Device Allocation**: Premium, Standard, Basic device tiers with automatic fallback
- **Feature Grouping**: Organize tests by feature files in reports
- **Screenshot Management**: Automatic screenshots on failure, stored with reports
- **BDD Support**: Full Cucumber integration for behavior-driven testing
- **Extensible Architecture**: Easy to add custom cloud providers and extensions

---

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Node.js 18+ and npm
- Appium 2.x
- Android SDK (for Android testing)
- Xcode (for iOS testing - macOS only)

### 5-Minute Setup

```bash
# 1. Clone the repository
git clone https://github.com/your-username/orchestrix.git
cd orchestrix

# 2. Build the project
mvn clean install

# 3. Configure devices
cp config/devices.json.example config/devices.json
# Edit config/devices.json with your device configurations

# 4. Start Appium servers
./scripts/start-appium-nodes.sh

# 5. Run your first test
mvn clean test

# 6. View reports
open reports/extent/TestReport-*/Report.html
```

**That's it!** You're ready to start testing. 🎉

---

## 📦 Installation

### Step 1: Install Prerequisites

#### Java 17+

**macOS (Homebrew):**
```bash
brew install openjdk@17
```

**Windows:**
Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

#### Maven 3.6+

**macOS (Homebrew):**
```bash
brew install maven
```

**Windows/Linux:**
Download from [Maven Download](https://maven.apache.org/download.cgi)

#### Appium 2.x

```bash
npm install -g appium@latest
appium driver install uiautomator2  # For Android
appium driver install xcuitest       # For iOS (macOS only)
```

### Step 2: Clone and Build

```bash
git clone https://github.com/your-username/orchestrix.git
cd orchestrix
mvn clean install
```

### Step 3: Configure Devices

Edit `config/devices.json`:

```json
[
  {
    "deviceName": "Pixel_7_Emulator",
    "udid": "emulator-5554",
    "platformName": "Android",
    "platformVersion": "13",
    "tier": "premium",
    "appiumPort": 4723,
    "systemPort": 8200,
    "chromedriverPort": 9515,
    "app": "/path/to/your/app.apk"
  }
]
```

**For detailed configuration, see [Device Configuration Guide](config/README.md)**

### Step 4: Verify Installation

```bash
# Check Java
java -version

# Check Maven
mvn -version

# Check Appium
appium --version

# Verify project builds
mvn clean compile
```

---

## 🎓 Getting Started

### Your First Test

Create a test class:

```java
package com.parallel.appium.tests.login;

import com.parallel.appium.tests.base.BaseTest;
import com.parallel.appium.pages.login.LoginPage;
import com.parallel.appium.pages.home.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    
    @Test(description = "TC001: Verify successful login")
    public void testValidLogin() {
        // Navigate to login page
        LoginPage loginPage = new LoginPage();
        
        // Perform login
        HomePage homePage = loginPage.login("user@example.com", "password123");
        
        // Verify success
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Home page should be loaded after login");
    }
}
```

### Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginTests

# Run with custom thread count
mvn clean test -Dparallel.threads=4

# Run specific device tier
mvn clean test -Ddevice.tier=premium

# Run Cucumber BDD tests
mvn clean test -Pcucumber
```

### Viewing Reports

After test execution, reports are generated in:

```
reports/
  ├── consolidated/
  │   └── Device_summary<timestamp>.html
  └── extent/
      └── TestReport-<timestamp>/
          ├── Report.html
          └── screenshots/
```

**Open reports:**

```bash
# macOS
open reports/extent/TestReport-*/Report.html

# Windows
start reports\extent\TestReport-*\Report.html

# Linux
xdg-open reports/extent/TestReport-*/Report.html
```

---

## 📁 Project Structure

```
orchestrix/
├── config/                          # Configuration files
│   ├── devices.json                 # Device configurations
│   ├── devices.json.example         # Example device config
│   ├── retry-config.json            # Retry policy configuration
│   └── README.md                    # Device configuration guide
│
├── scripts/                         # Utility scripts
│   ├── start-appium-nodes.sh        # Start Appium servers
│   ├── stop-appium-nodes.sh         # Stop Appium servers
│   ├── run-tests.sh                 # Test runner script
│   └── setup-environment.sh         # Environment setup
│
├── src/
│   ├── main/java/com/parallel/appium/
│   │   ├── core/                    # Core framework components
│   │   │   ├── DevicePool.java      # Device allocation manager
│   │   │   ├── DriverFactory.java   # ThreadLocal driver management
│   │   │   ├── PortManager.java     # Port allocation
│   │   │   └── TestContext.java     # Test context management
│   │   │
│   │   ├── config/                  # Configuration classes
│   │   │   ├── DeviceConfig.java    # Device configuration model
│   │   │   └── ConfigReader.java    # Configuration reader
│   │   │
│   │   ├── pages/                   # Page Object Model
│   │   │   ├── base/BasePage.java   # Base page class
│   │   │   ├── login/LoginPage.java
│   │   │   └── home/HomePage.java
│   │   │
│   │   ├── reporting/               # Reporting components
│   │   │   ├── ExtentReportManager.java
│   │   │   ├── ConsolidatedReportBuilder.java
│   │   │   └── DeviceStatsManager.java
│   │   │
│   │   ├── cloud/                   # Cloud provider support
│   │   │   ├── CloudProvider.java
│   │   │   ├── LambdaTestProvider.java
│   │   │   └── BrowserStackProvider.java
│   │   │
│   │   ├── retry/                   # Retry logic
│   │   │   └── RetryAnalyzer.java
│   │   │
│   │   ├── cache/                   # Result caching
│   │   │   └── TestResultCache.java
│   │   │
│   │   └── utils/                   # Utility classes
│   │       ├── WaitHelper.java
│   │       ├── ScreenshotUtils.java
│   │       └── GestureHelper.java
│   │
│   └── test/java/com/parallel/appium/
│       ├── tests/                   # Test classes
│       │   ├── base/BaseTest.java   # Base test class
│       │   ├── login/LoginTests.java
│       │   └── product/ProductTests.java
│       │
│       ├── runners/                 # Test runners
│       │   └── CucumberTestRunner.java
│       │
│       ├── stepdefinitions/         # Cucumber step definitions
│       │   ├── LoginStepDefinitions.java
│       │   └── ProductStepDefinitions.java
│       │
│       └── resources/
│           ├── features/            # Cucumber feature files
│           │   ├── login.feature
│           │   └── product.feature
│           └── cucumber.properties
│
├── reports/                         # Generated reports (gitignored)
├── logs/                            # Execution logs (gitignored)
│
├── .github/workflows/               # GitHub Actions workflows
│   └── appium-tests.yml
│
├── Jenkinsfile                      # Jenkins pipeline
├── pom.xml                          # Maven configuration
├── testng.xml                       # TestNG configuration
├── testng-cucumber.xml              # Cucumber TestNG config
│
└── Documentation/
    ├── README.md                    # This file
    ├── SETUP_GUIDE.md               # Complete setup instructions
    ├── USAGE_GUIDE.md               # How to write tests
    ├── TECHNICAL_GUIDE.md           # Architecture deep dive
    ├── REAL_WORLD_USE_CASES.md      # Practical examples
    └── DOCUMENTATION_INDEX.md       # Documentation navigation
```

---

## 💻 Examples

### Example 1: Simple Login Test

```java
public class LoginTests extends BaseTest {
    
    @Test(description = "TC001: Verify successful login")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("user@example.com", "password");
        
        Assert.assertTrue(homePage.isPageLoaded());
    }
}
```

### Example 2: Cucumber BDD Test

**Feature File** (`login.feature`):
```gherkin
@login @smoke @TC001
Feature: User Login
  As a user
  I want to login to the application
  So that I can access my account

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I login with username "user@example.com" and password "password"
    Then I should be logged in successfully
    And I should see the welcome message
```

**Step Definitions**:
```java
@Given("I am on the login page")
public void iAmOnTheLoginPage() {
    loginPage = new LoginPage();
    Assert.assertTrue(loginPage.isPageLoaded());
}

@When("I login with username {string} and password {string}")
public void iLoginWithUsernameAndPassword(String username, String password) {
    homePage = loginPage.login(username, password);
}

@Then("I should be logged in successfully")
public void iShouldBeLoggedInSuccessfully() {
    Assert.assertTrue(homePage.isPageLoaded());
}
```

### Example 3: E-Commerce Shopping Flow

```java
@Test(description = "Complete shopping journey")
public void testCompleteShoppingJourney() {
    // Login
    LoginPage loginPage = new LoginPage();
    HomePage homePage = loginPage.login("customer@shop.com", "password");
    
    // Browse products
    ProductPage productPage = homePage.navigateToProducts();
    productPage.searchProduct("laptop");
    
    // Add to cart
    productPage.selectProduct("MacBook Pro");
    productPage.addToCart();
    
    // Checkout
    CartPage cartPage = productPage.viewCart();
    CheckoutPage checkout = cartPage.proceedToCheckout();
    checkout.enterShippingAddress("123 Main St", "City", "12345");
    checkout.placeOrder();
    
    // Verify order
    OrderConfirmationPage confirmation = checkout.getConfirmation();
    Assert.assertTrue(confirmation.isOrderConfirmed());
}
```

**For more examples, see [Real-World Use Cases](REAL_WORLD_USE_CASES.md)**

---

## 📚 Documentation

### Complete Documentation Suite

Orchestrix comes with comprehensive documentation for all skill levels:

#### For Beginners
- 📖 **[Setup Guide](SETUP_GUIDE.md)** - Complete step-by-step installation and configuration
- 📝 **[Usage Guide](USAGE_GUIDE.md)** - Learn how to write tests with detailed examples
- 🌍 **[Real-World Use Cases](REAL_WORLD_USE_CASES.md)** - 15+ practical examples for different app types

#### For Advanced Users
- 🏗️ **[Technical Guide](TECHNICAL_GUIDE.md)** - Architecture, design patterns, and component deep dive
- 📊 **[Framework Summary](FRAMEWORK_SUMMARY.md)** - Complete feature overview
- 🥒 **[Cucumber BDD Guide](CUCUMBER_BDD_GUIDE.md)** - BDD testing with Cucumber
- ☁️ **[Cloud Integration Guide](CLOUD_PROVIDERS_GUIDE.md)** - LambdaTest, BrowserStack setup

#### Quick References
- ⚡ **[Quick Start Guide](QUICK_START.md)** - Get running in 5 minutes
- 📱 **[Local Device Setup](LOCAL_DEVICE_SETUP_GUIDE.md)** - Setup emulators and physical devices
- 🔧 **[Environment Setup](ENV_FILE_SETUP.md)** - Environment variables configuration
- 📱 **[Device Configuration](config/README.md)** - Device setup guide
- 📑 **[Documentation Index](DOCUMENTATION_INDEX.md)** - Navigate all documentation

### Documentation Navigation

**New to the framework?** Start here:
1. Read [Setup Guide](SETUP_GUIDE.md) for installation
2. Follow [Usage Guide](USAGE_GUIDE.md) for writing tests
3. Explore [Real-World Use Cases](REAL_WORLD_USE_CASES.md) for examples

**Want to understand the architecture?**
1. Read [Technical Guide](TECHNICAL_GUIDE.md)
2. Review [Framework Summary](FRAMEWORK_SUMMARY.md)

**Need help?** Check [Documentation Index](DOCUMENTATION_INDEX.md) for quick navigation.

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### How to Contribute

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes**
4. **Add tests** for new functionality
5. **Ensure all tests pass** (`mvn clean test`)
6. **Commit your changes** (`git commit -m 'Add amazing feature'`)
7. **Push to the branch** (`git push origin feature/amazing-feature`)
8. **Open a Pull Request**

### Contribution Guidelines

- Follow Java coding conventions
- Write meaningful commit messages
- Add documentation for new features
- Include tests for new functionality
- Update CHANGELOG.md for significant changes

### Areas for Contribution

- 🐛 Bug fixes
- ✨ New features
- 📚 Documentation improvements
- 🧪 Test coverage
- 🌍 Cloud provider integrations
- 🎨 UI/UX improvements for reports

**See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.**

---

## 🆘 Support

### Getting Help

- 📖 **Documentation**: Check [Documentation Index](DOCUMENTATION_INDEX.md)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/your-username/orchestrix/discussions)
- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/your-username/orchestrix/issues)
- 📧 **Email**: convergentthinker22@gmail.com
- 🌐 **Website**: [Visit our website](https://orchestrix-beta.vercel.app/)

### Common Issues

| Issue | Solution |
|-------|----------|
| Port conflicts | Use `./scripts/stop-appium-nodes.sh` to clean up |
| Device not found | Verify device UDID in `config/devices.json` |
| Tests hang | Check thread count matches available devices |
| Screenshots not loading | Ensure screenshots are in same folder as HTML |
| Build fails | Run `mvn clean install -U` to update dependencies |

**For more troubleshooting, see [Setup Guide - Troubleshooting](SETUP_GUIDE.md#troubleshooting)**

---

## 🏆 Features in Detail

### Parallel Execution

Run tests simultaneously across multiple devices:

```bash
# Run 4 tests in parallel
mvn clean test -Dparallel.threads=4
```

### Device Tier Management

Organize devices by tier with automatic fallback:

```json
{
  "tier": "premium",  // premium → standard → basic (automatic fallback)
  "deviceName": "iPhone 15 Pro"
}
```

### Feature Grouping in Reports

Tests are automatically grouped by feature files:

```
Feature: Login
  ├── Scenario 1 [Device 1]
  └── Scenario 2 [Device 2]
Feature: Product
  └── Scenario 1 [Device 1]
```

### Cloud Provider Support

Test on cloud devices with minimal configuration:

```json
{
  "executionType": "lambdatest",
  "cloudProvider": "lambdatest",
  "tunnelId": "your-tunnel-id"
}
```

---

## 📊 Reporting

### ExtentReports

- Feature-grouped test results
- Device-wise breakdown
- Screenshots on failure
- Thread-safe parallel execution
- Beautiful HTML reports

### Consolidated Reports

- Device utilization statistics
- Pass/fail rates per device
- Performance metrics
- Historical trends

**Report Structure:**
```
reports/
  ├── consolidated/
  │   └── Device_summary<timestamp>.html
  └── extent/
      └── TestReport-<timestamp>/
          ├── Report.html
          └── screenshots/
```

---

## 🔄 CI/CD Integration

### Jenkins

Use the included `Jenkinsfile`:

```groovy
pipeline {
    agent any
    stages {
        stage('Run Tests') {
            steps {
                sh 'mvn clean test'
            }
        }
    }
}
```

### GitHub Actions

Use `.github/workflows/appium-tests.yml`:

```yaml
name: Appium Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run tests
        run: mvn clean test
```

---

## 📈 Performance

### Execution Speed

- **Sequential**: ~10 minutes for 20 tests
- **Parallel (4 devices)**: ~3 minutes for 20 tests
- **Speed Improvement**: **3-10x faster**

### Resource Efficiency

- Automatic port management
- Device pooling with tier-based allocation
- Result caching saves 30-70% execution time
- Thread-safe execution with zero conflicts

---

## 🛠️ Technology Stack

- **Java 17+** - Modern Java features
- **Maven** - Dependency management
- **Appium 2.x** - Mobile automation
- **TestNG** - Test framework
- **Cucumber** - BDD support
- **ExtentReports** - HTML reporting
- **Gson** - JSON processing

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Appium Community** - For the amazing mobile automation framework
- **ExtentReports** - For beautiful reporting capabilities
- **FPGA Hardware Design** - Inspiration for parallel architecture patterns

---

## 🌟 Star History

If you find Orchestrix useful, please consider giving it a star ⭐

[![Star History Chart](https://api.star-history.com/svg?repos=your-username/orchestrix&type=Date)](https://star-history.com/#your-username/orchestrix&Date)

---

## 📞 Contact & Links
- 🌐 **Website**: [orchestrix](https://orchestrix-beta.vercel.app/)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/ConvergentThinker/orchestrix/discussions)
- 🐛 **Issues**: [GitHub Issues](https://github.com/ConvergentThinker/orchestrix/issues)
- 📖 **Documentation**: [Full Documentation](DOCUMENTATION_INDEX.md)

---

<div align="center">

**Built with ❤️ for enterprise mobile test automation**

[⬆ Back to Top](#-orchestrix---enterprise-parallel-appium-framework)

</div>
