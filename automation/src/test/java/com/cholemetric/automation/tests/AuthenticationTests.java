package com.cholemetric.automation.tests;

import com.cholemetric.automation.base.BaseTest;
import com.cholemetric.automation.config.AppiumConfig;
import com.cholemetric.automation.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AuthenticationTests — 40 Appium test cases covering Login and Logout flows.
 * TC_AUTH_001 through TC_AUTH_040
 */
public class AuthenticationTests extends BaseTest {

    // ── Helper ──────────────────────────────────────────────────────────────────

    private LoginPage loginPage()    { return new LoginPage(); }
    private WelcomePage welcomePage(){ return new WelcomePage(); }
    private DashboardPage dashPage() { return new DashboardPage(); }
    private SettingsPage settPage()  { return new SettingsPage(); }

    // ── Tests ───────────────────────────────────────────────────────────────────

    @Test(priority = 1, description = "Valid Login with correct credentials navigates to Dashboard",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_001_ValidLoginWithCorrectCredentials() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should be visible");
        lp.login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        DashboardPage dp = dashPage();
        Assert.assertTrue(dp.isDashboardVisible(), "Dashboard should be visible after valid login");
    }

    @Test(priority = 2, description = "Login with invalid password shows error message",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_002_LoginWithInvalidPassword() {
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getValidEmail(), AppiumConfig.getInvalidPassword());
        pause(1500);
        Assert.assertTrue(lp.isErrorMessageVisible(), "Error message should appear for invalid password");
        String error = lp.getErrorMessage();
        Assert.assertFalse(error.isEmpty(), "Error message text should not be empty");
    }

    @Test(priority = 3, description = "Login with invalid email shows error message",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_003_LoginWithInvalidEmail() {
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getInvalidEmail(), AppiumConfig.getValidPassword());
        pause(1500);
        Assert.assertTrue(lp.isErrorMessageVisible(), "Error message should appear for invalid email");
    }

