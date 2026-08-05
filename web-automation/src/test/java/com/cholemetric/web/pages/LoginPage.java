package com.cholemetric.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private By emailField = By.id("email");
    private By passwordField = By.id("password");
    private By loginButton = By.id("loginBtn");
    private By errorMessage = By.className("error-msg");
    private By forgotPasswordLink = By.linkText("Forgot Password?");
    private By signUpLink = By.linkText("Sign Up");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void enterEmail(String email) {
        type(emailField, email);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public void clickLogin() {
        click(loginButton);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public void clickForgotPassword() {
        click(forgotPasswordLink);
    }

    public void clickSignUp() {
        click(signUpLink);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(loginButton);
    }
}
