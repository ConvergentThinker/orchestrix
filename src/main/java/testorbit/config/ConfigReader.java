package testorbit.config;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Reader
 * Reads and parses configuration files
 */
public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final Gson gson = new Gson();
    
    private static Map<String, Object> retryConfig;

    /**
     * Load retry configuration
     */
    public static Map<String, Object> getRetryConfig() {
        if (retryConfig == null) {
            try (FileReader reader = new FileReader("config/retry-config.json")) {
                retryConfig = gson.fromJson(reader, Map.class);
                logger.info("✓ Loaded retry configuration");
            } catch (Exception e) {
                logger.warn("Could not load retry config, using defaults", e);
                retryConfig = getDefaultRetryConfig();
            }
        }
        return retryConfig;
    }

    /**
     * Get default retry configuration
     */
    private static Map<String, Object> getDefaultRetryConfig() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("defaultMaxRetries", 3);
        defaults.put("retryDelayMs", 2000);
        defaults.put("exponentialBackoff", true);
        return defaults;
    }

    /**
     * Get max retries for a specific test
     */
    @SuppressWarnings("unchecked")
    public static int getMaxRetriesForTest(String testName) {
        Map<String, Object> config = getRetryConfig();
        
        if (config.containsKey("testSpecificRetries")) {
            Map<String, Object> testSpecific = (Map<String, Object>) config.get("testSpecificRetries");
            if (testSpecific.containsKey(testName)) {
                return ((Double) testSpecific.get(testName)).intValue();
            }
        }
        
        return ((Double) config.getOrDefault("defaultMaxRetries", 3)).intValue();
    }
}
