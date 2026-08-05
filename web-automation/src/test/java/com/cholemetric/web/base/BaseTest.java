package com.cholemetric.web.base;

import com.cholemetric.web.config.WebConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class BaseTest {
    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected String baseUrl;

    @BeforeMethod
    public void setUp() {
        baseUrl = WebConfig.getBaseUrl();
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("BASE_URL must be provided via -DBASE_URL=...");
        }
        // Ensure URL ends with /
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (WebConfig.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }

        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-web-security");
        options.addArguments("--log-level=3");

        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        webDriver.manage().window().maximize();

        driver.set(webDriver);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            System.out.println("❌ FAILED: " + result.getName());
            takeScreenshot(result.getName());
        } else if (ITestResult.SKIP == result.getStatus()) {
            System.out.println("⏭️ SKIPPED: " + result.getName());
        } else {
            System.out.println("✅ PASSED: " + result.getName());
        }
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }

    public WebDriver getDriver() {
        return driver.get();
    }

    private void takeScreenshot(String testName) {
        try {
            WebDriver webDriver = getDriver();
            if (webDriver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
                Path screenshotDir = Paths.get(System.getProperty("user.dir"), "..", "screenshots");
                Files.createDirectories(screenshotDir);
                Path screenshotPath = screenshotDir.resolve(safeName + "_" + timestamp + ".png");
                Files.write(screenshotPath, screenshot);
                System.out.println("📸 Screenshot saved: " + screenshotPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("⚠️ Could not save screenshot for test: " + testName + " — " + e.getMessage());
        }
    }
}
