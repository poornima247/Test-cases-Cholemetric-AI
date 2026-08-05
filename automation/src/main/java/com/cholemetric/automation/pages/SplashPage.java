package com.cholemetric.automation.pages;

/**
 * SplashPage — Page Object for SplashActivity
 */
public class SplashPage extends BasePage {
    private static final String LOGO       = "com.cholemetric.app:id/ivSplashLogo";
    private static final String APP_NAME   = "com.cholemetric.app:id/tvAppName";
    private static final String TAGLINE    = "com.cholemetric.app:id/tvTagline";

    public SplashPage() { super(); }

    public boolean isLogoVisible()    { return isVisibleById(LOGO); }
    public boolean isAppNameVisible() { return isVisibleById(APP_NAME); }
    public boolean isTaglineVisible() { return isVisibleById(TAGLINE); }
    public boolean isOnSplashActivity() { return isOnActivity("SplashActivity"); }

    public void waitForSplashToFinish() {
        // Wait until splash transitions to WelcomeActivity or LoginActivity
        waitSeconds(3);
    }
}
