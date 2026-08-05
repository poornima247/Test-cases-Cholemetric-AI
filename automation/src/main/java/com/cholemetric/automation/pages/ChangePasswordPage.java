package com.cholemetric.automation.pages;

/**
 * ChangePasswordPage — Page Object for ChangePasswordActivity
 */
public class ChangePasswordPage extends BasePage {
    private static final String CURRENT_PASS  = "com.cholemetric.app:id/etCurrentPassword";
    private static final String NEW_PASS      = "com.cholemetric.app:id/etNewPassword";
    private static final String CONFIRM_PASS  = "com.cholemetric.app:id/etConfirmPassword";
    private static final String SAVE_BTN      = "com.cholemetric.app:id/btnSave";
    private static final String BACK_BTN      = "com.cholemetric.app:id/ivBack";
    private static final String SUCCESS_TEXT  = "com.cholemetric.app:id/tvSuccess";
    private static final String ERROR_TEXT    = "com.cholemetric.app:id/tvError";

    public ChangePasswordPage() { super(); }

    public ChangePasswordPage enterCurrentPassword(String pass)  { typeTextById(CURRENT_PASS, pass); return this; }
    public ChangePasswordPage enterNewPassword(String pass)      { typeTextById(NEW_PASS, pass); return this; }
    public ChangePasswordPage enterConfirmPassword(String pass)  { typeTextById(CONFIRM_PASS, pass); return this; }
    public void clickSave()   { tapById(SAVE_BTN); }
    public void clickBack()   { tapById(BACK_BTN); }

    public void changePassword(String current, String newPass) {
        enterCurrentPassword(current).enterNewPassword(newPass).enterConfirmPassword(newPass);
        clickSave();
    }

    public boolean isChangePasswordPageVisible(){ return isVisibleById(CURRENT_PASS); }
    public boolean isSuccessMessageVisible()    { return isVisibleById(SUCCESS_TEXT); }
    public boolean isErrorMessageVisible()      { return isVisibleById(ERROR_TEXT); }
    public boolean isOnChangePasswordActivity() { return isOnActivity("ChangePasswordActivity"); }

    public String getSuccessMessage() { try { return getTextById(SUCCESS_TEXT); } catch (Exception e) { return ""; } }
    public String getErrorMessage()   { try { return getTextById(ERROR_TEXT); }   catch (Exception e) { return ""; } }
}
