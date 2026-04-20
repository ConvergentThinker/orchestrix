package testorbit.cloud;

import testorbit.utils.EnvConfig;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * LambdaTest Cloud Provider
 * Handles LambdaTest-specific configuration and capabilities
 */
public class LambdaTestProvider extends BaseCloudProvider {
    private static final Logger logger = LoggerFactory.getLogger(LambdaTestProvider.class);
    
    private static final String LAMBDATEST_SERVER_URL = "https://mobile-hub.lambdatest.com/wd/hub";
    private static final String LAMBDATEST_USERNAME_ENV = "LT_USERNAME";
    private static final String LAMBDATEST_ACCESS_KEY_ENV = "LT_ACCESS_KEY";
    private static final String LAMBDATEST_TUNNEL_ID_ENV = "LT_TUNNEL_ID";
    
    private String tunnelId;

    public LambdaTestProvider() {
        this(null, null, null);
    }
    
    public LambdaTestProvider(String username, String accessKey) {
        this(username, accessKey, null);
    }
    
    public LambdaTestProvider(String username, String accessKey, String tunnelId) {
        super(
            // Priority: Parameter > System Property > Environment Variable > .env file
            username != null && !username.isEmpty() ? username :
                EnvConfig.get("lt.username", 
                    EnvConfig.get(LAMBDATEST_USERNAME_ENV, "")),
            accessKey != null && !accessKey.isEmpty() ? accessKey :
                EnvConfig.get("lt.accesskey",
                    EnvConfig.get(LAMBDATEST_ACCESS_KEY_ENV, "")),
            LAMBDATEST_SERVER_URL
        );
        this.tunnelId = tunnelId != null && !tunnelId.isEmpty() ? tunnelId :
            EnvConfig.get("lt.tunnel.id",
                EnvConfig.get(LAMBDATEST_TUNNEL_ID_ENV, ""));
    }

    
    public void setTunnelId(String tunnelId) {
        this.tunnelId = tunnelId;
    }
    
    public String getTunnelId() {
        return tunnelId;
    }

    @Override
    public String getProviderName() {
        return "LambdaTest";
    }

    @Override
    public DesiredCapabilities buildCapabilities(DesiredCapabilities baseCapabilities) {
        logger.info("Building LambdaTest capabilities");
        
        java.util.Map<String, Object> ltOpts = getLambdaTestOptions();
        baseCapabilities.setCapability("lt:options", ltOpts);
        
        // isRealMobile at root (LambdaTest mandatory for real vs virtual)
        if (ltOpts.containsKey("isRealMobile")) {
            baseCapabilities.setCapability("isRealMobile", ltOpts.get("isRealMobile"));
        }
        
        // Set LambdaTest credentials at root level (not namespaced)
        // LambdaTest requires user and accessKey at root level, not under appium: namespace
        // We use setCapability which should set them at root level
        baseCapabilities.setCapability("user", username);
        baseCapabilities.setCapability("accessKey", accessKey);
        
        // LambdaTest requires these specific capability names
        if (baseCapabilities.getCapability("platformName") != null) {
            String platform = baseCapabilities.getCapability("platformName").toString();
            if ("Android".equalsIgnoreCase(platform)) {
                baseCapabilities.setCapability("platformName", "android");
            } else if ("iOS".equalsIgnoreCase(platform)) {
                baseCapabilities.setCapability("platformName", "ios");
            }
        }
        
        logger.info("LambdaTest credentials set - user: {}, accessKey: {}***", 
            username, accessKey != null && accessKey.length() > 10 ? accessKey.substring(0, 10) : "N/A");
        logger.debug("LambdaTest capabilities built successfully");
        return baseCapabilities;
    }

    /**
     * Get LambdaTest options
     */
    private java.util.Map<String, Object> getLambdaTestOptions() {
        java.util.Map<String, Object> ltOptions = new java.util.HashMap<>();
        ltOptions.put("build", System.getProperty("lt.build", "Parallel Appium Framework Build"));
        ltOptions.put("name", System.getProperty("lt.name", "Parallel Test Execution"));
        ltOptions.put("project", System.getProperty("lt.project", "Appium Tests"));
        ltOptions.put("w3c", false);  // Avoid namespacing; LambdaTest expects user/accessKey at root
        ltOptions.put("plugin", "java-testng");
        if (username != null && !username.isEmpty()) ltOptions.put("user", username);
        if (accessKey != null && !accessKey.isEmpty()) ltOptions.put("accessKey", accessKey);
        
        // Real vs Virtual: "Native App Automation Virtual" requires upgraded plan.
        // Default true = Real Device Cloud (included in most plans).
        boolean useRealDevices = !"false".equalsIgnoreCase(System.getProperty("lt.real.mobile", "true"));
        ltOptions.put("isRealMobile", useRealDevices);
        logger.info("LambdaTest device type: {}", useRealDevices ? "Real Device" : "Virtual");
        
        // Tunnel ID support for local testing
        // Priority: DeviceConfig > System Property > Environment Variable
        String tunnelIdToUse = this.tunnelId;
        if (tunnelIdToUse == null || tunnelIdToUse.isEmpty()) {
            tunnelIdToUse = System.getProperty("lt.tunnel.id");
        }
        if (tunnelIdToUse == null || tunnelIdToUse.isEmpty()) {
            tunnelIdToUse = System.getenv(LAMBDATEST_TUNNEL_ID_ENV);
        }
        
        if (tunnelIdToUse != null && !tunnelIdToUse.isEmpty()) {
            ltOptions.put("tunnel", true);
            ltOptions.put("tunnelName", tunnelIdToUse);
            logger.info("LambdaTest tunnel enabled with ID: {}", tunnelIdToUse);
        }
        
        return ltOptions;
    }

    @Override
    public String getServerUrl() {
        return buildAuthUrl();
    }

    /**
     * Build LambdaTest URL with embedded credentials (URL-encoded).
     * LambdaTest accepts Basic auth via URL; avoids W3C capability namespacing issues.
     */
    @Override
    protected String buildAuthUrl() {
        if (username == null || username.isEmpty() || accessKey == null || accessKey.isEmpty()) {
            return LAMBDATEST_SERVER_URL;
        }
        String encUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encKey = URLEncoder.encode(accessKey, StandardCharsets.UTF_8);
        return "https://" + encUser + ":" + encKey + "@mobile-hub.lambdatest.com/wd/hub";
    }
}
