package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditProfilePage extends BasePage {
    private static final By NAME = By.id("name");
    private static final By EMAIL = By.id("email");
    private static final By HOSPITAL = By.id("hospital");
    private static final By SPECIALIZATION = By.id("specialization");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit'], .btn-save");

    public EditProfilePage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "edit_profile.html"); }
    public void enterName(String val) { type(NAME, val); }
    public void enterEmail(String val) { type(EMAIL, val); }
    public void enterHospital(String val) { type(HOSPITAL, val); }
    public void enterSpecialization(String val) { type(SPECIALIZATION, val); }
    public void clickSave() { click(SAVE_BUTTON); }
}
