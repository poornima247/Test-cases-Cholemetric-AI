package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class PatientHistoryPage extends BasePage {
    private static final By SCAN_LIST_ITEMS = By.cssSelector(".scan-item, tr.scan-row");
    private static final By SEARCH_INPUT = By.cssSelector("#search, input[type='search']");
    private static final By DELETE_BUTTONS = By.cssSelector(".btn-delete, button.delete");

    public PatientHistoryPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "patient_history.html"); }
    public void enterSearch(String query) { type(SEARCH_INPUT, query); }
    public List<WebElement> getScanItems() { return driver.findElements(SCAN_LIST_ITEMS); }
    public void deleteFirstScan() { 
        List<WebElement> btns = driver.findElements(DELETE_BUTTONS);
        if (!btns.isEmpty()) btns.get(0).click(); 
    }
}
