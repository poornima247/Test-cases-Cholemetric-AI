package com.cholemetric.automation.base;

import com.cholemetric.automation.config.AppiumConfig;
import com.cholemetric.automation.drivers.DriverManager;
import com.cholemetric.automation.utils.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * BaseTest — Parent class for all Appium test classes.
 * Handles driver lifecycle (init / quit) and screenshot capture on failure.
 */
public class BaseTest {

    protected AndroidDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        System.out.println("=== Starting Test: " + method.getName() + " ===");
        DriverManager.initDriver();
        driver = DriverManager.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("Test FAILED — capturing screenshot: " + result.getName());
            takeScreenshot(result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            System.out.println("Test PASSED: " + result.getName());
        } else {
            System.out.println("Test SKIPPED: " + result.getName());
        }
        DriverManager.quitDriver();
    }

    protected void takeScreenshot(String testName) {
        if (driver != null) {
            try {
                java.io.File screenshot = ScreenshotUtil.capture(driver, testName);
                if (screenshot != null) {
                    System.out.println("Screenshot saved: " + screenshot.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot for: " + testName + " — " + e.getMessage());
            }
        }
    }

    /**
     * Wait for a given duration in milliseconds (use sparingly — prefer explicit waits).
     */
    protected void pause(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }
}
