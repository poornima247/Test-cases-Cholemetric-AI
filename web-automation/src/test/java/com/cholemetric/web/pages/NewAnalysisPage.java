package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NewAnalysisPage extends BasePage {
    private static final By FILE_INPUT = By.cssSelector("#ctScanFile, input[type='file']");
    private static final By PATIENT_NAME = By.cssSelector("#patientName, input[name='patientName']");
    private static final By SUBMIT_BTN = By.cssSelector("button[type='submit'], #btnSubmitScan");
    private static final By RESULTS_SECTION = By.cssSelector("#results, .analysis-results");

    public NewAnalysisPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "new_analysis.html"); }
    public void uploadScan(String filePath) { type(FILE_INPUT, filePath); }
    public void enterPatientName(String name) { type(PATIENT_NAME, name); }
    public void clickSubmit() { click(SUBMIT_BTN); }
    public boolean isResultsSectionDisplayed() { return isDisplayed(RESULTS_SECTION); }
}
