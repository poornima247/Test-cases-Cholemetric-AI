package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WelcomePage extends BasePage {
    private static final By LOGIN_BTN = By.cssSelector("a[href*='login'], .btn-login");
    private static final By SIGNUP_BTN = By.cssSelector("a[href*='signup'], .btn-signup");
    private static final By LOGO = By.cssSelector(".logo, img[alt='logo']");
    private static final By HERO_SECTION = By.cssSelector(".hero, #hero");

    public WelcomePage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "welcome.html"); }
    public void clickLogin() { click(LOGIN_BTN); }
    public void clickSignup() { click(SIGNUP_BTN); }
    public boolean isLogoDisplayed() { return isDisplayed(LOGO); }
    public boolean isHeroSectionDisplayed() { return isDisplayed(HERO_SECTION); }
}
