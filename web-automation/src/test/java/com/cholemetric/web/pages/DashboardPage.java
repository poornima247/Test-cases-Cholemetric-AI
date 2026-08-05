package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;
import org.openqa.selenium.WebElement;

public class DashboardPage extends BasePage {
    private static final By NEW_ANALYSIS_BTN = By.cssSelector(".btn-new-analysis, #btnNewAnalysis, a[href*='new_analysis']");
    private static final By PATIENT_HISTORY_BTN = By.cssSelector(".btn-history, #btnPatientHistory, a[href*='patient_history']");
    private static final By SETTINGS_BTN = By.cssSelector(".btn-settings, a[href*='settings']");
    private static final By LOGOUT_BTN = By.cssSelector(".btn-logout, #btnLogout, button.logout");
    private static final By WELCOME_MESSAGE = By.cssSelector(".welcome-msg, .doctor-name, h1, h2");
    private static final By SCAN_COUNT = By.cssSelector(".scan-count, .total-scans, #scanCount");
    private static final By RECENT_SCANS = By.cssSelector(".recent-scans, .scan-list, #recentScans");
    private static final By EMPTY_STATE = By.cssSelector(".empty-state, .no-scans");

    public DashboardPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "dashboard.html"); }
    public void clickNewAnalysis() { click(NEW_ANALYSIS_BTN); }
    public void clickPatientHistory() { click(PATIENT_HISTORY_BTN); }
    public void clickSettings() { click(SETTINGS_BTN); }
    public void clickLogout() { click(LOGOUT_BTN); }
    public boolean isDashboardDisplayed() { return driver.getTitle().contains("Dashboard") || isDisplayed(NEW_ANALYSIS_BTN) || isDisplayed(WELCOME_MESSAGE); }
    public boolean isWelcomeMessageDisplayed() { return isDisplayed(WELCOME_MESSAGE); }
    public boolean isNewAnalysisBtnDisplayed() { return isDisplayed(NEW_ANALYSIS_BTN); }
    public boolean isLogoutBtnDisplayed() { return isDisplayed(LOGOUT_BTN); }
    public String getWelcomeText() { try { return getText(WELCOME_MESSAGE); } catch(Exception e) { return ""; } }
    public List<WebElement> getRecentScans() { return driver.findElements(RECENT_SCANS); }
    public boolean isEmptyStateDisplayed() { return isDisplayed(EMPTY_STATE); }
}
