package com.cholemetric.automation.pages;

/**
 * EditProfilePage — Page Object for EditProfileActivity
 */
public class EditProfilePage extends BasePage {
    private static final String FULL_NAME_FIELD  = "com.cholemetric.app:id/etFullName";
    private static final String HOSPITAL_FIELD   = "com.cholemetric.app:id/etHospital";
    private static final String SPECIALIZATION   = "com.cholemetric.app:id/etSpecialization";
    private static final String SAVE_BTN         = "com.cholemetric.app:id/btnSave";
    private static final String BACK_BTN         = "com.cholemetric.app:id/ivBack";
    private static final String SUCCESS_TEXT     = "com.cholemetric.app:id/tvSuccess";
    private static final String ERROR_TEXT       = "com.cholemetric.app:id/tvError";
    private static final String PAGE_TITLE       = "com.cholemetric.app:id/tvTitle";

    public EditProfilePage() { super(); }

    public EditProfilePage enterFullName(String name)    { typeTextById(FULL_NAME_FIELD, name); return this; }
    public EditProfilePage enterHospital(String hospital){ typeTextById(HOSPITAL_FIELD, hospital); return this; }
    public EditProfilePage enterSpecialization(String sp){ typeTextById(SPECIALIZATION, sp); return this; }
    public void clickSave()  { tapById(SAVE_BTN); }
    public void clickBack()  { tapById(BACK_BTN); }

    public void updateProfile(String name, String hospital, String spec) {
        enterFullName(name).enterHospital(hospital).enterSpecialization(spec);
        clickSave();
    }

    public boolean isEditProfilePageVisible() { return isVisibleById(FULL_NAME_FIELD); }
    public boolean isSaveButtonVisible()      { return isVisibleById(SAVE_BTN); }
    public boolean isSuccessMessageVisible()  { return isVisibleById(SUCCESS_TEXT); }
    public boolean isErrorMessageVisible()    { return isVisibleById(ERROR_TEXT); }
    public boolean isOnEditProfileActivity()  { return isOnActivity("EditProfileActivity"); }

    public String getFullNameText()   { try { return getTextById(FULL_NAME_FIELD); } catch (Exception e) { return ""; } }
    public String getSuccessMessage() { try { return getTextById(SUCCESS_TEXT); }    catch (Exception e) { return ""; } }
    public String getErrorMessage()   { try { return getTextById(ERROR_TEXT); }      catch (Exception e) { return ""; } }
}
