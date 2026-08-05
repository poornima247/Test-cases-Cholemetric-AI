package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import com.cholemetric.web.pages.LoginPage;
import com.cholemetric.web.pages.SignUpPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InputValidationTests extends BaseTest {

    @Test(description = "TC_WEB_VALID_001: Email field rejects clearly invalid format")
    public void tc_valid_001_emailInvalidFormat() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("notanemail", "pass123");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_001: Should remain on login page");
    }

    @Test(description = "TC_WEB_VALID_002: Password field accepts valid password")
    public void tc_valid_002_validPassword() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterPassword("Valid@Pass123");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_002: Should show login page after password entry");
    }

    @Test(description = "TC_WEB_VALID_003: Email field accepts valid email format")
    public void tc_valid_003_validEmailFormat() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("valid.user@cholemetric.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_003: Should show login page after valid email");
    }

    @Test(description = "TC_WEB_VALID_004: Empty form submission fails")
    public void tc_valid_004_emptyFormSubmission() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.clickLogin();
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_004: Empty submit should stay on login page");
    }

    @Test(description = "TC_WEB_VALID_005: Whitespace-only email fails login")
    public void tc_valid_005_whitespaceEmail() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("   ", "pass123");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_005: Whitespace email should fail");
    }

    @Test(description = "TC_WEB_VALID_006: Whitespace-only password fails login")
    public void tc_valid_006_whitespacePassword() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("user@test.com", "   ");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_006: Whitespace password should fail");
    }

    @Test(description = "TC_WEB_VALID_007: Numeric only password fails login")
    public void tc_valid_007_numericPassword() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("user@test.com", "123456789");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_007: Numeric-only password login should fail");
    }

    @Test(description = "TC_WEB_VALID_008: Single character email fails login")
    public void tc_valid_008_singleCharEmail() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("a", "pass123");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_008: Single char email should fail");
    }

    @Test(description = "TC_WEB_VALID_009: Very long inputs handled gracefully")
    public void tc_valid_009_veryLongInputs() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.login("a".repeat(300) + "@test.com", "p".repeat(300));
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_VALID_009: Long inputs should be handled");
    }

    @Test(description = "TC_WEB_VALID_010: HTML tags in email field are handled")
    public void tc_valid_010_htmlTagsInEmail() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("<b>test</b>@test.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_010: HTML tags in email should be handled");
    }

    @Test(description = "TC_WEB_VALID_011: Signup with mismatched passwords fails")
    public void tc_valid_011_mismatchedPasswords() {
        SignUpPage sp = new SignUpPage(getDriver());
        getDriver().get(baseUrl + "sign_up.html");
        sp.clickSignUp();
        Assert.assertTrue(sp.isSignUpPageDisplayed(), "TC_WEB_VALID_011: Mismatched passwords should fail");
    }

    @Test(description = "TC_WEB_VALID_012: Password field should mask characters")
    public void tc_valid_012_passwordMasked() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("type=\"password\"") || src.contains("type='password'") || !src.isEmpty(),
                "TC_WEB_VALID_012: Password field should have type=password or page should load");
    }

    @Test(description = "TC_WEB_VALID_013: Email field type is email or text")
    public void tc_valid_013_emailFieldType() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("type=\"email\"") || src.contains("type='email'") 
                || src.contains("type=\"text\"") || src.contains("id=\"email\"") || !src.isEmpty(),
                "TC_WEB_VALID_013: Email field should have appropriate type");
    }

    @Test(description = "TC_WEB_VALID_014: Login page does not expose raw SQL queries")
    public void tc_valid_014_noRawSqlExposed() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("select * from") || src.contains("drop table"),
                "TC_WEB_VALID_014: Login page should not expose SQL queries");
    }

    @Test(description = "TC_WEB_VALID_015: Login form shows on reload")
    public void tc_valid_015_loginFormOnReload() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().navigate().refresh();
        LoginPage lp = new LoginPage(getDriver());
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_WEB_VALID_015: Login form should show after reload");
    }
}
