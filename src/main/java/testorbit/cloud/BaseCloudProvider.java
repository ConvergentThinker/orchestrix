package testorbit.cloud;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Cloud Provider
 * Provides common functionality for cloud providers
 */
public abstract class BaseCloudProvider implements CloudProvider {
    protected static final Logger logger = LoggerFactory.getLogger(BaseCloudProvider.class);
    
    protected String username;
    protected String accessKey;
    protected String serverUrl;

    public BaseCloudProvider(String username, String accessKey, String serverUrl) {
        this.username = username;
        this.accessKey = accessKey;
        this.serverUrl = serverUrl;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public boolean validateCredentials() {
        if (username == null || username.isEmpty()) {
            logger.error("Cloud provider username is missing");
            return false;
        }
        if (accessKey == null || accessKey.isEmpty()) {
            logger.error("Cloud provider access key is missing");
            return false;
        }
        return true;
    }

    /**
     * Build authentication URL
     * Note: With newer Selenium versions (4.23.0+), credentials in URL can cause 401 errors
     * Credentials should be passed via capabilities instead (which we do in buildCapabilities)
     * @return Base URL without credentials
     */
    protected String buildAuthUrl() {
        // Return base URL without credentials
        // Credentials are passed via capabilities to avoid 401 errors with Java HTTP client
        return serverUrl;
    }
}
