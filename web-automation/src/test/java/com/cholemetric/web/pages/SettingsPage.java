package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SettingsPage extends BasePage {
    private static final By PROFILE_SECTION = By.cssSelector("#profile-section, .profile");
    private static final By CHANGE_PASSWORD = By.cssSelector("#change-password, .btn-pwd");
    private static final By DELETE_ACCOUNT = By.cssSelector("#delete-account, .btn-delete");
    private static final By LOGOUT = By.cssSelector("#logout-settings, .btn-logout");

    public SettingsPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "settings.html"); }
    public boolean isProfileSectionDisplayed() { return isDisplayed(PROFILE_SECTION); }
    public void clickChangePassword() { click(CHANGE_PASSWORD); }
    public void clickDeleteAccount() { click(DELETE_ACCOUNT); }
    public void clickLogout() { click(LOGOUT); }
}
