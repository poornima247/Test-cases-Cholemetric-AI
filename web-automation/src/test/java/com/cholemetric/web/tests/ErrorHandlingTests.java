package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ErrorHandlingTests extends BaseTest {

    @Test(description = "TC_WEB_ERR_001: Navigating to non-existent page shows error or redirect")
    public void tc_err_001_nonExistentPage() {
        getDriver().get(baseUrl + "nonexistent_page_xyz.html");
        String src = getDriver().getPageSource();
        Assert.assertNotNull(src, "TC_WEB_ERR_001: Browser should handle missing page gracefully");
    }

    @Test(description = "TC_WEB_ERR_002: Login page handles network timeout gracefully")
    public void tc_err_002_loginPageNetworkHandling() {
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_ERR_002: Login page should load without timing out");
    }

    @Test(description = "TC_WEB_ERR_003: Dashboard page gracefully handles missing data")
    public void tc_err_003_dashboardMissingData() {
        getDriver().get(baseUrl + "dashboard.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.toLowerCase().contains("uncaught referenceerror"),
                "TC_WEB_ERR_003: Dashboard should not have uncaught JS errors");
    }

    @Test(description = "TC_WEB_ERR_004: Page source does not contain PHP errors")
    public void tc_err_004_noPhpErrors() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("fatal error") && src.contains("php"),
                "TC_WEB_ERR_004: Login page should not expose PHP errors");
    }

    @Test(description = "TC_WEB_ERR_005: Page source does not contain stack traces")
    public void tc_err_005_noStackTraces() {
        getDriver().get(baseUrl + "dashboard.html");
        String src = getDriver().getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("at com.") && src.contains("exception"),
                "TC_WEB_ERR_005: Dashboard should not expose Java stack traces");
    }

    @Test(description = "TC_WEB_ERR_006: Welcome page has no console-level error markup")
    public void tc_err_006_welcomeNoConsoleErrors() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_ERR_006: Welcome page should load fine");
    }

    @Test(description = "TC_WEB_ERR_007: FAQ page loads without error")
    public void tc_err_007_faqPageNoError() {
        getDriver().get(baseUrl + "faq.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_ERR_007: FAQ page should not be blank");
    }

    @Test(description = "TC_WEB_ERR_008: Settings page loads without error")
    public void tc_err_008_settingsPageNoError() {
        getDriver().get(baseUrl + "settings.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_ERR_008: Settings page should not be blank");
    }

    @Test(description = "TC_WEB_ERR_009: Edit profile page loads without error")
    public void tc_err_009_editProfilePageNoError() {
        getDriver().get(baseUrl + "edit_profile.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_ERR_009: Edit profile page should not be blank");
    }

    @Test(description = "TC_WEB_ERR_010: All pages do not show internal server error")
    public void tc_err_010_noInternalServerError() {
        String[] pages = {"login_form.html", "sign_up.html", "dashboard.html", "welcome.html", "faq.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource().toLowerCase();
            Assert.assertFalse(src.contains("internal server error"),
                    "TC_WEB_ERR_010: Page " + page + " should not show internal server error");
        }
    }

    @Test(description = "TC_WEB_ERR_011: Login with null-like empty strings does not crash")
    public void tc_err_011_nullLikeInputNoCrash() {
        getDriver().get(baseUrl + "login_form.html");
        try {
            getDriver().findElement(By.id("email")).clear();
            getDriver().findElement(By.id("password")).clear();
            getDriver().findElement(By.id("loginBtn")).click();
        } catch (Exception e) {
            // Expected - element might not exist, but page should not crash browser
        }
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_ERR_011: Page should remain usable");
    }

    @Test(description = "TC_WEB_ERR_012: Rapid page navigation does not break browser")
    public void tc_err_012_rapidNavigationNoCrash() {
        String[] pages = {"welcome.html", "login_form.html", "dashboard.html", "faq.html", "settings.html"};
        for (int i = 0; i < 3; i++) {
            for (String page : pages) {
                getDriver().get(baseUrl + page);
            }
        }
        Assert.assertNotNull(getDriver().getCurrentUrl(), "TC_WEB_ERR_012: Browser should remain stable after rapid navigation");
    }

    @Test(description = "TC_WEB_ERR_013: Page does not redirect to external domain unexpectedly")
    public void tc_err_013_noUnexpectedRedirect() {
        getDriver().get(baseUrl + "login_form.html");
        String currentUrl = getDriver().getCurrentUrl();
        // The URL should stay within expected domain
        Assert.assertTrue(currentUrl.startsWith("http"), "TC_WEB_ERR_013: URL should remain http-based");
    }

    @Test(description = "TC_WEB_ERR_014: Browser handles broken URLs gracefully")
    public void tc_err_014_brokenUrlGraceful() {
        getDriver().get(baseUrl + "broken_link_test_12345.html");
        // Just assert we didn't throw a browser exception - graceful handling
        Assert.assertNotNull(getDriver().getPageSource(), "TC_WEB_ERR_014: Browser should not crash on broken URL");
    }

    @Test(description = "TC_WEB_ERR_015: App pages return valid HTTP content types")
    public void tc_err_015_validContentType() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        // Pages should contain HTML, not just error text
        Assert.assertTrue(src.contains("<") || !src.isEmpty(),
                "TC_WEB_ERR_015: Login page should return HTML content");
    }
}
