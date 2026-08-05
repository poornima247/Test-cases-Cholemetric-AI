package com.cholemetric.automation.pages;

/**
 * ForgotPasswordPage — Page Object for ForgotPasswordActivity
 */
public class ForgotPasswordPage extends BasePage {
    private static final String EMAIL_FIELD    = "com.cholemetric.app:id/etEmail";
    private static final String SEND_BUTTON    = "com.cholemetric.app:id/btnSendReset";
    private static final String BACK_LINK      = "com.cholemetric.app:id/tvBackToLogin";
    private static final String SUCCESS_TEXT   = "com.cholemetric.app:id/tvSuccess";
    private static final String ERROR_TEXT     = "com.cholemetric.app:id/tvError";

    public ForgotPasswordPage() { super(); }

    public ForgotPasswordPage enterEmail(String email) { typeTextById(EMAIL_FIELD, email); return this; }
    public void clickSendResetButton() { tapById(SEND_BUTTON); }
    public void clickBackToLogin()     { tapById(BACK_LINK); }

    public void submitForgotPassword(String email) {
        enterEmail(email);
        clickSendResetButton();
    }

    public boolean isForgotPasswordPageVisible() { return isVisibleById(EMAIL_FIELD) && isVisibleById(SEND_BUTTON); }
    public boolean isSuccessMessageVisible()     { return isVisibleById(SUCCESS_TEXT); }
    public boolean isErrorMessageVisible()       { return isVisibleById(ERROR_TEXT); }
    public boolean isOnForgotPasswordActivity()  { return isOnActivity("ForgotPasswordActivity"); }

    public String getSuccessMessage() {
        try { return getTextById(SUCCESS_TEXT); } catch (Exception e) { return ""; }
    }
    public String getErrorMessage() {
        try { return getTextById(ERROR_TEXT); } catch (Exception e) { return ""; }
    }
}
