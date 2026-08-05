package com.cholemetric.automation.utils;

import com.cholemetric.automation.config.AppiumConfig;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtil — Captures and saves screenshots on test failure.
 */
public class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);

    private ScreenshotUtil() {}

    /**
     * Capture a screenshot and save it to the configured screenshots directory.
     *
     * @param driver  The AndroidDriver instance
     * @param name    A descriptive name for the screenshot (test name/TC ID)
     * @return        Absolute path of the saved screenshot, or empty string on failure
     */
    public static String capture(AndroidDriver driver, String name) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String safeName  = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String fileName  = safeName + "_" + timestamp + ".png";

            String screenshotDir = AppiumConfig.getScreenshotsDir();
            File dir = new File(screenshotDir);
            if (!dir.exists()) { dir.mkdirs(); }

            File destination = new File(dir, fileName);
            File screenshot  = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, destination);

            log.info("Screenshot saved: {}", destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Capture screenshot with a prefix to indicate failure context.
     */
    public static String captureOnFailure(AndroidDriver driver, String testName) {
        return capture(driver, "FAIL_" + testName);
    }

    /**
     * Capture screenshot with pass prefix.
     */
    public static String captureOnPass(AndroidDriver driver, String testName) {
        return capture(driver, "PASS_" + testName);
    }
}
