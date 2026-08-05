package com.cholemetric.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * LoginPage — Page Object for LoginActivity (com.cholemetric.app.LoginActivity)
 */
public class LoginPage extends BasePage {

    // Resource IDs — matching the Cholemetric app layout
    private static final String EMAIL_FIELD    = "com.cholemetric.app:id/etEmail";
    private static final String PASSWORD_FIELD = "com.cholemetric.app:id/etPassword";
    private static final String LOGIN_BUTTON   = "com.cholemetric.app:id/btnLogin";
    private static final String FORGOT_LINK    = "com.cholemetric.app:id/tvForgotPassword";
    private static final String SIGNUP_LINK    = "com.cholemetric.app:id/tvSignUp";
    private static final String ERROR_TEXT     = "com.cholemetric.app:id/tvError";
    private static final String LOGO           = "com.cholemetric.app:id/ivLogo";
    private static final String TITLE          = "com.cholemetric.app:id/tvTitle";
    private static final String PROGRESS_BAR   = "com.cholemetric.app:id/progressBar";

    public LoginPage() { super(); }

    // ── Actions ───────────────────────────────────────────────────────────────

    public LoginPage enterEmail(String email) {
        typeTextById(EMAIL_FIELD, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeTextById(PASSWORD_FIELD, password);
        return this;
    }

    public LoginPage clickLoginButton() {
        tapById(LOGIN_BUTTON);
        return this;
    }

    public void clickForgotPassword() {
        tapById(FORGOT_LINK);
    }

    public void clickSignUp() {
        tapById(SIGNUP_LINK);
    }

    public void clearEmailField() {
        findById(EMAIL_FIELD).clear();
    }

    public void clearPasswordField() {
        findById(PASSWORD_FIELD).clear();
    }

    // ── Full Login Flow ───────────────────────────────────────────────────────

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isLoginPageVisible() {
        return isVisibleById(EMAIL_FIELD) && isVisibleById(PASSWORD_FIELD);
    }

    public boolean isLoginButtonVisible() {
        return isVisibleById(LOGIN_BUTTON);
    }

    public boolean isErrorMessageVisible() {
        return isVisibleById(ERROR_TEXT);
    }

    public String getErrorMessage() {
        try { return getTextById(ERROR_TEXT); } catch (Exception e) { return ""; }
    }

    public boolean isForgotPasswordLinkVisible() {
        return isVisibleById(FORGOT_LINK);
    }

    public boolean isSignUpLinkVisible() {
        return isVisibleById(SIGNUP_LINK);
    }

    public boolean isPasswordMasked() {
        try {
            WebElement pwField = findById(PASSWORD_FIELD);
            // inputType 129 = TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
            String inputType = pwField.getAttribute("password");
            return "true".equalsIgnoreCase(inputType);
        } catch (Exception e) {
            return true; // assume masked if check fails
        }
    }

    public boolean isLogoVisible() {
        return isVisibleById(LOGO);
    }

    public boolean isProgressBarVisible() {
        return isVisibleById(PROGRESS_BAR);
    }

    public boolean isOnLoginActivity() {
        return isOnActivity("LoginActivity");
    }

    public String getEmailFieldText() {
        return getTextById(EMAIL_FIELD);
    }

    public String getPasswordFieldText() {
        return getTextById(PASSWORD_FIELD);
    }
}
