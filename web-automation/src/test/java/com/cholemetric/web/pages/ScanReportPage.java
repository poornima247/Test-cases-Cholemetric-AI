package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ScanReportPage extends BasePage {
    private static final By REPORT_TITLE = By.cssSelector("h1.report-title, .title");
    private static final By SCAN_IMAGE = By.cssSelector(".scan-image, img.ct-scan");
    private static final By ANALYSIS_RESULTS = By.cssSelector(".analysis-results, #results");
    private static final By EXPORT_BUTTON = By.cssSelector(".btn-export, #btnExport");

    public ScanReportPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "scan_report.html"); }
    public boolean isReportTitleDisplayed() { return isDisplayed(REPORT_TITLE); }
    public boolean isScanImageDisplayed() { return isDisplayed(SCAN_IMAGE); }
    public boolean isAnalysisResultsDisplayed() { return isDisplayed(ANALYSIS_RESULTS); }
    public void clickExport() { click(EXPORT_BUTTON); }
}
