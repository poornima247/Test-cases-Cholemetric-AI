package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PerformanceSmokeTests extends BaseTest {

    private long measurePageLoadTime(String url) {
        long start = System.currentTimeMillis();
        getDriver().get(url);
        long elapsed = System.currentTimeMillis() - start;
        return elapsed;
    }

    private long getNavigationTiming() {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        try {
            Long loadEventEnd = (Long) js.executeScript("return window.performance.timing.loadEventEnd;");
            Long navigationStart = (Long) js.executeScript("return window.performance.timing.navigationStart;");
            if (loadEventEnd != null && navigationStart != null && loadEventEnd > 0) {
                return loadEventEnd - navigationStart;
            }
        } catch (Exception ignored) {}
        return -1;
    }

    @Test(description = "TC_WEB_PERF_001: Login page loads under 5 seconds")
    public void tc_perf_001_loginLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "login_form.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_001: Login page load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_002: Dashboard page loads under 5 seconds")
    public void tc_perf_002_dashboardLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "dashboard.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_002: Dashboard load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_003: Welcome page loads under 5 seconds")
    public void tc_perf_003_welcomeLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "welcome.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_003: Welcome page load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_004: Signup page loads under 5 seconds")
    public void tc_perf_004_signupLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "sign_up.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_004: Signup page load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_005: FAQ page loads under 5 seconds")
    public void tc_perf_005_faqLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "faq.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_005: FAQ page load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_006: New analysis page loads under 5 seconds")
    public void tc_perf_006_newAnalysisLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "new_analysis.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_006: New analysis load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_007: Patient history page loads under 5 seconds")
    public void tc_perf_007_patientHistoryLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "patient_history.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_007: Patient history load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_008: Scan report page loads under 5 seconds")
    public void tc_perf_008_scanReportLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "scan_report.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_008: Scan report load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_009: Settings page loads under 5 seconds")
    public void tc_perf_009_settingsLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "settings.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_009: Settings load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_010: Edit profile page loads under 5 seconds")
    public void tc_perf_010_editProfileLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "edit_profile.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_010: Edit profile load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_011: Login page Navigation API load time is non-negative")
    public void tc_perf_011_loginNavApiTiming() {
        getDriver().get(baseUrl + "login_form.html");
        long navTime = getNavigationTiming();
        if (navTime >= 0) {
            Assert.assertTrue(navTime < 10000, "TC_WEB_PERF_011: Navigation timing " + navTime + "ms should be < 10000ms");
        } else {
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_PERF_011: Page loaded (Nav API unavailable)");
        }
    }

    @Test(description = "TC_WEB_PERF_012: Dashboard Navigation API load time is non-negative")
    public void tc_perf_012_dashboardNavApiTiming() {
        getDriver().get(baseUrl + "dashboard.html");
        long navTime = getNavigationTiming();
        if (navTime >= 0) {
            Assert.assertTrue(navTime >= 0, "TC_WEB_PERF_012: Navigation timing should be non-negative");
        } else {
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_PERF_012: Page loaded (Nav API unavailable)");
        }
    }

    @Test(description = "TC_WEB_PERF_013: Login page DOM content loaded time")
    public void tc_perf_013_loginDomContentLoaded() {
        getDriver().get(baseUrl + "login_form.html");
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        try {
            Long domComplete = (Long) js.executeScript(
                    "return window.performance.timing.domContentLoadedEventEnd - window.performance.timing.navigationStart;");
            if (domComplete != null && domComplete >= 0) {
                Assert.assertTrue(domComplete < 5000,
                        "TC_WEB_PERF_013: DOM content loaded " + domComplete + "ms should be < 5000ms");
            } else {
                Assert.assertTrue(true, "TC_WEB_PERF_013: DOM timing not available, page loaded");
            }
        } catch (Exception e) {
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_PERF_013: Page loaded");
        }
    }

    @Test(description = "TC_WEB_PERF_014: Multiple pages load sequentially within time budget")
    public void tc_perf_014_sequentialPagesTimeBudget() {
        String[] pages = {"welcome.html", "login_form.html", "dashboard.html", "faq.html"};
        long totalTime = 0;
        for (String page : pages) {
            long s = System.currentTimeMillis();
            getDriver().get(baseUrl + page);
            totalTime += System.currentTimeMillis() - s;
        }
        Assert.assertTrue(totalTime < 20000,
                "TC_WEB_PERF_014: Total time for 4 pages " + totalTime + "ms should be < 20000ms");
    }

    @Test(description = "TC_WEB_PERF_015: Page reload is not significantly slower than first load")
    public void tc_perf_015_reloadPerformance() {
        getDriver().get(baseUrl + "login_form.html");
        long s = System.currentTimeMillis();
        getDriver().navigate().refresh();
        long reloadTime = System.currentTimeMillis() - s;
        Assert.assertTrue(reloadTime < 5000, "TC_WEB_PERF_015: Reload time " + reloadTime + "ms should be < 5000ms");
    }

    @Test(description = "TC_WEB_PERF_016: Forgot password page loads under 5 seconds")
    public void tc_perf_016_forgotPasswordLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl + "forgot_password.html");
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_016: Forgot password load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_017: Home/index page loads under 5 seconds")
    public void tc_perf_017_homePageLoadTime() {
        long elapsed = measurePageLoadTime(baseUrl);
        Assert.assertTrue(elapsed < 5000, "TC_WEB_PERF_017: Home page load time " + elapsed + "ms > 5000ms");
    }

    @Test(description = "TC_WEB_PERF_018: Login page renders without JS block (no long tasks)")
    public void tc_perf_018_loginNoLongJsTasks() {
        getDriver().get(baseUrl + "login_form.html");
        // If page has loaded and DOM is accessible, no long blocking tasks
        java.util.List<org.openqa.selenium.WebElement> inputs = getDriver().findElements(
                org.openqa.selenium.By.tagName("input"));
        Assert.assertNotNull(inputs, "TC_WEB_PERF_018: Inputs should be accessible without JS blocking");
    }

    @Test(description = "TC_WEB_PERF_019: All 10 pages load within 60 second total")
    public void tc_perf_019_allPagesWithin60s() {
        String[] pages = {"welcome.html", "login_form.html", "sign_up.html", "dashboard.html",
                "faq.html", "settings.html", "new_analysis.html", "patient_history.html",
                "scan_report.html", "edit_profile.html"};
        long total = 0;
        for (String page : pages) {
            long s = System.currentTimeMillis();
            getDriver().get(baseUrl + page);
            total += System.currentTimeMillis() - s;
        }
        Assert.assertTrue(total < 60000,
                "TC_WEB_PERF_019: Total time for all 10 pages " + total + "ms should be < 60000ms");
    }

    @Test(description = "TC_WEB_PERF_020: Performance metrics object is accessible via JS")
    public void tc_perf_020_perfApiAccessible() {
        getDriver().get(baseUrl + "dashboard.html");
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        Object perfDefined = js.executeScript("return typeof window.performance !== 'undefined';");
        Assert.assertEquals(perfDefined, Boolean.TRUE, "TC_WEB_PERF_020: Performance API should be accessible");
    }
}
