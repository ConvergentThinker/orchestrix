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
    "appiumPort": 4723,
    "systemPort": 8200,        // Android only
    "chromedriverPort": 9515,  // Android only
    "wdaLocalPort": 8100       // iOS only
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
- Each device must have a unique `appiumPort`
- Android devices need unique `systemPort` and `chromedriverPort`
- iOS devices need unique `wdaLocalPort`
- Ports should not conflict with other services

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
3. Ensure unique UDIDs and ports
4. Restart tests (framework loads config on startup)

### Updating Retry Configuration

1. Edit `retry-config.json`
2. Modify retry policies as needed
3. Changes take effect on next test run

## Example: Adding a New Device

```json
{
  "udid": "your-device-udid",
  "deviceName": "Your Device Name",
  "platformName": "Android",
  "platformVersion": "13",
  "tier": "standard",
  "appiumPort": 4727,
  "systemPort": 8203,
  "chromedriverPort": 9518
}
```

## Validation

The framework validates configuration files on startup:
- JSON syntax validation
- Required fields check
- Port uniqueness check
- Device availability check

If validation fails, the framework will not start and will log the error.
