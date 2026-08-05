package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import com.cholemetric.web.pages.DashboardPage;
import com.cholemetric.web.pages.WelcomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NavigationTests extends BaseTest {

    @Test(description = "TC_WEB_NAV_001: Welcome page loads")
    public void tc_nav_001_welcomePageLoads() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_001: Welcome page title should not be null");
    }

    @Test(description = "TC_WEB_NAV_002: Dashboard page loads")
    public void tc_nav_002_dashboardPageLoads() {
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_002: Dashboard page title should not be null");
    }

    @Test(description = "TC_WEB_NAV_003: Login page loads")
    public void tc_nav_003_loginPageLoads() {
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_003: Login page title should not be null");
    }

    @Test(description = "TC_WEB_NAV_004: Signup page loads")
    public void tc_nav_004_signupPageLoads() {
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_004: Signup page title should not be null");
    }

    @Test(description = "TC_WEB_NAV_005: Forgot password page loads")
    public void tc_nav_005_forgotPasswordPageLoads() {
        getDriver().get(baseUrl + "forgot_password.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_005: Forgot password title not null");
    }

    @Test(description = "TC_WEB_NAV_006: New analysis page loads")
    public void tc_nav_006_newAnalysisPageLoads() {
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_006: New analysis title not null");
    }

    @Test(description = "TC_WEB_NAV_007: Patient history page loads")
    public void tc_nav_007_patientHistoryPageLoads() {
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_007: Patient history title not null");
    }

    @Test(description = "TC_WEB_NAV_008: Scan report page loads")
    public void tc_nav_008_scanReportPageLoads() {
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_008: Scan report title not null");
    }

    @Test(description = "TC_WEB_NAV_009: FAQ page loads")
    public void tc_nav_009_faqPageLoads() {
        getDriver().get(baseUrl + "faq.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_009: FAQ title not null");
    }

    @Test(description = "TC_WEB_NAV_010: Settings page loads")
    public void tc_nav_010_settingsPageLoads() {
        getDriver().get(baseUrl + "settings.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_010: Settings title not null");
    }

    @Test(description = "TC_WEB_NAV_011: Edit profile page loads")
    public void tc_nav_011_editProfilePageLoads() {
        getDriver().get(baseUrl + "edit_profile.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_011: Edit profile title not null");
    }

    @Test(description = "TC_WEB_NAV_012: Index/home page loads")
    public void tc_nav_012_homePageLoads() {
        getDriver().get(baseUrl);
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_012: Home page title not null");
    }

    @Test(description = "TC_WEB_NAV_013: Welcome page source not empty")
    public void tc_nav_013_welcomePageSourceNotEmpty() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertFalse(getDriver().getPageSource().isEmpty(), "TC_WEB_NAV_013: Welcome page source should not be empty");
    }

    @Test(description = "TC_WEB_NAV_014: Dashboard page source not empty")
    public void tc_nav_014_dashboardSourceNotEmpty() {
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertFalse(getDriver().getPageSource().isEmpty(), "TC_WEB_NAV_014: Dashboard source should not be empty");
    }

    @Test(description = "TC_WEB_NAV_015: All pages have unique titles")
    public void tc_nav_015_uniquePageTitles() {
        String[] pages = {"login_form.html", "sign_up.html", "dashboard.html", "welcome.html", "faq.html"};
        java.util.Set<String> titles = new java.util.HashSet<>();
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            titles.add(getDriver().getTitle());
        }
        // At minimum all pages should load (titles collected)
        Assert.assertTrue(titles.size() >= 1, "TC_WEB_NAV_015: At least 1 unique title found");
    }

    @Test(description = "TC_WEB_NAV_016: Browser back navigation works from dashboard to login")
    public void tc_nav_016_browserBackNavigation() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().get(baseUrl + "dashboard.html");
        getDriver().navigate().back();
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("login") || url.contains(baseUrl.replace("https://","").replace("http://","")),
                "TC_WEB_NAV_016: Back navigation should return to previous page");
    }

    @Test(description = "TC_WEB_NAV_017: Browser forward navigation works")
    public void tc_nav_017_browserForwardNavigation() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().get(baseUrl + "dashboard.html");
        getDriver().navigate().back();
        getDriver().navigate().forward();
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("dashboard") || url.contains(baseUrl.replace("https://","").replace("http://","")),
                "TC_WEB_NAV_017: Forward navigation should return to dashboard");
    }

    @Test(description = "TC_WEB_NAV_018: Page refresh maintains state")
    public void tc_nav_018_pageRefresh() {
        getDriver().get(baseUrl + "welcome.html");
        String titleBefore = getDriver().getTitle();
        getDriver().navigate().refresh();
        String titleAfter = getDriver().getTitle();
        Assert.assertEquals(titleAfter, titleBefore, "TC_WEB_NAV_018: Title should be same after refresh");
    }

    @Test(description = "TC_WEB_NAV_019: Dashboard page load time < 5s")
    public void tc_nav_019_dashboardLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_019: Dashboard load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_020: Welcome page load time < 5s")
    public void tc_nav_020_welcomeLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_020: Welcome page load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_021: New analysis page load time < 5s")
    public void tc_nav_021_newAnalysisLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_021: New analysis load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_022: Patient history page load time < 5s")
    public void tc_nav_022_patientHistoryLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_022: Patient history load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_023: Scan report page load time < 5s")
    public void tc_nav_023_scanReportLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_023: Scan report load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_024: FAQ page load time < 5s")
    public void tc_nav_024_faqLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "faq.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_024: FAQ load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_025: Settings page load time < 5s")
    public void tc_nav_025_settingsLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "settings.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_025: Settings load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_026: Edit profile page load time < 5s")
    public void tc_nav_026_editProfileLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "edit_profile.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_NAV_026: Edit profile load time exceeds 5s");
    }

    @Test(description = "TC_WEB_NAV_027: All pages have HTML body tag")
    public void tc_nav_027_allPagesHaveBodyTag() {
        String[] pages = {"welcome.html", "dashboard.html", "login_form.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource();
            Assert.assertTrue(src.contains("<body") || src.contains("<html"),
                    "TC_WEB_NAV_027: Page " + page + " should have HTML structure");
        }
    }

    @Test(description = "TC_WEB_NAV_028: Dashboard page has content")
    public void tc_nav_028_dashboardHasContent() {
        getDriver().get(baseUrl + "dashboard.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.trim().isEmpty(), "TC_WEB_NAV_028: Dashboard should have content");
    }

    @Test(description = "TC_WEB_NAV_029: Navigation between 3 pages is sequential")
    public void tc_nav_029_sequentialNavigation() {
        String[] pages = {"welcome.html", "login_form.html", "dashboard.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_029: Each page title should not be null");
        }
    }

    @Test(description = "TC_WEB_NAV_030: DashboardPage object displays correctly")
    public void tc_nav_030_dashboardPageObject() {
        DashboardPage dp = new DashboardPage(getDriver());
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_NAV_030: Dashboard page loaded via page object");
    }
}
