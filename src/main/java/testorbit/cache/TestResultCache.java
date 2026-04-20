package testorbit.cache;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test Result Cache
 * Skips unchanged tests to save time
 * 
 * CONCEPT:
 * 1. Before test: Check if test file changed
 * 2. If unchanged + previously passed → Skip
 * 3. If changed or previously failed → Run
 * 4. After test: Cache result with file checksum
 */
public class TestResultCache {
    private static final Logger logger = LoggerFactory.getLogger(TestResultCache.class);
    private static TestResultCache instance;
    
    private static final String CACHE_DIR = ".test-cache/";
    private static final String CACHE_FILE = CACHE_DIR + "results.json";
    
    // In-memory cache
    private Map<String, CachedResult> cache;
    private Gson gson = new Gson();

    private TestResultCache() {
        this.cache = new ConcurrentHashMap<>();
        initializeCache();
    }

    public static synchronized TestResultCache getInstance() {
        if (instance == null) {
            instance = new TestResultCache();
        }
        return instance;
    }

    /**
     * Initialize cache from disk
     */
    private void initializeCache() {
        try {
            Files.createDirectories(Paths.get(CACHE_DIR));
            
            File cacheFile = new File(CACHE_FILE);
            if (cacheFile.exists()) {
                try (FileReader reader = new FileReader(cacheFile)) {
                    CachedResult[] results = gson.fromJson(reader, CachedResult[].class);
                    
                    if (results != null) {
                        for (CachedResult result : results) {
                            cache.put(result.testId, result);
                        }
                        logger.info("✓ Loaded {} cached results", cache.size());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Could not load cache: {}", e.getMessage());
        }
    }

    /**
     * Check if test can be skipped
     * 
     * @param testId Test identifier
     * @param testFilePath Path to test file
     * @return true if can skip, false if must run
     */
    public boolean canSkipTest(String testId, String testFilePath) {
        CachedResult cached = cache.get(testId);
        
        // No cache entry → must run
        if (cached == null) {
            logger.debug("No cache for: {}", testId);
            return false;
        }

        // Check if cache expired (24 hours)
        if (isCacheExpired(cached)) {
            logger.debug("Cache expired for: {}", testId);
            cache.remove(testId);
            return false;
        }

        // Check if test file changed
        String currentChecksum = calculateChecksum(testFilePath);
        if (!currentChecksum.equals(cached.fileChecksum)) {
            logger.debug("Test file changed: {}", testId);
            cache.remove(testId);
            return false;
        }

        // If previously failed → must run
        if (!cached.passed) {
            logger.debug("Previously failed, must run: {}", testId);
            return false;
        }

        logger.info("✓ Skipping test (cached): {}", testId);
        return true;
    }

    /**
     * Cache test result
     */
    public void cacheResult(String testId, boolean passed, String testFilePath) {
        CachedResult result = new CachedResult();
        result.testId = testId;
        result.passed = passed;
        result.timestamp = LocalDateTime.now();
        result.fileChecksum = calculateChecksum(testFilePath);
        
        cache.put(testId, result);
        logger.debug("Cached result for: {}", testId);
    }

    /**
     * Save cache to disk
     */
    public void saveCache() {
        try (FileWriter writer = new FileWriter(CACHE_FILE)) {
            gson.toJson(cache.values(), writer);
            logger.info("✓ Saved {} cached results", cache.size());
        } catch (IOException e) {
            logger.error("Failed to save cache", e);
        }
    }

    /**
     * Calculate MD5 checksum of file
     */
    private String calculateChecksum(String filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            byte[] digest = md.digest(fileBytes);
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (Exception e) {
            logger.error("Failed to calculate checksum for: {}", filePath, e);
            return "";
        }
    }

    /**
     * Check if cache is expired (24 hours)
     */
    private boolean isCacheExpired(CachedResult result) {
        long hoursSince = ChronoUnit.HOURS.between(
            result.timestamp, LocalDateTime.now());
        return hoursSince > 24;
    }

    /**
     * Clear all cache
     */
    public void clearCache() {
        cache.clear();
        new File(CACHE_FILE).delete();
        logger.info("Cache cleared");
    }

    /**
     * Cached Result Model
     */
    private static class CachedResult {
        String testId;
        boolean passed;
        LocalDateTime timestamp;
        String fileChecksum;
    }
}
