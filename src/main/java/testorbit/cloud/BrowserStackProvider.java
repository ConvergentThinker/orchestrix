package testorbit.cloud;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrowserStack Cloud Provider
 * Handles BrowserStack-specific configuration and capabilities
 */
public class BrowserStackProvider extends BaseCloudProvider {
    private static final Logger logger = LoggerFactory.getLogger(BrowserStackProvider.class);
    
    private static final String BROWSERSTACK_SERVER_URL = "https://hub.browserstack.com/wd/hub";
    private static final String BROWSERSTACK_USERNAME_ENV = "BROWSERSTACK_USERNAME";
    private static final String BROWSERSTACK_ACCESS_KEY_ENV = "BROWSERSTACK_ACCESS_KEY";

    public BrowserStackProvider() {
        super(
            System.getenv(BROWSERSTACK_USERNAME_ENV) != null ? 
                System.getenv(BROWSERSTACK_USERNAME_ENV) : 
                System.getProperty("browserstack.username", ""),
            System.getenv(BROWSERSTACK_ACCESS_KEY_ENV) != null ? 
                System.getenv(BROWSERSTACK_ACCESS_KEY_ENV) : 
                System.getProperty("browserstack.accesskey", ""),
            BROWSERSTACK_SERVER_URL
        );
    }

    public BrowserStackProvider(String username, String accessKey) {
        super(username, accessKey, BROWSERSTACK_SERVER_URL);
    }

    @Override
    public String getProviderName() {
        return "BrowserStack";
    }

    @Override
    public DesiredCapabilities buildCapabilities(DesiredCapabilities baseCapabilities) {
        logger.info("Building BrowserStack capabilities");
        
        // BrowserStack specific capabilities
        baseCapabilities.setCapability("bstack:options", getBrowserStackOptions());
        
        // Set BrowserStack credentials
        baseCapabilities.setCapability("userName", username);
        baseCapabilities.setCapability("accessKey", accessKey);
        
        // BrowserStack requires lowercase platform names
        if (baseCapabilities.getCapability("platformName") != null) {
            String platform = baseCapabilities.getCapability("platformName").toString();
            if ("Android".equalsIgnoreCase(platform)) {
                baseCapabilities.setCapability("platformName", "android");
            } else if ("iOS".equalsIgnoreCase(platform)) {
                baseCapabilities.setCapability("platformName", "ios");
            }
        }
        
        logger.debug("BrowserStack capabilities built successfully");
        return baseCapabilities;
    }

    /**
     * Get BrowserStack options
     */
    private java.util.Map<String, Object> getBrowserStackOptions() {
        java.util.Map<String, Object> bstackOptions = new java.util.HashMap<>();
        bstackOptions.put("buildName", System.getProperty("browserstack.build", "Parallel Appium Framework Build"));
        bstackOptions.put("sessionName", System.getProperty("browserstack.session", "Parallel Test Execution"));
        bstackOptions.put("projectName", System.getProperty("browserstack.project", "Appium Tests"));
        bstackOptions.put("debug", false);
        bstackOptions.put("networkLogs", true);
        bstackOptions.put("consoleLogs", "info");
        return bstackOptions;
    }

    @Override
    public String getServerUrl() {
        return buildAuthUrl();
    }
}
