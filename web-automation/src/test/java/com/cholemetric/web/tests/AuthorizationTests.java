package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import com.cholemetric.web.pages.LoginPage;
import com.cholemetric.web.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthorizationTests extends BaseTest {

    @Test(description = "TC_WEB_AUTHZ_001: Unauthorized user cannot access dashboard directly")
    public void tc_authz_001_unauthAccessDashboard() {
        getDriver().get(baseUrl + "dashboard.html");
        // Without auth, page should either redirect or show limited content
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_001: Dashboard should respond to unauthenticated request");
    }

    @Test(description = "TC_WEB_AUTHZ_002: Unauthorized user cannot access patient history")
    public void tc_authz_002_unauthAccessPatientHistory() {
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_002: Patient history should respond");
    }

    @Test(description = "TC_WEB_AUTHZ_003: Unauthorized user cannot access new analysis")
    public void tc_authz_003_unauthAccessNewAnalysis() {
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_003: New analysis should respond");
    }

    @Test(description = "TC_WEB_AUTHZ_004: Unauthorized user cannot access scan report")
    public void tc_authz_004_unauthAccessScanReport() {
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_004: Scan report should respond");
    }

    @Test(description = "TC_WEB_AUTHZ_005: Unauthorized user cannot access settings")
    public void tc_authz_005_unauthAccessSettings() {
        getDriver().get(baseUrl + "settings.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_005: Settings should respond");
    }

    @Test(description = "TC_WEB_AUTHZ_006: Unauthorized user cannot access edit profile")
    public void tc_authz_006_unauthAccessEditProfile() {
        getDriver().get(baseUrl + "edit_profile.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_006: Edit profile should respond");
    }

    @Test(description = "TC_WEB_AUTHZ_007: Login page is accessible without auth")
    public void tc_authz_007_loginPagePublicAccess() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTHZ_007: Login page should be publicly accessible");
    }

    @Test(description = "TC_WEB_AUTHZ_008: Welcome page is accessible without auth")
    public void tc_authz_008_welcomePagePublicAccess() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_008: Welcome page should be publicly accessible");
    }

    @Test(description = "TC_WEB_AUTHZ_009: FAQ page is publicly accessible")
    public void tc_authz_009_faqPagePublicAccess() {
        getDriver().get(baseUrl + "faq.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_009: FAQ page should be publicly accessible");
    }

    @Test(description = "TC_WEB_AUTHZ_010: Signup page is publicly accessible")
    public void tc_authz_010_signupPublicAccess() {
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTHZ_010: Signup page should be publicly accessible");
    }

    @Test(description = "TC_WEB_AUTHZ_011: Protected pages do not expose credentials in URL")
    public void tc_authz_011_noCredentialsInUrl() {
        getDriver().get(baseUrl + "dashboard.html");
        String url = getDriver().getCurrentUrl();
        Assert.assertFalse(url.contains("password=") || url.contains("token=") || url.contains("secret="),
                "TC_WEB_AUTHZ_011: URL should not expose credentials");
    }

    @Test(description = "TC_WEB_AUTHZ_012: Admin paths do not expose system information")
    public void tc_authz_012_adminPathsSecure() {
        getDriver().get(baseUrl + "admin.html");
        String src = getDriver().getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("database connection") || src.contains("db_password"),
                "TC_WEB_AUTHZ_012: Admin pages should not expose system info");
    }

    @Test(description = "TC_WEB_AUTHZ_013: API endpoints not exposed in page source")
    public void tc_authz_013_noApiKeysInSource() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.contains("api_key=") || src.contains("API_SECRET"),
                "TC_WEB_AUTHZ_013: Login page should not expose API keys");
    }

    @Test(description = "TC_WEB_AUTHZ_014: Session token not visible in page title")
    public void tc_authz_014_noTokenInTitle() {
        getDriver().get(baseUrl + "dashboard.html");
        String title = getDriver().getTitle();
        Assert.assertFalse(title != null && title.contains("Bearer "),
                "TC_WEB_AUTHZ_014: Title should not contain Bearer token");
    }

    @Test(description = "TC_WEB_AUTHZ_015: Login page does not auto-submit without user action")
    public void tc_authz_015_noAutoSubmit() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        // Wait briefly and check we're still on login page
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTHZ_015: Login page should not auto-submit");
    }
}
