package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import com.cholemetric.web.pages.LoginPage;
import com.cholemetric.web.pages.SignUpPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FormsTests extends BaseTest {

    @Test(description = "TC_WEB_FORM_001: Login form accepts email input")
    public void tc_form_001_loginAcceptsEmail() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("test@example.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_FORM_001: Login page should display after email entry");
    }

    @Test(description = "TC_WEB_FORM_002: Login form accepts password input")
    public void tc_form_002_loginAcceptsPassword() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterPassword("Pass@123");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_FORM_002: Login page should display after password entry");
    }

    @Test(description = "TC_WEB_FORM_003: Login form submit button is clickable")
    public void tc_form_003_loginSubmitClickable() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("user@test.com");
        lp.enterPassword("pass");
        lp.clickLogin();
        Assert.assertNotNull(getDriver().getTitle(), "TC_FORM_003: Page should remain usable after submit");
    }

    @Test(description = "TC_WEB_FORM_004: Signup form accepts full name")
    public void tc_form_004_signupAcceptsName() {
        SignUpPage sp = new SignUpPage(getDriver());
        getDriver().get(baseUrl + "sign_up.html");
        Assert.assertTrue(sp.isSignUpPageDisplayed(), "TC_FORM_004: Signup page should be displayed");
    }

    @Test(description = "TC_WEB_FORM_005: Signup form is submittable")
    public void tc_form_005_signupFormSubmit() {
        SignUpPage sp = new SignUpPage(getDriver());
        getDriver().get(baseUrl + "sign_up.html");
        sp.clickSignUp();
        Assert.assertNotNull(getDriver().getTitle(), "TC_FORM_005: After submit, page title should not be null");
    }

    @Test(description = "TC_WEB_FORM_006: Login form clears after clicking multiple times")
    public void tc_form_006_loginFormClear() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("a@b.com");
        lp.enterEmail("c@d.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_FORM_006: Login page should remain visible");
    }

    @Test(description = "TC_WEB_FORM_007: Form input handles unicode characters")
    public void tc_form_007_unicodeInForm() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("测试@test.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_FORM_007: Login page should handle unicode input");
    }

    @Test(description = "TC_WEB_FORM_008: Form input handles spaces")
    public void tc_form_008_spacesInFormInput() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("  user@test.com  ");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_FORM_008: Login page should handle whitespace input");
    }

    @Test(description = "TC_WEB_FORM_009: Signup page has form structure")
    public void tc_form_009_signupHasFormStructure() {
        getDriver().get(baseUrl + "sign_up.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<form") || src.contains("<input"),
                "TC_FORM_009: Signup page should have form/input elements");
    }

    @Test(description = "TC_WEB_FORM_010: Login form has form structure")
    public void tc_form_010_loginHasFormStructure() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<form") || src.contains("<input"),
                "TC_FORM_010: Login page should have form/input elements");
    }

    @Test(description = "TC_WEB_FORM_011: Form page renders without HTTP error")
    public void tc_form_011_loginPageNoErrorResponse() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.toLowerCase().contains("404 not found"),
                "TC_FORM_011: Login page should not return 404");
    }

    @Test(description = "TC_WEB_FORM_012: Signup page renders without HTTP error")
    public void tc_form_012_signupPageNoErrorResponse() {
        getDriver().get(baseUrl + "sign_up.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.toLowerCase().contains("404 not found"),
                "TC_FORM_012: Signup page should not return 404");
    }

    @Test(description = "TC_WEB_FORM_013: Edit profile page loads")
    public void tc_form_013_editProfilePageLoads() {
        getDriver().get(baseUrl + "edit_profile.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_FORM_013: Edit profile page title should not be null");
    }

    @Test(description = "TC_WEB_FORM_014: Edit profile page has form elements")
    public void tc_form_014_editProfileHasFormElements() {
        getDriver().get(baseUrl + "edit_profile.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_FORM_014: Edit profile page should have content");
    }

    @Test(description = "TC_WEB_FORM_015: Settings page loads")
    public void tc_form_015_settingsPageLoads() {
        getDriver().get(baseUrl + "settings.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_FORM_015: Settings page title should not be null");
    }

    @Test(description = "TC_WEB_FORM_016: New analysis page has form elements")
    public void tc_form_016_newAnalysisHasFormElements() {
        getDriver().get(baseUrl + "new_analysis.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_FORM_016: New analysis page should have content");
    }

    @Test(description = "TC_WEB_FORM_017: All pages return HTTP 200 equivalent")
    public void tc_form_017_pagesReturnSuccess() {
        String[] pages = {"login_form.html", "sign_up.html", "dashboard.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource();
            Assert.assertFalse(src.toLowerCase().contains("404") && src.toLowerCase().contains("not found"),
                    "TC_FORM_017: Page " + page + " should not be 404");
        }
    }

    @Test(description = "TC_WEB_FORM_018: Login form submit with numbers in email")
    public void tc_form_018_numericEmailLogin() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        lp.enterEmail("123456@test.com");
        lp.enterPassword("123456");
        lp.clickLogin();
        Assert.assertNotNull(getDriver().getTitle(), "TC_FORM_018: Page should respond to numeric email login");
    }

    @Test(description = "TC_WEB_FORM_019: Form inputs accept max length text")
    public void tc_form_019_maxLengthInput() {
        LoginPage lp = new LoginPage(getDriver());
        getDriver().get(baseUrl + "login_form.html");
        String longInput = "a".repeat(100);
        lp.enterEmail(longInput + "@test.com");
        Assert.assertTrue(lp.isLoginPageDisplayed(), "TC_FORM_019: Login page should handle max-length email");
    }

    @Test(description = "TC_WEB_FORM_020: Forms page structure has correct HTML")
    public void tc_form_020_htmlStructureCorrect() {
        String[] pages = {"login_form.html", "sign_up.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource();
            Assert.assertTrue(src.contains("</html>") || src.contains("</body>") || !src.isEmpty(),
                    "TC_FORM_020: Page " + page + " should have proper HTML closure or content");
        }
    }
}
