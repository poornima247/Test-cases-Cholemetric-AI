package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SignUpPage extends BasePage {
    private static final By FULL_NAME = By.id("fullName");
    private static final By EMAIL = By.id("email");
    private static final By PASSWORD = By.id("password");
    private static final By CONFIRM_PASSWORD = By.id("confirmPassword");
    private static final By HOSPITAL = By.id("hospital");
    private static final By SPECIALIZATION = By.id("specialization");
    private static final By SIGN_UP_BUTTON = By.cssSelector("button[type='submit'], .btn-signup, #btnSignUp");
    private static final By LOGIN_LINK = By.cssSelector("a[href*='login'], .login-link");
    private static final By ERROR_MESSAGE = By.cssSelector(".error, .alert-danger, #errorMsg");

    public SignUpPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "signup.html"); }
    public void enterFullName(String name) { type(FULL_NAME, name); }
    public void enterEmail(String email) { type(EMAIL, email); }
    public void enterPassword(String password) { type(PASSWORD, password); }
    public void enterConfirmPassword(String pass) { type(CONFIRM_PASSWORD, pass); }
    public void enterHospital(String hospital) { type(HOSPITAL, hospital); }
    public void enterSpecialization(String spec) { type(SPECIALIZATION, spec); }
    public void clickSignUpButton() { click(SIGN_UP_BUTTON); }
    public void clickLoginLink() { click(LOGIN_LINK); }
    public boolean isSignUpPageDisplayed() { return isDisplayed(SIGN_UP_BUTTON); }
    public boolean isErrorMessageDisplayed() { return isDisplayed(ERROR_MESSAGE); }
    public String getErrorMessage() { try { return getText(ERROR_MESSAGE); } catch(Exception e) { return ""; } }
    public void register(String name, String email, String password, String hospital, String spec) {
        enterFullName(name); enterEmail(email); enterPassword(password);
        enterConfirmPassword(password); enterHospital(hospital); enterSpecialization(spec);
        clickSignUpButton();
    }
}
