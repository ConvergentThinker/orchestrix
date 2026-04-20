package testorbit.config;

import java.util.Map;

public class DeviceConfig {
    private String udid;
    private String deviceName;
    private String platformName;
    private String platformVersion;
    private String tier;
    private int appiumPort = 0;        // 0 = auto-allocate, >0 = use specified port
    private int systemPort = 0;        // Android only, 0 = auto-allocate
    private int chromedriverPort = 0;  // Android only, 0 = auto-allocate
    private int wdaLocalPort = 0;      // iOS only, 0 = auto-allocate
    private Map<String, Object> capabilities;
    
    // Cloud provider support
    private String executionType;  // "local", "lambdatest", "browserstack"
    private String cloudProvider;  // Provider name if cloud
    private String cloudUsername;  // Cloud username (from JSON or env)
    private String cloudAccessKey; // Cloud access key (from JSON or env)
    private String cloudDeviceName; // Cloud-specific device name/identifier
    private String tunnelId; // LambdaTest tunnel ID for local testing
    private String cloudAppUrl; // Cloud app URL (lt:// or bs:// format)
    private String appPackage;  // Android app package (from devices.json)
    private String appActivity; // Android main activity (from devices.json)

    // Getters and Setters
    public String getUdid() { return udid; }
    public void setUdid(String udid) { this.udid = udid; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getPlatformName() { return platformName; }
    public void setPlatformName(String platformName) { this.platformName = platformName; }
    
    public String getPlatformVersion() { return platformVersion; }
    public void setPlatformVersion(String platformVersion) { 
        this.platformVersion = platformVersion; 
    }
    
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    
    public int getAppiumPort() { return appiumPort; }
    public void setAppiumPort(int appiumPort) { this.appiumPort = appiumPort; }
    
    public int getSystemPort() { return systemPort; }
    public void setSystemPort(int systemPort) { this.systemPort = systemPort; }
    
    public int getChromedriverPort() { return chromedriverPort; }
    public void setChromedriverPort(int chromedriverPort) { 
        this.chromedriverPort = chromedriverPort; 
    }
    
    public int getWdaLocalPort() { return wdaLocalPort; }
    public void setWdaLocalPort(int wdaLocalPort) { this.wdaLocalPort = wdaLocalPort; }
    
    public Map<String, Object> getCapabilities() { return capabilities; }
    public void setCapabilities(Map<String, Object> capabilities) { 
        this.capabilities = capabilities; 
    }
    
    // Cloud provider getters and setters
    public String getExecutionType() { 
        return executionType != null ? executionType : "local"; 
    }
    public void setExecutionType(String executionType) { 
        this.executionType = executionType; 
    }
    
    public String getCloudProvider() { return cloudProvider; }
    public void setCloudProvider(String cloudProvider) { 
        this.cloudProvider = cloudProvider; 
    }
    
    public String getCloudUsername() { return cloudUsername; }
    public void setCloudUsername(String cloudUsername) { 
        this.cloudUsername = cloudUsername; 
    }
    
    public String getCloudAccessKey() { return cloudAccessKey; }
    public void setCloudAccessKey(String cloudAccessKey) { 
        this.cloudAccessKey = cloudAccessKey; 
    }
    
    public String getCloudDeviceName() { return cloudDeviceName; }
    public void setCloudDeviceName(String cloudDeviceName) { 
        this.cloudDeviceName = cloudDeviceName; 
    }
    
    /**
     * Check if device is cloud-based
     */
    public boolean isCloudDevice() {
        return executionType != null && 
               !executionType.equalsIgnoreCase("local") && 
               !executionType.isEmpty();
    }
    
    public String getTunnelId() { return tunnelId; }
    public void setTunnelId(String tunnelId) { 
        this.tunnelId = tunnelId; 
    }
    
    public String getCloudAppUrl() { return cloudAppUrl; }
    public void setCloudAppUrl(String cloudAppUrl) { 
        this.cloudAppUrl = cloudAppUrl; 
    }

    public String getAppPackage() { return appPackage; }
    public void setAppPackage(String appPackage) { this.appPackage = appPackage; }

    public String getAppActivity() { return appActivity; }
    public void setAppActivity(String appActivity) { this.appActivity = appActivity; }
}
