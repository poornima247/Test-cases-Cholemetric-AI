package com.cholemetric.automation.listeners;

import com.cholemetric.automation.config.AppiumConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryListener — Retries failed tests up to the configured retry count.
 */
public class RetryListener implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryListener.class);
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetry = AppiumConfig.getRetryCount();
        if (retryCount < maxRetry) {
            retryCount++;
            log.warn("🔄 RETRY #{} for test: [{}]", retryCount, result.getName());
            return true;
        }
        return false;
    }
}
