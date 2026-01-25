# 🔄 Parallel Execution Configuration Guide

## Table of Contents
1. [Overview](#overview)
2. [How It Works](#how-it-works)
3. [Port Allocation](#port-allocation)
4. [Device Allocation](#device-allocation)
5. [Configuration Steps](#configuration-steps)
6. [Examples](#examples)
7. [Real-World Test Runs](#real-world-test-runs)
8. [Troubleshooting](#troubleshooting)

---

## Overview

Orchestrix automatically distributes tests across available devices based on your configuration. The framework ensures that:

- ✅ **Tests run in parallel** only when devices are available
- ✅ **Tests wait indefinitely** for device availability when other tests are running
- ✅ **Tests fail after 60 seconds** only when no other tests are running
- ✅ **Thread count matches** available device count
- ✅ **Automatic distribution** across all available devices
- ✅ **Ports auto-allocated** — no need to specify `appiumPort`, `systemPort`, `chromedriverPort` in `devices.json`
- ✅ **Tier fallback** — e.g. `@standard` tests can use premium devices when no standard device exists

---

## How It Works

### Device-Aware Parallel Execution

```
Available Devices: 2
Thread Count: 2
Scenarios: 5

Execution Flow:
┌─────────────────────────────────────────┐
│ Scenario 1 → Device 1 (immediate)        │
│ Scenario 2 → Device 2 (immediate)        │
│ Scenario 3 → Waits for Device 1/2       │
│ Scenario 4 → Waits for Device 1/2       │
│ Scenario 5 → Waits for Device 1/2       │
└─────────────────────────────────────────┘

As devices become available, waiting scenarios are allocated.
```

### Key Components

1. **DevicePool**: Manages device allocation with intelligent wait and tier fallback
2. **PortManager**: Auto-allocates ports; **Appium port** per device, **systemPort/chromedriverPort** per session
3. **DriverFactory**: Creates driver per thread; allocates and releases session ports on quit
4. **Thread Count**: Controls how many scenarios run simultaneously
5. **Wait Mechanism**: 
   - Waits indefinitely when other tests are running (devices will be released)
   - Fails after 60 seconds only when no other tests are running
   - Polls every 10 seconds, checks other tests every 30 seconds

---

## Port Allocation

Ports are **auto-allocated** by the framework. You do **not** need to specify `appiumPort`, `systemPort`, or `chromedriverPort` in `devices.json` unless you want explicit values.

### Per-Device vs Per-Session

| Port Type | Allocated | When | Released |
|-----------|-----------|------|----------|
| **Appium** | Per device | Device load (DevicePool) | Never (static per device) |
| **systemPort** (Android) | Per session | Driver creation (DriverFactory) | Driver quit |
| **chromedriverPort** (Android) | Per session | Driver creation (DriverFactory) | Driver quit |
| **wdaLocalPort** (iOS) | Per device | Device load (DevicePool) | Never |

### Why Per-Session for systemPort / chromedriverPort?

Multiple parallel sessions can use the **same device** (e.g. thread-count &gt; devices, or sequential reuse). Each session needs unique `systemPort` and `chromedriverPort` to avoid *"port #8200 is busy"* errors. The framework allocates them when creating the driver and releases them when the driver quits.

### Port Ranges (PortManager)

- **Appium**: 4723–4800  
- **Android systemPort**: 8200–8300  
- **Android chromedriverPort**: 9515–9600  
- **iOS WDA**: 8100–8200  

### Optional Overrides

- **Appium port override** (e.g. when running default `appium` on 4723):
  ```bash
  mvn test -Dappium.port=4723
  ```
- **Explicit ports in devices.json**: You can still set `appiumPort`, `systemPort`, `chromedriverPort`. Appium port is respected; for Android, `systemPort`/`chromedriverPort` are **always** allocated per session for parallel safety.

---

## Device Allocation

### Tier-Based Allocation

Scenario tags (`@premium`, `@standard`, `@basic`) map to device tiers. The pool allocates a device matching the requested tier, with fallback.

### Tier Fallback Order

| Requested | Fallback order |
|-----------|----------------|
| **premium** | premium → standard → basic |
| **standard** | standard → basic → **premium** |
| **basic** | basic → standard → **premium** |

Standard/basic requests can use a **higher-tier** device if no same/lower-tier device is available (e.g. one premium device can run both `@premium` and `@standard` tests).

### Allocation Flow

1. **Lock-based**: Each device has a `ReentrantLock`. First thread to acquire the lock gets the device.
2. **First-come-first-served**: Among devices in the fallback list, the first available one is allocated.
3. **Wait or fail**: If no device is free, the scenario either **waits** (when other tests are running) or **fails after 60s** (when no other tests are running).

### Tags → Tier Mapping

- `@premium` → tier `premium`  
- `@standard` → tier `standard`  
- `@basic` → tier `basic`  
- No tag → default `standard` (or as configured in `CucumberHooks` / `BaseTest`)

---

## Configuration Steps

### Step 1: Count Your Available Devices

**Option A: Use the Script (Recommended)**

```bash
./scripts/calculate-thread-count.sh
```

**Output:**
```
Total devices in config/devices.json: 1
Recommended thread-count: 1
Recommended data-provider-thread-count: 1
```

**Option B: Manual Count**

Count devices in `config/devices.json`:

```json
[
  { "deviceName": "Device 1", ... },  // 1 device
  { "deviceName": "Device 2", ... }   // 2 devices
]
```

### Step 2: Set Thread Count

#### Method 1: Update testng-cucumber.xml

Edit `testng-cucumber.xml`:

```xml
<suite name="Cucumber BDD Test Suite" 
       parallel="methods" 
       thread-count="1" 
       data-provider-thread-count="1">
```

**Important**: Both `thread-count` and `data-provider-thread-count` must match your device count.

#### Method 2: Use Maven Property

```bash
# Set thread count via Maven property
mvn clean test -Dparallel.threads=1
```

#### Method 3: Update pom.xml

Edit `pom.xml`:

```xml
<properties>
    <parallel.threads>1</parallel.threads>  <!-- Match device count -->
</properties>
```

### Step 3: Verify Configuration

**Check devices.json:**
```bash
cat config/devices.json | jq '. | length'
```

**Check thread count in XML:**
```bash
grep "thread-count" testng-cucumber.xml
```

**They should match!**

---

## Examples

### Example 1: Single Device (Minimal Config — Ports Auto-Allocated)

**devices.json:** (no ports required)
```json
[
  {
    "udid": "bmqc4h7lmjn7ugrg",
    "deviceName": "Xiaomi",
    "platformName": "Android",
    "platformVersion": "14",
    "tier": "premium",
    "executionType": "local",
    "appPackage": "com.yourapp.package",
    "appActivity": ".MainActivity"
  }
]
```

**testng-cucumber.xml:**
```xml
<suite name="Cucumber BDD Test Suite" 
       parallel="methods" 
       thread-count="1" 
       data-provider-thread-count="1">
```

**Result:**
- ✅ Appium port auto-allocated (e.g. 4723); systemPort/chromedriverPort per session
- ✅ 1 scenario runs at a time; others wait for device
- ✅ No "No available device" or "port busy" errors

### Example 2: Two Devices

**devices.json:** (optional `appiumPort` only if you run separate Appium instances per device)
```json
[
  {
    "udid": "emulator-5554",
    "deviceName": "Pixel_7_Emulator",
    "platformName": "Android",
    "platformVersion": "13",
    "tier": "premium",
    "executionType": "local",
    "appPackage": "com.yourapp.package",
    "appActivity": ".MainActivity"
  },
  {
    "udid": "bmqc4h7lmjn7ugrg",
    "deviceName": "Xiaomi",
    "platformName": "Android",
    "platformVersion": "14",
    "tier": "premium",
    "executionType": "local",
    "appPackage": "com.yourapp.package",
    "appActivity": ".MainActivity"
  }
]
```

**testng-cucumber.xml:**
```xml
<suite name="Cucumber BDD Test Suite" 
       parallel="methods" 
       thread-count="2" 
       data-provider-thread-count="2">
```

**Result:**
- ✅ 2 scenarios run in parallel (one per device)
- ✅ systemPort/chromedriverPort unique per session
- ✅ Additional scenarios wait for device availability

### Example 3: Mixed Device Tiers + Tier Fallback

**devices.json:**
```json
[
  { "deviceName": "Premium Device", "tier": "premium", "executionType": "local", ... },
  { "deviceName": "Standard Device", "tier": "standard", "executionType": "local", ... },
  { "deviceName": "Basic Device", "tier": "basic", "executionType": "local", ... }
]
```

**testng-cucumber.xml:**
```xml
<suite name="Cucumber BDD Test Suite" 
       parallel="methods" 
       thread-count="3" 
       data-provider-thread-count="3">
```

**Result:**
- ✅ 3 scenarios can run in parallel
- ✅ `@premium` → premium, else standard, else basic
- ✅ `@standard` → standard, else basic, else **premium**
- ✅ `@basic` → basic, else standard, else **premium**
- ✅ Single premium device can serve both `@premium` and `@standard` (when used sequentially)

---

## Configuration Matrix

| Devices Available | Thread Count | Data Provider Thread Count | Behavior |
|-------------------|--------------|----------------------------|----------|
| 1 | 1 | 1 | Sequential execution, one at a time |
| 2 | 2 | 2 | 2 scenarios in parallel |
| 3 | 3 | 3 | 3 scenarios in parallel |
| 4 | 4 | 4 | 4 scenarios in parallel |
| N | N | N | N scenarios in parallel |

**Rule**: `thread-count = data-provider-thread-count = number of devices`

---

## Real-World Test Runs

### Run 1: Single Device, 5 @premium Scenarios (Cucumber)

**Setup:** 1 device (Xiaomi), `thread-count=1`, `data-provider-thread-count=1`.

**Execution:**
```
16:05:23 [TestNG-PoolService-0] ⏳ Waiting for device (tier: premium)...
16:05:23 [TestNG-PoolService-0] ✓ Allocated device: Xiaomi [tier: premium]
16:05:23 [TestNG-PoolService-0] Allocated session ports: systemPort=8200, chromedriverPort=9515
16:05:23 [TestNG-PoolService-0] ✓ Driver created for: Xiaomi
... scenario 1 runs ...
16:06:12 [TestNG-PoolService-0] ✓ Driver quit … Released session ports: 8200, 9515
16:06:12 [TestNG-PoolService-0] ✓ Released device: Xiaomi

16:06:13 [TestNG-PoolService-1] ✓ Allocated device: Xiaomi (after waiting)
16:06:13 [TestNG-PoolService-1] Allocated session ports: systemPort=8201, chromedriverPort=9516
... scenario 2 runs ...
```

**Allocation:** One scenario at a time. Others wait; when the device is released, the next gets it. Each session gets new `systemPort`/`chromedriverPort`, so no port conflicts.

---

### Run 2: Two Devices, 6 Scenarios, Parallel Then Queue

**Setup:** 2 devices (Pixel emulator + Xiaomi), `thread-count=2`, `data-provider-thread-count=2`.

**Execution:**
```
T0    Scenario A → Pixel (immediate)     Scenario B → Xiaomi (immediate)
T1    … both run in parallel …
T2    A finishes, releases Pixel         B still running
T3    Scenario C → Pixel (immediate)     B still running
T4    C finishes                         B finishes, releases Xiaomi
T5    Scenario D → Pixel                 Scenario E → Xiaomi
... D, E finish …
T6    Scenario F → either device
```

**Allocation:** First two scenarios use both devices. Later scenarios use whichever device is free. Same device can be reused across sessions; each session still gets unique `systemPort`/`chromedriverPort`.

---

### Run 3: One Premium Device, Mixed @premium and @standard

**Setup:** 1 device (Xiaomi, tier=premium). Tags: 2× `@premium`, 2× `@standard`. `thread-count=1`.

**Execution:**
```
@premium scenario 1  → Xiaomi (exact tier match)
@premium scenario 2  → waits, then Xiaomi after release
@standard scenario 1 → Xiaomi (fallback: no standard/basic, use premium)
@standard scenario 2 → waits, then Xiaomi after release
```

**Allocation:** All four use the same device. Standard scenarios succeed via tier fallback (standard → basic → **premium**). No "No available device for tier: standard" when only a premium device exists.

---

### Run 4: CI Pipeline (Jenkins / GitHub Actions)

**Setup:** `config/devices.json` has 2 local devices. Pipeline runs `./scripts/start-appium-nodes.sh`, then Maven.

**Example commands:**
```bash
# Jenkinsfile or workflow
./scripts/calculate-thread-count.sh   # e.g. 2
./scripts/start-appium-nodes.sh       # start Appium on ports from devices.json
mvn clean test -Pcucumber -Dparallel.threads=2
```

**Allocation:** Same as Run 2. Thread count matches device count; scenarios run in parallel then queue. Ports come from PortManager; no manual port configuration in CI.

---

### Run 5: Default Appium (Single Port 4723)

**Setup:** You run `appium` (default 4723). `devices.json` has no `appiumPort` or uses another port.

**Fix:** Override when running tests:
```bash
mvn test -Pcucumber -Dappium.port=4723
```

Or set `appiumPort: 4723` in `devices.json` for the local device. Appium port is per device; systemPort/chromedriverPort remain per session.

---

### Allocation Summary

| Scenario | Devices | Threads | Allocation |
|----------|---------|---------|------------|
| Single device, many scenarios | 1 | 1 | Sequential; wait for release; new session ports each run |
| Multi-device, many scenarios | 2+ | = device count | Parallel up to N; then queue; reuse devices across sessions |
| Mixed tiers, one device | 1 (premium) | 1 | Tier fallback: @standard uses premium |
| CI | N | N | Same as multi-device; use scripts + `-Dparallel.threads` |

---

## How Wait Mechanism Works

### Without Wait (Old Behavior)

```
Scenario 1 → Allocates Device 1 ✅
Scenario 2 → No device available ❌ FAILS
Scenario 3 → No device available ❌ FAILS
```

### With Wait (New Behavior)

```
Scenario 1 → Allocates Device 1 ✅
Scenario 2 → Waits... Device 1 released → Allocates Device 1 ✅
Scenario 3 → Waits... Device 1 released → Allocates Device 1 ✅
```

### Intelligent Wait Logic

The framework uses an intelligent wait mechanism that adapts based on test execution state:

**Wait Details:**
- Maximum wait time: **No fixed hardcoded time** - waits until device becomes available
- Poll interval: **10 seconds** - checks device availability every 10 seconds
- Other tests check: **Every 30 seconds** - verifies if other tests are running
- Fails only if no device available after **60 seconds** when **no other tests are running**
  - Test should not fail when other tests are running (devices will be released)

### How It Works Step-by-Step

```
1. Scenario tries to allocate device
   ↓
2. Device not available? → Start waiting
   ↓
3. Every 10 seconds: Try to allocate device
   ↓
4. Every 30 seconds: Check if other tests are running
   ↓
5a. Other tests running? → Continue waiting indefinitely
   ↓
5b. No other tests running? → Check timeout
   ↓
6a. Timeout < 60 seconds? → Continue waiting
   ↓
6b. Timeout >= 60 seconds? → Fail (no devices will be released)
```

### Example Scenarios

#### Scenario 1: Other Tests Running

```
Time 0s:  Scenario 2 tries to allocate → No device (Device 1 in use)
Time 10s: Try allocate → No device (Device 1 still in use)
Time 30s: Check other tests → YES (Device 1 allocated) → Wait indefinitely
Time 40s: Try allocate → No device
Time 60s: Check other tests → YES (Device 1 still in use) → Continue waiting
Time 70s: Try allocate → Device 1 released! → Allocate ✅
```

**Result**: Scenario waits until device becomes available (no timeout)

#### Scenario 2: No Other Tests Running

```
Time 0s:  Scenario 1 tries to allocate → No device (all devices unavailable)
Time 10s: Try allocate → No device
Time 30s: Check other tests → NO (no devices allocated) → Start timeout timer
Time 40s: Try allocate → No device
Time 60s: Check other tests → NO → Timeout reached → FAIL ❌
```

**Result**: Scenario fails after 60 seconds (no devices will be released)

#### Scenario 3: Device Becomes Available During Wait

```
Time 0s:  Scenario 2 tries to allocate → No device
Time 10s: Try allocate → Device 1 released! → Allocate ✅
```

**Result**: Scenario gets device as soon as it becomes available

---

## Running Tests

### Step 1: Calculate Thread Count

```bash
./scripts/calculate-thread-count.sh
```

### Step 2: Update Configuration

Based on output, update `testng-cucumber.xml` or use Maven property.

### Step 3: Start Appium Servers

**Option A — Use script (multiple devices, one Appium per device):**
```bash
./scripts/start-appium-nodes.sh
```
Each local device in `devices.json` must have `appiumPort` set; the script starts one Appium process per device on that port.

**Option B — Single Appium (one device or shared server):**
```bash
appium   # default 4723
```
Ensure `devices.json` either omits `appiumPort` (framework auto-allocates 4723) or uses `4723`. When using default `appium`, run tests with:
```bash
mvn test -Pcucumber -Dappium.port=4723
```

### Step 4: Run Tests

```bash
# Using XML configuration
mvn clean test -Pcucumber

# Or override thread count
mvn clean test -Pcucumber -Dparallel.threads=1
```

---

## Troubleshooting

### Issue 1: All Scenarios Start at Once

**Symptom:**
```
All scenarios try to start simultaneously
Multiple "No available device" errors
```

**Cause:** Thread count is higher than available devices.

**Solution:**
1. Count devices: `./scripts/calculate-thread-count.sh`
2. Update `testng-cucumber.xml`:
   ```xml
   thread-count="1"  <!-- Match device count -->
   data-provider-thread-count="1"
   ```
3. Or use Maven property:
   ```bash
   mvn clean test -Dparallel.threads=1
   ```

### Issue 2: Tests Hang/Wait Forever

**Symptom:**
```
Tests start but hang, never complete
```

**Cause:** Device not being released properly.

**Solution:**
1. Check `@After` hook releases device:
   ```java
   devicePool.releaseDevice(device);
   ```
2. Verify device is released in logs
3. Check for exceptions preventing device release

### Issue 3: "Timeout waiting for device"

**Symptom:**
```
Timeout waiting for device (tier: premium) after 60000ms
No other tests running, so no devices will be released
```

**Cause:** 
- No other tests are running (all devices should be available)
- Device configuration issue (device not properly loaded)
- Appium server not running for the device
- Device not actually available/connected

**Solution:**
1. Verify devices are loaded:
   ```bash
   # Check if devices.json is correct
   cat config/devices.json
   ```
2. Check device is actually connected:
   ```bash
   adb devices  # Android
   xcrun simctl list devices  # iOS
   ```
3. Verify Appium server is running:
   ```bash
   # Check if Appium is running on expected port
   curl http://127.0.0.1:4723/wd/hub/status
   ```
4. Check device release in `@After` hook (should not be issue if no other tests running)
5. Review logs for device loading errors

**Note:** This timeout only occurs when **no other tests are running**. If other tests are running, the framework waits indefinitely for device release.

### Issue 4: Tests Run Sequentially When Multiple Devices Available

**Symptom:**
```
Have 2 devices but only 1 test runs at a time
```

**Cause:** Thread count set to 1.

**Solution:**
Update `testng-cucumber.xml`:
```xml
thread-count="2"  <!-- Match device count -->
data-provider-thread-count="2"
```

### Issue 5: "Port #8200 is busy" (systemPort / chromedriverPort)

**Symptom:**
```
UiAutomator2 Server cannot start because the local port #8200 is busy.
Make sure the port you provide via 'systemPort' capability is not occupied.
```

**Cause:** Multiple sessions on the same device were using the same `systemPort`/`chromedriverPort`. This is handled by **per-session** allocation in `DriverFactory`.

**Solution:**
1. Ensure you are on the latest code: `systemPort` and `chromedriverPort` are allocated per session and released on driver quit.
2. Do **not** set `systemPort`/`chromedriverPort` in `devices.json` for local Android; let the framework allocate them.
3. If you still see it, check for leftover Appium/ADB processes and retry.

---

## Best Practices

### 1. Match Thread Count to Devices

**Always:**
```bash
thread-count = number of devices in devices.json
```

### 2. Use the Calculation Script

```bash
# Before running tests
./scripts/calculate-thread-count.sh
```

### 3. Verify Before Running

```bash
# Count devices
cat config/devices.json | jq '. | length'

# Check thread count
grep "thread-count" testng-cucumber.xml

# They should match!
```

### 4. Start with Single Device

When testing:
1. Start with 1 device, thread-count=1
2. Verify it works
3. Add more devices and increase thread count

### 5. Monitor Device Allocation

Watch logs to see:
- How many devices are allocated
- When devices are released
- If scenarios are waiting

### 6. Prefer Auto-Allocated Ports

- Omit `appiumPort`, `systemPort`, `chromedriverPort` in `devices.json` for local devices when possible.
- Use `-Dappium.port=4723` when running default `appium`; let the framework handle the rest.

---

## Advanced Configuration

### Dynamic Thread Count Based on Device Count

You can create a script to automatically update `testng-cucumber.xml`:

```bash
#!/bin/bash
DEVICE_COUNT=$(jq '. | length' config/devices.json)
sed -i '' "s/thread-count=\"[0-9]*\"/thread-count=\"$DEVICE_COUNT\"/" testng-cucumber.xml
sed -i '' "s/data-provider-thread-count=\"[0-9]*\"/data-provider-thread-count=\"$DEVICE_COUNT\"/" testng-cucumber.xml
echo "Updated thread-count to $DEVICE_COUNT"
```

### Tier-Based Thread Count

If you want to limit by tier:

```bash
# Count premium devices only
PREMIUM_COUNT=$(jq '[.[] | select(.tier == "premium")] | length' config/devices.json)
```

---

## Summary

### Quick Checklist

- [ ] Count devices in `config/devices.json`
- [ ] Set `thread-count` = device count
- [ ] Set `data-provider-thread-count` = device count
- [ ] Verify both match
- [ ] Omit ports in `devices.json` (or use `appiumPort` only if needed); systemPort/chromedriverPort are per-session
- [ ] Start Appium (`./scripts/start-appium-nodes.sh` or `appium` + `-Dappium.port=4723`)
- [ ] Run tests

### Key Points

1. **Thread count must match device count** for optimal execution
2. **Tests wait indefinitely** when other tests are running (devices will be released)
3. **Tests fail after 60 seconds** only when no other tests are running
4. **Automatic distribution** across available devices
5. **Use calculation script** to determine optimal thread count
6. **Ports:** Omit `appiumPort`/`systemPort`/`chromedriverPort` in `devices.json` for auto-allocation; use `-Dappium.port` if you run default `appium`
7. **Tier fallback:** `@standard` / `@basic` can use premium devices when no same-tier device exists

---

## Example Workflow

```bash
# 1. Check device count
./scripts/calculate-thread-count.sh
# Output: Recommended thread-count: 1

# 2. Update testng-cucumber.xml (if needed)
# Set thread-count="1" and data-provider-thread-count="1"

# 3. (Optional) Keep devices.json minimal — omit appiumPort, systemPort, chromedriverPort
#    Framework auto-allocates; use -Dappium.port=4723 if you run default `appium`

# 4. Start Appium
appium   # or ./scripts/start-appium-nodes.sh when using multiple devices with appiumPort set

# 5. Run tests
mvn clean test -Pcucumber
# Or with default appium: mvn clean test -Pcucumber -Dappium.port=4723

# 6. Verify execution
# Logs: "Allocated session ports: systemPort=..., chromedriverPort=...", "Released session ports"
# Scenarios wait then run; no "port busy" or "No available device" errors
```

---

**Need Help?** Check [Troubleshooting Guide](TROUBLESHOOTING_GUIDE.md) or [Documentation Index](DOCUMENTATION_INDEX.md)
