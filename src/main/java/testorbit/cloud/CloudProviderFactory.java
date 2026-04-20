package testorbit.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cloud Provider Factory
 * Creates appropriate cloud provider instances
 */
public class CloudProviderFactory {
    private static final Logger logger = LoggerFactory.getLogger(CloudProviderFactory.class);

    /**
     * Create cloud provider based on provider name
     * 
     * @param providerName Provider name (lambdatest, browserstack, local)
     * @param username Optional username (if not in env vars)
     * @param accessKey Optional access key (if not in env vars)
     * @return CloudProvider instance or null for local
     */
    public static CloudProvider createProvider(String providerName, String username, String accessKey) {
        return createProvider(providerName, username, accessKey, null);
    }

    /**
     * Create cloud provider based on provider name with tunnel ID support
     * 
     * @param providerName Provider name (lambdatest, browserstack, local)
     * @param username Optional username (if not in env vars)
     * @param accessKey Optional access key (if not in env vars)
     * @param tunnelId Optional tunnel ID for LambdaTest local testing
     * @return CloudProvider instance or null for local
     */
    public static CloudProvider createProvider(String providerName, String username, String accessKey, String tunnelId) {
        if (providerName == null || providerName.isEmpty() || "local".equalsIgnoreCase(providerName)) {
            logger.info("Using local device execution");
            return null; // Local execution
        }

        String provider = providerName.toLowerCase().trim();
        
        switch (provider) {
            case "lambdatest":
            case "lt":
                logger.info("Creating LambdaTest provider");
                LambdaTestProvider ltProvider;
                if (username != null && accessKey != null) {
                    if (tunnelId != null && !tunnelId.isEmpty()) {
                        ltProvider = new LambdaTestProvider(username, accessKey, tunnelId);
                    } else {
                        ltProvider = new LambdaTestProvider(username, accessKey);
                    }
                } else {
                    ltProvider = new LambdaTestProvider();
                }
                // Set tunnel ID if provided
                if (tunnelId != null && !tunnelId.isEmpty()) {
                    ltProvider.setTunnelId(tunnelId);
                }
                return ltProvider;
                
            case "browserstack":
            case "bs":
                logger.info("Creating BrowserStack provider");
                if (username != null && accessKey != null) {
                    return new BrowserStackProvider(username, accessKey);
                }
                return new BrowserStackProvider();
                
            default:
                logger.warn("Unknown cloud provider: {}. Falling back to local execution", providerName);
                return null;
        }
    }

    /**
     * Create cloud provider from provider name only
     * Uses environment variables or system properties for credentials
     */
    public static CloudProvider createProvider(String providerName) {
        return createProvider(providerName, null, null);
    }
}
