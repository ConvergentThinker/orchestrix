package testorbit.cloud;

import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Cloud Provider Interface
 * Defines contract for cloud device providers (LambdaTest, BrowserStack, etc.)
 */
public interface CloudProvider {
    
    /**
     * Get provider name
     * @return Provider name (e.g., "LambdaTest", "BrowserStack")
     */
    String getProviderName();
    
    /**
     * Build cloud-specific capabilities
     * @param baseCapabilities Base capabilities from DeviceConfig
     * @return Enhanced capabilities with cloud-specific settings
     */
    DesiredCapabilities buildCapabilities(DesiredCapabilities baseCapabilities);
    
    /**
     * Get cloud server URL
     * @return Cloud server endpoint URL
     */
    String getServerUrl();
    
    /**
     * Validate cloud credentials
     * @return true if credentials are valid
     */
    boolean validateCredentials();
    
    /**
     * Get username for cloud provider
     * @return Username
     */
    String getUsername();
    
    /**
     * Get access key for cloud provider
     * @return Access key
     */
    String getAccessKey();
}
