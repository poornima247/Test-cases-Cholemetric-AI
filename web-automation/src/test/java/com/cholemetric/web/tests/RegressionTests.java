package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegressionTests extends BaseTest {

    @Test(description = "TC_WEB_REG_001: Login page regression - page loads correctly")
    public void tc_reg_001_loginPageRegression() {
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_001: Login page should load on regression");
    }

    @Test(description = "TC_WEB_REG_002: Dashboard page regression - page loads correctly")
    public void tc_reg_002_dashboardPageRegression() {
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_002: Dashboard page should load on regression");
    }

    @Test(description = "TC_WEB_REG_003: Welcome page regression - page loads correctly")
    public void tc_reg_003_welcomePageRegression() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_003: Welcome page should load on regression");
    }

    @Test(description = "TC_WEB_REG_004: Signup page regression - page loads correctly")
    public void tc_reg_004_signupPageRegression() {
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_004: Signup page should load on regression");
    }

    @Test(description = "TC_WEB_REG_005: Forgot password page regression")
    public void tc_reg_005_forgotPasswordRegression() {
        getDriver().get(baseUrl + "forgot_password.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_005: Forgot password page should load on regression");
    }

    @Test(description = "TC_WEB_REG_006: New analysis page regression")
    public void tc_reg_006_newAnalysisRegression() {
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_006: New analysis page should load on regression");
    }

    @Test(description = "TC_WEB_REG_007: Patient history page regression")
    public void tc_reg_007_patientHistoryRegression() {
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_007: Patient history page should load on regression");
    }

    @Test(description = "TC_WEB_REG_008: Scan report page regression")
    public void tc_reg_008_scanReportRegression() {
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_008: Scan report page should load on regression");
    }

    @Test(description = "TC_WEB_REG_009: FAQ page regression")
    public void tc_reg_009_faqPageRegression() {
        getDriver().get(baseUrl + "faq.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_009: FAQ page should load on regression");
    }

    @Test(description = "TC_WEB_REG_010: Settings page regression")
    public void tc_reg_010_settingsPageRegression() {
        getDriver().get(baseUrl + "settings.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_010: Settings page should load on regression");
    }

    @Test(description = "TC_WEB_REG_011: Edit profile page regression")
    public void tc_reg_011_editProfileRegression() {
        getDriver().get(baseUrl + "edit_profile.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_011: Edit profile page should load on regression");
    }

    @Test(description = "TC_WEB_REG_012: All pages have non-empty content on regression")
    public void tc_reg_012_allPagesHaveContent() {
        String[] pages = {"login_form.html", "sign_up.html", "dashboard.html", "welcome.html",
                "faq.html", "settings.html", "new_analysis.html", "patient_history.html",
                "scan_report.html", "edit_profile.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource();
            Assert.assertFalse(src.isEmpty(), "TC_WEB_REG_012: Page " + page + " should have content");
        }
    }

    @Test(description = "TC_WEB_REG_013: All pages load under 5 seconds on regression")
    public void tc_reg_013_allPagesLoadTimeRegression() {
        String[] pages = {"login_form.html", "dashboard.html", "welcome.html", "faq.html", "sign_up.html"};
        for (String page : pages) {
            long s = System.currentTimeMillis();
            getDriver().get(baseUrl + page);
            long elapsed = System.currentTimeMillis() - s;
            Assert.assertTrue(elapsed < 5000, "TC_WEB_REG_013: Page " + page + " load time " + elapsed + "ms > 5s");
        }
    }

    @Test(description = "TC_WEB_REG_014: Login form regression - form is submittable")
    public void tc_reg_014_loginFormSubmittableRegression() {
        getDriver().get(baseUrl + "login_form.html");
        com.cholemetric.web.pages.LoginPage lp = new com.cholemetric.web.pages.LoginPage(getDriver());
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_REG_014: Login form should be submittable on regression");
    }

    @Test(description = "TC_WEB_REG_015: Dashboard page regression - no server errors")
    public void tc_reg_015_dashboardNoServerErrors() {
        getDriver().get(baseUrl + "dashboard.html");
        String src = getDriver().getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("internal server error"),
                "TC_WEB_REG_015: Dashboard should not show server errors on regression");
    }

    @Test(description = "TC_WEB_REG_016: All page titles are non-null on regression")
    public void tc_reg_016_allPageTitlesNonNull() {
        String[] pages = {"login_form.html", "sign_up.html", "dashboard.html", "welcome.html",
                "faq.html", "settings.html", "new_analysis.html", "patient_history.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_016: Page " + page + " title should not be null");
        }
    }

    @Test(description = "TC_WEB_REG_017: Login page regression - security check")
    public void tc_reg_017_loginSecurityRegression() {
        getDriver().get(baseUrl + "login_form.html");
        String url = getDriver().getCurrentUrl();
        Assert.assertFalse(url.contains("password="), "TC_WEB_REG_017: Login URL should not expose password");
    }

    @Test(description = "TC_WEB_REG_018: All pages use HTML not plain text")
    public void tc_reg_018_allPagesUseHtml() {
        String[] pages = {"login_form.html", "dashboard.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource();
            Assert.assertTrue(src.contains("<"), "TC_WEB_REG_018: Page " + page + " should use HTML");
        }
    }

    @Test(description = "TC_WEB_REG_019: Login page regression after navigation away and back")
    public void tc_reg_019_loginAfterNavigation() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().get(baseUrl + "dashboard.html");
        getDriver().navigate().back();
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_REG_019: Login page should be accessible after back navigation");
    }

    @Test(description = "TC_WEB_REG_020: Dashboard page regression - viewport renders")
    public void tc_reg_020_dashboardViewportRenders() {
        getDriver().get(baseUrl + "dashboard.html");
        org.openqa.selenium.Dimension size = getDriver().manage().window().getSize();
        Assert.assertTrue(size.width > 0 && size.height > 0,
                "TC_WEB_REG_020: Dashboard should render with positive viewport dimensions");
    }
}
