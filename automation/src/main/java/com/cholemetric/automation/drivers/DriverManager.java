package com.cholemetric.automation.drivers;

import com.cholemetric.automation.config.AppiumConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * DriverManager — Thread-safe Appium AndroidDriver lifecycle manager.
 * Supports parallel execution via ThreadLocal storage.
 */
public class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> driverThread = new ThreadLocal<>();

    private DriverManager() {}

    /**
     * Initialize the AndroidDriver with UiAutomator2 capabilities.
     */
    public static void initDriver() {
        if (driverThread.get() != null) {
            log.warn("Driver already initialized for this thread. Quitting existing driver first.");
            quitDriver();
        }

        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(AppiumConfig.getPlatform());
        options.setDeviceName(AppiumConfig.getDeviceName());

        String platformVer = AppiumConfig.getPlatformVersion();
        if (!platformVer.isEmpty()) {
            options.setPlatformVersion(platformVer);
        }

        options.setAppPackage(AppiumConfig.getAppPackage());
        options.setAppActivity(AppiumConfig.getAppActivity());
        options.setNoReset(AppiumConfig.isNoReset());
        options.setNewCommandTimeout(Duration.ofSeconds(AppiumConfig.getNewCommandTimeout()));
        options.setAutomationName(AppiumConfig.getAutomationName());

        // Set APK path if running locally or in CI
        String appPath = AppiumConfig.getAppPath();
        if (!appPath.isEmpty()) {
            try {
                String absolutePath = Paths.get(appPath).toAbsolutePath().toString();
                options.setApp(absolutePath);
                log.info("App path set to: {}", absolutePath);
            } catch (Exception e) {
                log.warn("Could not resolve absolute app path: {}", e.getMessage());
            }
        }

        options.setCapability("appium:settings[waitForIdleTimeout]", 100);
        options.setCapability("appium:settings[waitForSelectorTimeout]", 10000);

        try {
            URL appiumUrl = new URL(AppiumConfig.getAppiumUrl());
            AndroidDriver driver = new AndroidDriver(appiumUrl, options);
            driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(AppiumConfig.getImplicitWait())
            );
            driverThread.set(driver);
            log.info("AndroidDriver initialized. SessionId: {}", driver.getSessionId());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium URL: " + AppiumConfig.getAppiumUrl(), e);
        }
    }

    /**
     * Get the current thread's driver instance.
     */
    public static AndroidDriver getDriver() {
        AndroidDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialized. Call DriverManager.initDriver() first.");
        }
        return driver;
    }

    /**
     * Quit and remove the current thread's driver.
     */
    public static void quitDriver() {
        AndroidDriver driver = driverThread.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("AndroidDriver quit successfully.");
            } catch (Exception e) {
                log.error("Error quitting driver: {}", e.getMessage());
            } finally {
                driverThread.remove();
            }
        }
    }

    /**
     * Check if driver is currently initialized for this thread.
     */
    public static boolean isDriverInitialized() {
        return driverThread.get() != null;
    }
}
