package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ForgotPasswordPage extends BasePage {
    private static final By EMAIL = By.id("email");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit'], .btn-submit");
    private static final By SUCCESS_MESSAGE = By.cssSelector(".success, .alert-success");

    public ForgotPasswordPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "forgot_password.html"); }
    public void enterEmail(String email) { type(EMAIL, email); }
    public void clickSubmit() { click(SUBMIT_BUTTON); }
    public boolean isSuccessMessageDisplayed() { return isDisplayed(SUCCESS_MESSAGE); }
}