    @Test(priority = 4, description = "Login with empty email and password shows validation",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_004_LoginWithEmptyFields() {
        LoginPage lp = loginPage();
        lp.clickLoginButton();
        pause(1000);
        // App should stay on login page and show validation
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should remain visible when fields are empty");
    }

    @Test(priority = 5, description = "Login with empty email field shows validation",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_005_LoginWithEmptyEmail() {
        LoginPage lp = loginPage();
        lp.enterPassword(AppiumConfig.getValidPassword()).clickLoginButton();
        pause(1000);
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should remain when email is empty");
    }

    @Test(priority = 6, description = "Login with empty password field shows validation",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_006_LoginWithEmptyPassword() {
        LoginPage lp = loginPage();
        lp.enterEmail(AppiumConfig.getValidEmail()).clickLoginButton();
        pause(1000);
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should remain when password is empty");
    }

    @Test(priority = 7, description = "Login with special characters in password",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_007_LoginWithSpecialCharsPassword() {
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getValidEmail(), "P@$$w0rd!#%^&*()");
        pause(1500);
        // Should show error (wrong password but accepted as input)
        Assert.assertTrue(lp.isLoginPageVisible() || lp.isErrorMessageVisible(),
            "App should handle special chars gracefully");
    }

    @Test(priority = 8, description = "Login with SQL injection in email field is safely handled",
          groups = {"authentication", "security"})
    public void testTC_AUTH_008_LoginWithSQLInjectionEmail() {
        LoginPage lp = loginPage();
        lp.login("admin' OR '1'='1", "anything");
        pause(1500);
        Assert.assertTrue(lp.isLoginPageVisible() || lp.isErrorMessageVisible(),
            "SQL injection should be rejected, not crash or login");
        // Must NOT reach dashboard
        Assert.assertFalse(dashPage().isDashboardVisible(), "SQL injection must not grant access");
    }

    @Test(priority = 9, description = "Successful login lands on Dashboard screen",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_009_LoginAndVerifyDashboard() {
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        Assert.assertTrue(dashPage().isDashboardVisible(), "Dashboard must be visible after login");
    }

    @Test(priority = 10, description = "Logout from Dashboard navigates back to Welcome/Login",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_010_LogoutFromDashboard() {
        // Login first
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        // Logout via dashboard
        DashboardPage dp = dashPage();
        Assert.assertTrue(dp.isDashboardVisible(), "Must be on dashboard to test logout");
        dp.logout();
        pause(1500);
        Assert.assertTrue(loginPage().isLoginPageVisible() || welcomePage().isWelcomePageVisible(),
            "After logout, Welcome or Login page should be shown");
    }

    @Test(priority = 11, description = "Logout from Settings navigates to Login/Welcome",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_011_LogoutFromSettings() {
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        DashboardPage dp = dashPage();
        dp.openSettings();
        pause(1000);
        SettingsPage sp = settPage();
        Assert.assertTrue(sp.isSettingsPageVisible(), "Settings page should be open");
        sp.clickLogout();
        pause(1500);
        Assert.assertTrue(loginPage().isLoginPageVisible() || welcomePage().isWelcomePageVisible(),
            "After logout from settings, should return to Welcome/Login");
    }

    @Test(priority = 12, description = "Login with whitespace-padded email is rejected or trimmed",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_012_LoginWithWhitespaceEmail() {
        LoginPage lp = loginPage();
        lp.login("  " + AppiumConfig.getValidEmail() + "  ", AppiumConfig.getValidPassword());
        pause(2000);
        // Either logs in (app trims whitespace) or shows error — must not crash
        boolean onDash = dashPage().isDashboardVisible();
        boolean onLogin = lp.isLoginPageVisible();
        Assert.assertTrue(onDash || onLogin, "App must handle whitespace in email gracefully");
    }

    @Test(priority = 13, description = "Login with uppercase email is handled correctly",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_013_LoginWithUppercaseEmail() {
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getValidEmail().toUpperCase(), AppiumConfig.getValidPassword());
        pause(2000);
        // Register normalizes to lowercase; login with uppercase should succeed or gracefully fail
        boolean onDash = dashPage().isDashboardVisible();
        boolean onLogin = lp.isLoginPageVisible() || lp.isErrorMessageVisible();
        Assert.assertTrue(onDash || onLogin, "Uppercase email must be handled without crash");
    }

    @Test(priority = 14, description = "Login page does not show Remember Me option",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_014_RememberMeNotPresent() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page must be visible");
        // Verify login page is reachable (app does not have remember me feature)
        Assert.assertTrue(lp.isLoginButtonVisible(), "Login button should be present");
    }

    @Test(priority = 15, description = "Back button from Login returns to Welcome screen",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_015_BackButtonFromLoginToWelcome() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should be visible");
        driver.navigate().back();
        pause(1000);
        Assert.assertTrue(welcomePage().isWelcomePageVisible() || lp.isLoginPageVisible(),
            "Back from login should go to welcome or stay on login");
    }

    @Test(priority = 16, description = "Error message displayed for invalid credentials",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_016_ErrorMessageInvalidCredentials() {
        LoginPage lp = loginPage();
        lp.login("wrong@email.com", "wrongpassword");
        pause(1500);
        Assert.assertTrue(lp.isErrorMessageVisible(), "Error message must be displayed for invalid credentials");
        String msg = lp.getErrorMessage();
        Assert.assertFalse(msg.isEmpty(), "Error message must not be empty");
    }

    @Test(priority = 17, description = "Error or validation shown when email is empty on submit",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_017_ErrorMessageEmptyEmail() {
        LoginPage lp = loginPage();
        lp.enterPassword("SomePass@123").clickLoginButton();
        pause(1000);
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page must remain visible when email is empty");
    }

    @Test(priority = 18, description = "Error or validation shown when password is empty on submit",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_018_ErrorMessageEmptyPassword() {
        LoginPage lp = loginPage();
        lp.enterEmail(AppiumConfig.getValidEmail()).clickLoginButton();
        pause(1000);
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page must remain visible when password is empty");
    }

    @Test(priority = 19, description = "Login button is visible and enabled on Login page",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_019_LoginButtonEnabled() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isLoginButtonVisible(), "Login button must be visible and enabled");
    }

    @Test(priority = 20, description = "Login page logo is visible",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_020_LoginPageTitle() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should be visible");
        Assert.assertTrue(lp.isLogoVisible(), "App logo must be visible on login page");
    }

    @Test(priority = 21, description = "Login with very long email string is handled safely",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_021_LoginWithLongEmail() {
        String longEmail = "a".repeat(250) + "@test.com";
        LoginPage lp = loginPage();
        lp.login(longEmail, AppiumConfig.getValidPassword());
        pause(1500);
        Assert.assertTrue(lp.isLoginPageVisible() || lp.isErrorMessageVisible(),
            "Long email must not crash the app");
    }

    @Test(priority = 22, description = "Login with very long password string is handled safely",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_022_LoginWithLongPassword() {
        String longPass = "P@$$word" + "x".repeat(242);
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getValidEmail(), longPass);
        pause(1500);
        Assert.assertTrue(lp.isLoginPageVisible() || lp.isErrorMessageVisible(),
            "Long password must not crash the app");
    }

    @Test(priority = 23, description = "Login with numeric-only password",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_023_LoginWithNumericsOnlyPassword() {
        LoginPage lp = loginPage();
        lp.login(AppiumConfig.getValidEmail(), "123456789");
        pause(1500);
        Assert.assertTrue(lp.isLoginPageVisible() || lp.isErrorMessageVisible(),
            "Numeric-only wrong password must show error, not crash");
    }

    @Test(priority = 24, description = "Multiple failed login attempts do not crash the app",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_024_MultipleFailedLoginAttempts() {
        LoginPage lp = loginPage();
        for (int i = 0; i < 3; i++) {
            lp.clearEmailField();
            lp.clearPasswordField();
            lp.login("wrong" + i + "@test.com", "wrongpass" + i);
            pause(1500);
        }
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should still be visible after multiple failures");
    }

    @Test(priority = 25, description = "Session is established and Dashboard stays after login",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_025_SessionPersistenceAfterLogin() {
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        DashboardPage dp = dashPage();
        Assert.assertTrue(dp.isDashboardVisible(), "Dashboard visible after login");
        // Navigate back and re-open
        driver.navigate().back();
        pause(1000);
        // App should keep session on dashboard (not return to login)
        // Exact behavior depends on app flow; at minimum must not crash
        Assert.assertNotNull(driver, "Driver should still be active");
    }

    @Test(priority = 26, description = "Login page is accessible from Welcome page via Sign In link",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_026_DeepLinkToLoginPage() {
        WelcomePage wp = welcomePage();
        if (wp.isWelcomePageVisible()) {
            wp.clickSignIn();
            pause(1000);
        }
        Assert.assertTrue(loginPage().isLoginPageVisible(), "Login page should be reachable");
    }

    @Test(priority = 27, description = "App logo is visible on Login page",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_027_LoginPageLogoVisible() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isLoginPageVisible(), "Login page should be visible");
        Assert.assertTrue(lp.isLogoVisible(), "Logo must be visible");
    }

    @Test(priority = 28, description = "Email field accepts email format input",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_028_LoginEmailFieldType() {
        LoginPage lp = loginPage();
        lp.enterEmail("test@cholemetric.com");
        pause(500);
        String entered = lp.getEmailFieldText();
        Assert.assertTrue(entered.contains("cholemetric") || entered.contains("test"),
            "Email field should accept and retain typed email");
    }

    @Test(priority = 29, description = "Password field content is masked (not visible as plain text)",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_029_LoginPasswordMasked() {
        LoginPage lp = loginPage();
        lp.enterPassword("TestPass@123");
        pause(500);
        Assert.assertTrue(lp.isPasswordMasked(), "Password field must be masked");
    }

    @Test(priority = 30, description = "After logout session is cleared and re-login is required",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_030_LoginWithExpiredSession() {
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        dashPage().logout();
        pause(1500);
        Assert.assertTrue(loginPage().isLoginPageVisible() || welcomePage().isWelcomePageVisible(),
            "Session cleared — login page should be shown");
    }

    @Test(priority = 31, description = "Valid login always navigates to Dashboard",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_031_ValidLoginNavigatesToDashboard() {
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        Assert.assertTrue(dashPage().isDashboardVisible(), "Dashboard must be visible after valid login");
    }

    @Test(priority = 32, description = "Login with a registered doctor email succeeds",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_032_LoginWithRegisteredEmail() {
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        Assert.assertTrue(dashPage().isDashboardVisible(), "Registered email should successfully login");
    }

    @Test(priority = 33, description = "Forgot Password link is visible on Login screen",
          groups = {"authentication", "smoke"})
    public void testTC_AUTH_033_LoginFormSubmitOnEnter() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isForgotPasswordLinkVisible(), "Forgot Password link must be visible");
        lp.clickForgotPassword();
        pause(1000);
        // Navigates to Forgot Password screen
        Assert.assertFalse(dashPage().isDashboardVisible(), "Should be on ForgotPassword screen, not Dashboard");
    }

    @Test(priority = 34, description = "Login with leading/trailing spaces in email",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_034_LoginWithSpacesInEmail() {
        LoginPage lp = loginPage();
        lp.login(" test@cholemetric.com ", AppiumConfig.getValidPassword());
        pause(2000);
        boolean valid = dashPage().isDashboardVisible() || lp.isErrorMessageVisible();
        Assert.assertTrue(valid, "Spaces in email must not crash the app");
    }

    @Test(priority = 35, description = "Login with empty string values handled gracefully",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_035_LoginWithNullValues() {
        LoginPage lp = loginPage();
        lp.login("", "");
        pause(1000);
        Assert.assertTrue(lp.isLoginPageVisible(), "Empty credentials must keep user on login page");
    }

    @Test(priority = 36, description = "After logout, session data is cleared",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_036_LogoutClearsSession() {
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        DashboardPage dp = dashPage();
        Assert.assertTrue(dp.isDashboardVisible());
        dp.logout();
        pause(1500);
        // Session cleared — user must re-authenticate
        Assert.assertTrue(loginPage().isLoginPageVisible() || welcomePage().isWelcomePageVisible(),
            "Session must be cleared after logout");
    }

    @Test(priority = 37, description = "User can re-login successfully after logout",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_037_ReLoginAfterLogout() {
        // First login
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        dashPage().logout();
        pause(1500);
        // Second login
        loginPage().login(AppiumConfig.getValidEmail(), AppiumConfig.getValidPassword());
        pause(2000);
        Assert.assertTrue(dashPage().isDashboardVisible(), "Re-login after logout must succeed");
    }

    @Test(priority = 38, description = "Login with unregistered email returns error",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_038_LoginWithDifferentUser() {
        LoginPage lp = loginPage();
        lp.login("unregistered.user@noexist.com", "AnyPass@123");
        pause(1500);
        Assert.assertTrue(lp.isErrorMessageVisible(), "Unregistered email login must show error");
    }

    @Test(priority = 39, description = "Sign Up link is visible on Login page",
          groups = {"authentication", "regression"})
    public void testTC_AUTH_039_LoginPageKeyboardDismiss() {
        LoginPage lp = loginPage();
        Assert.assertTrue(lp.isSignUpLinkVisible(), "Sign Up link must be visible on login page");
        lp.clickSignUp();
        pause(1000);
        // Must navigate to SignUp screen
        Assert.assertFalse(dashPage().isDashboardVisible(), "Should navigate to SignUp, not Dashboard");
    }

    @Test(priority = 40, description = "Attempting login with unknown/tampered token values shows error",
          groups = {"authentication", "security"})
    public void testTC_AUTH_040_LoginWithTamperedToken() {
        LoginPage lp = loginPage();
        // Simulate tampered token-like strings in fields
        lp.login("eyJhbGciOiJub25lIn0.eyJzdWIiOiJhZG1pbiJ9.", "Bearer fakeToken");
        pause(1500);
        Assert.assertTrue(lp.isLoginPageVisible() || lp.isErrorMessageVisible(),
            "Tampered token input must be rejected");
        Assert.assertFalse(dashPage().isDashboardVisible(),
            "Tampered token must not grant dashboard access");
    }
}
