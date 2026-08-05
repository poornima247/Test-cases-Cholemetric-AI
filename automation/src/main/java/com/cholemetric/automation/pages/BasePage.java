package com.cholemetric.automation.pages;

import com.cholemetric.automation.config.AppiumConfig;
import com.cholemetric.automation.drivers.DriverManager;
import com.cholemetric.automation.utils.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * BasePage — Foundation for all Page Object classes.
 * Provides common Appium interaction methods.
 */
public abstract class BasePage {

    protected final Logger log;
    protected final AndroidDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;

    protected BasePage() {
        this.log = LoggerFactory.getLogger(getClass());
        this.driver = DriverManager.getDriver();
        int explicitWait = AppiumConfig.getExplicitWait();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ── Element Finders ───────────────────────────────────────────────────────

    protected WebElement findById(String resourceId) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.id(resourceId)));
    }

    protected WebElement findByText(String text) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[@text='" + text + "']")));
    }

    protected WebElement findByContainsText(String partial) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[contains(@text,'" + partial + "')]")));
    }

    protected WebElement findByXpath(String xpath) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath(xpath)));
    }

    protected WebElement findByClass(String className) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className(className)));
    }

    protected List<WebElement> findAllById(String resourceId) {
        return driver.findElements(By.id(resourceId));
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    protected void tap(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        log.debug("Tapped element: {}", element);
    }

    protected void tapById(String resourceId) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
            By.id(resourceId)));
        el.click();
    }

    protected void tapByText(String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[@text='" + text + "']")));
        el.click();
    }

    protected void typeText(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    protected void typeTextById(String resourceId, String text) {
        WebElement el = findById(resourceId);
        el.clear();
        el.sendKeys(text);
    }

    protected void clearField(WebElement element) {
        element.clear();
    }

    protected void pressBack() {
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
        log.debug("Pressed BACK key");
    }

    protected void pressHome() {
        driver.pressKey(new KeyEvent(AndroidKey.HOME));
    }

    // ── Text Getters ──────────────────────────────────────────────────────────

    protected String getText(WebElement element) {
        return element.getText();
    }

    protected String getTextById(String resourceId) {
        return findById(resourceId).getText();
    }

    // ── Visibility Checks ─────────────────────────────────────────────────────

    protected boolean isVisible(By locator) {
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isVisibleById(String resourceId) {
        return isVisible(By.id(resourceId));
    }

    protected boolean isVisibleByText(String text) {
        return isVisible(By.xpath("//*[@text='" + text + "']"));
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ── Wait Methods ──────────────────────────────────────────────────────────

    protected void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void waitForVisibilityById(String resourceId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(resourceId)));
    }

    protected void waitForInvisibility(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitSeconds(int seconds) {
        try { Thread.sleep(seconds * 1000L); } catch (InterruptedException ignored) {}
    }

    // ── Scroll Methods ────────────────────────────────────────────────────────

    protected void scrollDown() {
        Dimension size = driver.manage().window().getSize();
        int startY = (int) (size.getHeight() * 0.8);
        int endY   = (int) (size.getHeight() * 0.2);
        int centerX = size.getWidth() / 2;
        performSwipe(centerX, startY, centerX, endY);
    }

    protected void scrollUp() {
        Dimension size = driver.manage().window().getSize();
        int startY = (int) (size.getHeight() * 0.2);
        int endY   = (int) (size.getHeight() * 0.8);
        int centerX = size.getWidth() / 2;
        performSwipe(centerX, startY, centerX, endY);
    }

    protected void scrollToText(String text) {
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView("
                + "new UiSelector().text(\"" + text + "\"))"));
        } catch (Exception e) {
            log.warn("scrollToText failed for '{}': {}", text, e.getMessage());
        }
    }

    private void performSwipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(swipe));
    }

    // ── Screenshot ────────────────────────────────────────────────────────────

    protected String takeScreenshot(String name) {
        return ScreenshotUtil.capture(driver, name);
    }

    // ── Activity Check ────────────────────────────────────────────────────────

    protected String getCurrentActivity() {
        return driver.currentActivity();
    }

    protected boolean isOnActivity(String activityName) {
        try {
            return driver.currentActivity().contains(activityName);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Toast / Snackbar ──────────────────────────────────────────────────────

    protected boolean isToastVisible(String message) {
        try {
            WebElement toast = driver.findElement(
                By.xpath("//*[@class='android.widget.Toast' and @text='" + message + "']"));
            return toast != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Appium By (inner class to avoid external import issues)
    protected static class AppiumBy {
        public static By androidUIAutomator(String uiautomatorText) {
            return io.appium.java_client.AppiumBy.androidUIAutomator(uiautomatorText);
        }
    }
}
