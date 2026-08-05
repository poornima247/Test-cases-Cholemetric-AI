package com.cholemetric.automation.pages;

/**
 * SettingsPage — Page Object for SettingsActivity
 */
public class SettingsPage extends BasePage {
    private static final String EDIT_PROFILE_OPTION  = "com.cholemetric.app:id/layoutEditProfile";
    private static final String CHANGE_PASS_OPTION   = "com.cholemetric.app:id/layoutChangePassword";
    private static final String HELP_FAQ_OPTION      = "com.cholemetric.app:id/layoutHelpFaq";
    private static final String LOGOUT_OPTION        = "com.cholemetric.app:id/layoutLogout";
    private static final String DELETE_ACCOUNT       = "com.cholemetric.app:id/layoutDeleteAccount";
    private static final String DOCTOR_NAME          = "com.cholemetric.app:id/tvDoctorName";
    private static final String DOCTOR_EMAIL         = "com.cholemetric.app:id/tvDoctorEmail";
    private static final String BACK_BTN             = "com.cholemetric.app:id/ivBack";
    private static final String PAGE_TITLE           = "com.cholemetric.app:id/tvTitle";
    private static final String CONFIRM_LOGOUT       = "com.cholemetric.app:id/btnConfirmLogout";
    private static final String CONFIRM_DELETE       = "com.cholemetric.app:id/btnConfirmDelete";

    public SettingsPage() { super(); }

    public void clickEditProfile()   { tapById(EDIT_PROFILE_OPTION); }
    public void clickChangePassword(){ tapById(CHANGE_PASS_OPTION); }
    public void clickHelpFaq()       { tapById(HELP_FAQ_OPTION); }
    public void clickLogout()        { tapById(LOGOUT_OPTION); }
    public void clickDeleteAccount() { tapById(DELETE_ACCOUNT); }
    public void clickBack()          { tapById(BACK_BTN); }
    public void clickConfirmLogout() { tapById(CONFIRM_LOGOUT); }
    public void clickConfirmDelete() { tapById(CONFIRM_DELETE); }

    public boolean isSettingsPageVisible()     { return isVisibleById(EDIT_PROFILE_OPTION); }
    public boolean isEditProfileVisible()      { return isVisibleById(EDIT_PROFILE_OPTION); }
    public boolean isChangePasswordVisible()   { return isVisibleById(CHANGE_PASS_OPTION); }
    public boolean isHelpFaqVisible()          { return isVisibleById(HELP_FAQ_OPTION); }
    public boolean isLogoutOptionVisible()     { return isVisibleById(LOGOUT_OPTION); }
    public boolean isDoctorNameVisible()       { return isVisibleById(DOCTOR_NAME); }
    public boolean isOnSettingsActivity()      { return isOnActivity("SettingsActivity"); }

    public String getDoctorName()  { try { return getTextById(DOCTOR_NAME); }  catch (Exception e) { return ""; } }
    public String getDoctorEmail() { try { return getTextById(DOCTOR_EMAIL); } catch (Exception e) { return ""; } }
    public String getPageTitle()   { try { return getTextById(PAGE_TITLE); }   catch (Exception e) { return ""; } }
}
