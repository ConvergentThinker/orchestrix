# Configuration Files

This directory contains framework configuration files.

## Files

### devices.json
Device farm configuration file. Defines all available devices for parallel test execution.

**Structure:**
```json
[
  {
    "udid": "device-unique-id",
    "deviceName": "Device Display Name",
    "platformName": "Android" or "iOS",
    "platformVersion": "Platform version",
    "tier": "premium" | "standard" | "basic",
    "executionType": "local",  // "local" | "lambdatest" | "browserstack"
    "appPackage": "com.yourapp.package",  // Android only (optional)
    "appActivity": "com.yourapp.MainActivity",  // Android only (optional)
    // Ports are OPTIONAL - auto-allocated if omitted:
    "appiumPort": 4723,        // Optional: auto-allocated if omitted
    "systemPort": 8200,        // Optional: Android only, auto-allocated if omitted
    "chromedriverPort": 9515,  // Optional: Android only, auto-allocated if omitted
    "wdaLocalPort": 8100       // Optional: iOS only, auto-allocated if omitted
  }
]
```

**Device Tiers:**
- `premium` - High-end devices for critical tests
- `standard` - Regular devices for standard tests
- `basic` - Lower-end devices for compatibility tests

**How to Find Device UDID:**
- **Android**: Run `adb devices` command
- **iOS Simulator**: Run `xcrun simctl list devices`
- **iOS Real Device**: Use Xcode or `instruments -s devices`

**Port Configuration:**
- **Ports are OPTIONAL** - Framework auto-allocates unique ports if not specified
- If you specify ports, ensure they are unique across all devices
- Auto-allocation prevents conflicts and simplifies configuration
- Port ranges:
  - Appium: 4723-4800
  - Android SystemPort: 8200-8300
  - Android ChromeDriver: 9515-9600
  - iOS WDA: 8100-8200

### retry-config.json
Retry policy configuration for test execution.

**Structure:**
```json
{
  "defaultMaxRetries": 3,
  "retryDelayMs": 2000,
  "exponentialBackoff": true,
  "testSpecificRetries": {
    "TestClass.testMethod": 5
  },
  "retryableExceptions": [
    "StaleElementReferenceException",
    "NoSuchElementException"
  ],
  "nonRetryableExceptions": [
    "AssertionError",
    "NullPointerException"
  ]
}
```

**Configuration Options:**
- `defaultMaxRetries` - Default number of retries for all tests
- `retryDelayMs` - Base delay between retries (milliseconds)
- `exponentialBackoff` - Whether to use exponential backoff (doubles delay each retry)
- `testSpecificRetries` - Override retry count for specific tests
- `retryableExceptions` - Exceptions that should trigger retry
- `nonRetryableExceptions` - Exceptions that should NOT trigger retry

## Usage

### Updating Device Configuration

1. Edit `devices.json`
2. Add/remove/modify device entries
3. **Ports are optional** - framework auto-allocates if not specified
4. Ensure unique UDIDs (ports are handled automatically)
5. Restart tests (framework loads config on startup)

### Updating Retry Configuration

1. Edit `retry-config.json`
2. Modify retry policies as needed
3. Changes take effect on next test run

## Example: Adding a New Device

**Minimal Configuration (Ports Auto-Allocated):**
```json
{
  "udid": "your-device-udid",
  "deviceName": "Your Device Name",
  "platformName": "Android",
  "platformVersion": "13",
  "tier": "standard",
  "executionType": "local",
  "appPackage": "com.yourapp.package",
  "appActivity": "com.yourapp.MainActivity"
  // Ports auto-allocated automatically!
}
```

**With Explicit Ports (Optional):**
```json
{
  "udid": "your-device-udid",
  "deviceName": "Your Device Name",
  "platformName": "Android",
  "platformVersion": "13",
  "tier": "standard",
  "executionType": "local",
  "appiumPort": 4727,
  "systemPort": 8203,
  "chromedriverPort": 9518,
  "appPackage": "com.yourapp.package",
  "appActivity": "com.yourapp.MainActivity"
}
```

## Validation

The framework validates configuration files on startup:
- JSON syntax validation
- Required fields check
- Port uniqueness check
- Device availability check

If validation fails, the framework will not start and will log the error.
