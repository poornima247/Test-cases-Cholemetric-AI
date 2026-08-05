package com.cholemetric.automation.pages;

/**
 * WelcomePage — Page Object for WelcomeActivity
 */
public class WelcomePage extends BasePage {
    private static final String WELCOME_TITLE  = "com.cholemetric.app:id/tvWelcomeTitle";
    private static final String WELCOME_DESC   = "com.cholemetric.app:id/tvWelcomeDescription";
    private static final String BTN_LOGIN      = "com.cholemetric.app:id/btnLogin";
    private static final String BTN_SIGNUP     = "com.cholemetric.app:id/btnSignUp";
    private static final String LOGO           = "com.cholemetric.app:id/ivLogo";

    public WelcomePage() { super(); }

    public void clickLoginButton()  { tapById(BTN_LOGIN); }
    public void clickSignUpButton() { tapById(BTN_SIGNUP); }

    /** Alias for clickLoginButton() — used by test classes */
    public void clickSignIn() { clickLoginButton(); }

    public boolean isWelcomeTitleVisible()   { return isVisibleById(WELCOME_TITLE); }
    public boolean isLoginButtonVisible()    { return isVisibleById(BTN_LOGIN); }
    public boolean isSignUpButtonVisible()   { return isVisibleById(BTN_SIGNUP); }
    public boolean isLogoVisible()           { return isVisibleById(LOGO); }
    public boolean isOnWelcomeActivity()     { return isOnActivity("WelcomeActivity"); }

    /** Returns true if Welcome page is currently displayed */
    public boolean isWelcomePageVisible() {
        return isVisibleById(BTN_LOGIN) || isVisibleById(BTN_SIGNUP);
    }

    public String getWelcomeTitle() {
        try { return getTextById(WELCOME_TITLE); } catch (Exception e) { return ""; }
    }
}
