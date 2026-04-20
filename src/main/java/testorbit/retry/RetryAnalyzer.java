package testorbit.retry;

import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Intelligent Retry Analyzer
 * 
 * WHAT IT DOES:
 * - Automatically retries failed tests
 * - Only retries transient failures (not assertion errors)
 * - Uses exponential backoff
 * - Tracks retry attempts per test
 * 
 * USAGE: Add to @Test annotation
 * @Test(retryAnalyzer = RetryAnalyzer.class)
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    
    // Track retry count per test (thread-safe)
    private static ConcurrentHashMap<String, AtomicInteger> retryCount = 
        new ConcurrentHashMap<>();
    
    // Configuration
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 2000;

    @Override
    public boolean retry(ITestResult result) {
        String testName = getTestName(result);
        
        // Get current retry count for this test
        AtomicInteger count = retryCount.computeIfAbsent(
            testName, k -> new AtomicInteger(0));
        
        int currentCount = count.get();
        
        // Check if should retry
        if (currentCount < MAX_RETRY_COUNT && shouldRetry(result)) {
            count.incrementAndGet();
            
            logger.warn("⚠ Test failed: {} - Retry attempt {}/{}", 
                testName, currentCount + 1, MAX_RETRY_COUNT);
            
            // Wait before retry (exponential backoff)
            waitBeforeRetry(currentCount);
            
            return true; // Retry
        }
        
        // Max retries exhausted or not retryable
        if (currentCount >= MAX_RETRY_COUNT) {
            logger.error("✗ Test failed after {} retries: {}", 
                MAX_RETRY_COUNT, testName);
        }
        
        return false; // Don't retry
    }

    /**
     * Determine if test should be retried
     * 
     * RETRY: Selenium exceptions (transient)
     * DON'T RETRY: Assertion errors (real bugs)
     */
    private boolean shouldRetry(ITestResult result) {
        Throwable throwable = result.getThrowable();
        
        if (throwable == null) {
            return false;
        }

        // DON'T retry assertion errors (real test failures)
        if (throwable instanceof AssertionError) {
            logger.debug("Assertion error - will not retry");
            return false;
        }

        // RETRY these exceptions (transient issues)
        if (throwable instanceof StaleElementReferenceException ||
            throwable instanceof NoSuchElementException ||
            throwable instanceof TimeoutException ||
            throwable instanceof ElementNotInteractableException ||
            throwable instanceof WebDriverException) {
            
            logger.debug("Transient exception detected: {}", 
                throwable.getClass().getSimpleName());
            return true;
        }

        // RETRY if error message indicates transient issue
        String message = throwable.getMessage();
        if (message != null && (
            message.contains("timeout") ||
            message.contains("connection") ||
            message.contains("network") ||
            message.contains("server error"))) {
            
            logger.debug("Transient error message detected");
            return true;
        }

        return false;
    }

    /**
     * Wait before retry with exponential backoff
     * Attempt 1: 2 seconds
     * Attempt 2: 4 seconds
     * Attempt 3: 8 seconds
     */
    private void waitBeforeRetry(int retryCount) {
        long delay = RETRY_DELAY_MS * (long) Math.pow(2, retryCount);
        
        try {
            logger.debug("Waiting {}ms before retry...", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + 
               result.getMethod().getMethodName();
    }
}
