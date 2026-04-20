package testorbit.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Environment Configuration Utility
 * Loads configuration from .env file in project root
 * 
 * Priority order:
 * 1. System properties (-Dkey=value)
 * 2. Environment variables
 * 3. .env file
 * 4. Default value
 */
public class EnvConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);
    private static Dotenv dotenv;
    private static boolean initialized = false;
    
    /**
     * Initialize .env file loader.
     * Loads from project root (user.dir). Do NOT use envFile.getParent() for ".env"
     * as it returns null and dotenv-java throws NPE on directory.replaceAll().
     */
    private static void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            String baseDir = System.getProperty("user.dir");
            if (baseDir == null || baseDir.isEmpty()) {
                baseDir = ".";
            }
            File envFile = new File(baseDir, ".env");
            dotenv = Dotenv.configure()
                .directory(baseDir)
                .filename(".env")
                .ignoreIfMissing()
                .load();
            if (envFile.exists() && envFile.canRead()) {
                logger.info("✓ Loaded .env from: {}", envFile.getAbsolutePath());
            } else {
                logger.warn("⚠ .env not found in {}. Using env vars / system properties only.", baseDir);
            }
        } catch (Exception e) {
            logger.warn("Failed to load .env: {}", e.getMessage());
            try {
                String baseDir = System.getProperty("user.dir");
                if (baseDir == null) baseDir = ".";
                dotenv = Dotenv.configure().directory(baseDir).ignoreIfMissing().load();
            } catch (Exception e2) {
                dotenv = null;
            }
        }
        
        initialized = true;
    }
    
    /**
     * Get configuration value with priority:
     * 1. System property
     * 2. Environment variable
     * 3. .env file
     * 4. Default value
     * 
     * @param key Configuration key
     * @param defaultValue Default value if not found
     * @return Configuration value
     */
    public static String get(String key, String defaultValue) {
        initialize();
        
        // Priority 1: System property
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            logger.debug("Using system property for {}: {}", key, maskSensitive(key, value));
            return value;
        }
        
        // Priority 2: Environment variable
        value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            logger.debug("Using environment variable for {}: {}", key, maskSensitive(key, value));
            return value;
        }
        
        // Priority 3: .env file
        if (dotenv != null) {
            value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                logger.debug("Using .env file for {}: {}", key, maskSensitive(key, value));
                return value;
            }
        }
        
        // Priority 4: Default value
        if (defaultValue != null && !defaultValue.isEmpty()) {
            logger.debug("Using default value for {}", key);
            return defaultValue;
        }
        
        return null;
    }
    
    /**
     * Get configuration value (no default)
     * 
     * @param key Configuration key
     * @return Configuration value or null
     */
    public static String get(String key) {
        return get(key, null);
    }
    
    /**
     * Mask sensitive values in logs
     */
    private static String maskSensitive(String key, String value) {
        if (key != null && (key.toLowerCase().contains("key") || 
                           key.toLowerCase().contains("password") || 
                           key.toLowerCase().contains("secret") ||
                           key.toLowerCase().contains("token"))) {
            if (value != null && value.length() > 10) {
                return value.substring(0, 10) + "***";
            }
            return "***";
        }
        return value;
    }
}
