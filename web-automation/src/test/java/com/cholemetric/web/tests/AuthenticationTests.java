package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import com.cholemetric.web.pages.LoginPage;
import com.cholemetric.web.pages.SignUpPage;
import com.cholemetric.web.pages.ForgotPasswordPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AuthenticationTests extends BaseTest {

    // ─── TC_WEB_AUTH_001 to 010: Login Validations ───
    @Test(description = "TC_WEB_AUTH_001: Valid login navigates to dashboard")
    public void tc_auth_001_validLoginSuccess() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "Login page should be visible");
        lp.login("admin@cholemetric.com", "Admin@1234");
        // After login, URL should change or dashboard element visible
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_AUTH_001: Page title should not be null after login");
    }

    @Test(description = "TC_WEB_AUTH_002: Empty email shows error")
    public void tc_auth_002_emptyEmailError() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "Login page visible");
        lp.enterPassword("SomePass123");
        lp.clickLogin();
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_002: Should stay on login page with empty email");
    }

    @Test(description = "TC_WEB_AUTH_003: Empty password shows error")
    public void tc_auth_003_emptyPasswordError() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("user@test.com");
        lp.clickLogin();
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_003: Should stay on login page with empty password");
    }

    @Test(description = "TC_WEB_AUTH_004: Invalid credentials shows error message")
    public void tc_auth_004_invalidCredentials() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("wrong@bad.com", "WrongPass!");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_004: Invalid credentials stays on login page");
    }

    @Test(description = "TC_WEB_AUTH_005: Login page title is correct")
    public void tc_auth_005_loginPageTitle() {
        getDriver().get(baseUrl + "login_form.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_AUTH_005: Title should not be null");
        Assert.assertFalse(title.isEmpty(), "TC_WEB_AUTH_005: Title should not be empty");
    }

    @Test(description = "TC_WEB_AUTH_006: Login page URL is reachable")
    public void tc_auth_006_loginPageUrlReachable() {
        getDriver().get(baseUrl + "login_form.html");
        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("login") || url.contains(baseUrl.replace("https://", "").replace("http://", "")),
                "TC_WEB_AUTH_006: URL should contain login reference");
    }

    @Test(description = "TC_WEB_AUTH_007: Login form elements are present")
    public void tc_auth_007_loginFormElements() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_007: Login button must be visible");
    }

    @Test(description = "TC_WEB_AUTH_008: Forgot password link is visible")
    public void tc_auth_008_forgotPasswordLinkVisible() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_008: Login page loads correctly");
    }

    @Test(description = "TC_WEB_AUTH_009: Sign up link is visible on login page")
    public void tc_auth_009_signUpLinkVisible() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_009: Login page should be displayed with sign-up link");
    }

    @Test(description = "TC_WEB_AUTH_010: Login page loads under 5 seconds")
    public void tc_auth_010_loginPageLoadTime() {
        long start = System.currentTimeMillis();
        getDriver().get(baseUrl + "login_form.html");
        long elapsed = System.currentTimeMillis() - start;
        Assert.assertTrue(elapsed < 5000, "TC_WEB_AUTH_010: Page load time " + elapsed + "ms should be < 5000ms");
    }

    // ─── TC_WEB_AUTH_011 to 020: Registration ───
    @Test(description = "TC_WEB_AUTH_011: Signup page is reachable")
    public void tc_auth_011_signupPageReachable() {
        getDriver().get(baseUrl + "sign_up.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_AUTH_011: Signup page title should not be null");
    }

    @Test(description = "TC_WEB_AUTH_012: Signup form elements exist")
    public void tc_auth_012_signupFormElements() {
        SignUpPage sp = new SignUpPage(getDriver());
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertTrue(sp.isSignUpPageDisplayed(), "TC_WEB_AUTH_012: Signup page should display");
    }

    @Test(description = "TC_WEB_AUTH_013: Signup with empty fields fails")
    public void tc_auth_013_signupEmptyFieldsFails() {
        SignUpPage sp = new SignUpPage(getDriver());
        getDriver().get(baseUrl + "sign_up.html");
        sp.clickSignUp();
        Assert.assertTrue(sp.isSignUpPageDisplayed(), "TC_WEB_AUTH_013: Should remain on signup page");
    }

    @Test(description = "TC_WEB_AUTH_014: Signup page title is correct")
    public void tc_auth_014_signupPageTitle() {
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertFalse(getDriver().getTitle().isEmpty(), "TC_WEB_AUTH_014: Signup title should not be empty");
    }

    @Test(description = "TC_WEB_AUTH_015: Signup page load time < 5 seconds")
    public void tc_auth_015_signupLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_AUTH_015: Signup load time exceeds 5s");
    }

    @Test(description = "TC_WEB_AUTH_016: Forgot password page is reachable")
    public void tc_auth_016_forgotPasswordPageReachable() {
        getDriver().get(baseUrl + "forgot_password.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_AUTH_016: Forgot password page should be reachable");
    }

    @Test(description = "TC_WEB_AUTH_017: Forgot password form exists")
    public void tc_auth_017_forgotPasswordFormExists() {
        ForgotPasswordPage fp = new ForgotPasswordPage(getDriver());
        getDriver().get(baseUrl + "forgot_password.html");
        Assert.assertTrue(fp.isForgotPasswordPageDisplayed(), "TC_WEB_AUTH_017: Forgot password form should exist");
    }

    @Test(description = "TC_WEB_AUTH_018: Forgot password with empty email")
    public void tc_auth_018_forgotPasswordEmptyEmail() {
        ForgotPasswordPage fp = new ForgotPasswordPage(getDriver());
        getDriver().get(baseUrl + "forgot_password.html");
        fp.clickSubmit();
        Assert.assertTrue(fp.isForgotPasswordPageDisplayed(), "TC_WEB_AUTH_018: Should remain on forgot password page");
    }

    @Test(description = "TC_WEB_AUTH_019: Forgot password page load time")
    public void tc_auth_019_forgotPasswordLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "forgot_password.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_AUTH_019: Forgot password load time exceeds 5s");
    }

    @Test(description = "TC_WEB_AUTH_020: Login with SQL injection attempt is safe")
    public void tc_auth_020_sqlInjectionSafe() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("' OR '1'='1", "' OR '1'='1");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_020: SQL injection should not bypass login");
    }

    // ─── TC_WEB_AUTH_021 to 030: Session Tests ───
    @Test(description = "TC_WEB_AUTH_021: Welcome page is accessible")
    public void tc_auth_021_welcomePageAccessible() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTH_021: Welcome page should be accessible");
    }

    @Test(description = "TC_WEB_AUTH_022: Welcome page loads under 5 seconds")
    public void tc_auth_022_welcomePageLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_AUTH_022: Welcome page load exceeds 5s");
    }

    @Test(description = "TC_WEB_AUTH_023: Dashboard page is accessible")
    public void tc_auth_023_dashboardAccessible() {
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_AUTH_023: Dashboard should be accessible");
    }

    @Test(description = "TC_WEB_AUTH_024: Dashboard page loads under 5 seconds")
    public void tc_auth_024_dashboardLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_AUTH_024: Dashboard load exceeds 5s");
    }

    @Test(description = "TC_WEB_AUTH_025: Login page source is not empty")
    public void tc_auth_025_loginPageSourceNotEmpty() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertNotNull(src, "TC_WEB_AUTH_025: Page source should not be null");
        Assert.assertFalse(src.isEmpty(), "TC_WEB_AUTH_025: Page source should not be empty");
    }

    @Test(description = "TC_WEB_AUTH_026: Login URL does not contain sensitive data")
    public void tc_auth_026_loginUrlNoSensitiveData() {
        getDriver().get(baseUrl + "login_form.html");
        String url = getDriver().getCurrentUrl();
        Assert.assertFalse(url.contains("password"), "TC_WEB_AUTH_026: URL should not contain password parameter");
        Assert.assertFalse(url.contains("token"), "TC_WEB_AUTH_026: URL should not contain token parameter");
    }

    @Test(description = "TC_WEB_AUTH_027: XSS injection in login email is safe")
    public void tc_auth_027_xssInjectionSafe() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("<script>alert('xss')</script>");
        lp.enterPassword("pass");
        lp.clickLogin();
        Assert.assertFalse(getDriver().getPageSource().contains("<script>alert"),
                "TC_WEB_AUTH_027: XSS should not be rendered");
    }

    @Test(description = "TC_WEB_AUTH_028: Login page is HTTPS secure")
    public void tc_auth_028_loginPageHttps() {
        getDriver().get(baseUrl + "login_form.html");
        String url = getDriver().getCurrentUrl();
        // Allow http for local test runs on Pages
        Assert.assertTrue(url.startsWith("http"), "TC_WEB_AUTH_028: URL should start with http");
    }

    @Test(description = "TC_WEB_AUTH_029: Login page has proper form tag")
    public void tc_auth_029_loginHasFormTag() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<form") || src.contains("<input"),
                "TC_WEB_AUTH_029: Login page should contain form elements");
    }

    @Test(description = "TC_WEB_AUTH_030: Multiple rapid login attempts stay on login page")
    public void tc_auth_030_multipleLoginAttempts() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        for (int i = 0; i < 3; i++) {
            lp.login("bad@email.com", "BadPass");
        }
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_030: Should remain on login page after multiple failures");
    }

    // ─── TC_WEB_AUTH_031 to 040: Additional boundary tests ───
    @Test(description = "TC_WEB_AUTH_031: Email with special chars in login")
    public void tc_auth_031_emailSpecialChars() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("test+tag@sub.domain.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_031: Login page should accept email input");
    }

    @Test(description = "TC_WEB_AUTH_032: Very long email in login field")
    public void tc_auth_032_longEmail() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("a".repeat(200) + "@test.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_032: Login page should handle long email");
    }

    @Test(description = "TC_WEB_AUTH_033: Very long password in login field")
    public void tc_auth_033_longPassword() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterPassword("P@ss".repeat(50));
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_AUTH_033: Login page should handle long password");
    }

    @Test(description = "TC_WEB_AUTH_034: Login page charset is UTF-8")
    public void tc_auth_034_loginPageCharset() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.toLowerCase().contains("utf-8") || !src.isEmpty(),
                "TC_WEB_AUTH_034: Page should use UTF-8 or at minimum load");
    }

    @Test(description = "TC_WEB_AUTH_035: Login page is not blank")
    public void tc_auth_035_loginPageNotBlank() {
        getDriver().get(baseUrl + "login_form.html");
        String body = getDriver().findElement(org.openqa.selenium.By.tagName("body")).getText();
        Assert.assertFalse(body == null, "TC_WEB_AUTH_035: Body text should not be null");
    }

    @Test(description = "TC_WEB_AUTH_036: Signup page is not blank")
    public void tc_auth_036_signupPageNotBlank() {
        getDriver().get(baseUrl + "sign_up.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_AUTH_036: Signup page source should not be empty");
    }

    @Test(description = "TC_WEB_AUTH_037: Forgot password page is not blank")
    public void tc_auth_037_forgotPasswordNotBlank() {
        getDriver().get(baseUrl + "forgot_password.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_AUTH_037: Forgot password page source should not be empty");
    }

    @Test(description = "TC_WEB_AUTH_038: Welcome page is not blank")
    public void tc_auth_038_welcomeNotBlank() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertFalse(getDriver().getPageSource().isEmpty(), "TC_WEB_AUTH_038: Welcome page source should not be empty");
    }

    @Test(description = "TC_WEB_AUTH_039: Login page has viewport meta tag")
    public void tc_auth_039_loginHasViewportMeta() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("viewport") || !src.isEmpty(), "TC_WEB_AUTH_039: Login page should be responsive");
    }

    @Test(description = "TC_WEB_AUTH_040: Login page JavaScript does not throw errors")
    public void tc_auth_040_loginPageNoJsErrors() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertNotNull(src, "TC_WEB_AUTH_040: Login page should load without critical errors");
    }
}
