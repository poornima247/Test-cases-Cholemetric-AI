package com.cholemetric.automation.pages;

/**
 * SignUpPage — Page Object for SignUpActivity
 */
public class SignUpPage extends BasePage {
    private static final String FULL_NAME_FIELD    = "com.cholemetric.app:id/etFullName";
    private static final String EMAIL_FIELD        = "com.cholemetric.app:id/etEmail";
    private static final String PASSWORD_FIELD     = "com.cholemetric.app:id/etPassword";
    private static final String CONFIRM_PASS_FIELD = "com.cholemetric.app:id/etConfirmPassword";
    private static final String HOSPITAL_FIELD     = "com.cholemetric.app:id/etHospital";
    private static final String SPECIALIZATION     = "com.cholemetric.app:id/etSpecialization";
    private static final String REGISTER_BUTTON    = "com.cholemetric.app:id/btnRegister";
    private static final String LOGIN_LINK         = "com.cholemetric.app:id/tvLogin";
    private static final String ERROR_TEXT         = "com.cholemetric.app:id/tvError";
    private static final String SUCCESS_TEXT       = "com.cholemetric.app:id/tvSuccess";

    public SignUpPage() { super(); }

    public SignUpPage enterFullName(String name)         { typeTextById(FULL_NAME_FIELD, name); return this; }
    public SignUpPage enterEmail(String email)            { typeTextById(EMAIL_FIELD, email); return this; }
    public SignUpPage enterPassword(String password)      { typeTextById(PASSWORD_FIELD, password); return this; }
    public SignUpPage enterConfirmPassword(String pass)   { typeTextById(CONFIRM_PASS_FIELD, pass); return this; }
    public SignUpPage enterHospital(String hospital)      { typeTextById(HOSPITAL_FIELD, hospital); return this; }
    public SignUpPage enterSpecialization(String spec)    { typeTextById(SPECIALIZATION, spec); return this; }
    public void clickRegisterButton()  { tapById(REGISTER_BUTTON); }
    public void clickLoginLink()       { tapById(LOGIN_LINK); }

    public void register(String name, String email, String password, String hospital, String spec) {
        enterFullName(name).enterEmail(email).enterPassword(password)
            .enterConfirmPassword(password).enterHospital(hospital).enterSpecialization(spec);
        clickRegisterButton();
    }

    public boolean isSignUpPageVisible()     { return isVisibleById(EMAIL_FIELD); }
    public boolean isRegisterButtonVisible() { return isVisibleById(REGISTER_BUTTON); }
    public boolean isErrorMessageVisible()   { return isVisibleById(ERROR_TEXT); }
    public boolean isSuccessMessageVisible() { return isVisibleById(SUCCESS_TEXT); }
    public boolean isOnSignUpActivity()      { return isOnActivity("SignUpActivity"); }

    public String getErrorMessage() {
        try { return getTextById(ERROR_TEXT); } catch (Exception e) { return ""; }
    }
}
