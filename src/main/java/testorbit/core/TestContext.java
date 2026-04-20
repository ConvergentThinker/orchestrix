package testorbit.core;

import testorbit.config.DeviceConfig;
import io.appium.java_client.AppiumDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Context - Thread-safe context for test execution
 * Stores driver, device config, and test data per thread
 */
public class TestContext {
    private ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private ThreadLocal<DeviceConfig> deviceConfig = new ThreadLocal<>();
    private ThreadLocal<Map<String, Object>> testData = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    // Driver management
    public AppiumDriver getDriver() {
        return driver.get();
    }

    public void setDriver(AppiumDriver driver) {
        this.driver.set(driver);
    }

    // Device config management
    public DeviceConfig getDeviceConfig() {
        return deviceConfig.get();
    }

    public void setDeviceConfig(DeviceConfig config) {
        this.deviceConfig.set(config);
    }

    // Test data management
    public void setData(String key, Object value) {
        testData.get().put(key, value);
    }

    public Object getData(String key) {
        return testData.get().get(key);
    }

    public void clearData() {
        testData.get().clear();
    }

    // Cleanup
    public void cleanup() {
        driver.remove();
        deviceConfig.remove();
        testData.remove();
    }
}
