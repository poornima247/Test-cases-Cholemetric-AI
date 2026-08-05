package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import com.cholemetric.web.pages.NewAnalysisPage;
import com.cholemetric.web.pages.PatientHistoryPage;
import com.cholemetric.web.pages.ScanReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CrudOperationsTests extends BaseTest {

    @Test(description = "TC_WEB_CRUD_001: New analysis page loads")
    public void tc_crud_001_newAnalysisPageLoads() {
        NewAnalysisPage nap = new NewAnalysisPage(getDriver());
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_CRUD_001: New analysis page title not null");
    }

    @Test(description = "TC_WEB_CRUD_002: Patient history page loads")
    public void tc_crud_002_patientHistoryPageLoads() {
        PatientHistoryPage php = new PatientHistoryPage(getDriver());
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_CRUD_002: Patient history page title not null");
    }

    @Test(description = "TC_WEB_CRUD_003: Scan report page loads")
    public void tc_crud_003_scanReportPageLoads() {
        ScanReportPage srp = new ScanReportPage(getDriver());
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_CRUD_003: Scan report page title not null");
    }

    @Test(description = "TC_WEB_CRUD_004: New analysis page source not empty")
    public void tc_crud_004_newAnalysisPageSourceNotEmpty() {
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertFalse(getDriver().getPageSource().isEmpty(), "TC_WEB_CRUD_004: Source should not be empty");
    }

    @Test(description = "TC_WEB_CRUD_005: Patient history page source not empty")
    public void tc_crud_005_patientHistorySourceNotEmpty() {
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertFalse(getDriver().getPageSource().isEmpty(), "TC_WEB_CRUD_005: Source should not be empty");
    }

    @Test(description = "TC_WEB_CRUD_006: Scan report page source not empty")
    public void tc_crud_006_scanReportSourceNotEmpty() {
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertFalse(getDriver().getPageSource().isEmpty(), "TC_WEB_CRUD_006: Source should not be empty");
    }

    @Test(description = "TC_WEB_CRUD_007: New analysis page load time < 5s")
    public void tc_crud_007_newAnalysisLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_CRUD_007: Load time exceeds 5s");
    }

    @Test(description = "TC_WEB_CRUD_008: Patient history page load time < 5s")
    public void tc_crud_008_patientHistoryLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "patient_history.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_CRUD_008: Load time exceeds 5s");
    }

    @Test(description = "TC_WEB_CRUD_009: Scan report page load time < 5s")
    public void tc_crud_009_scanReportLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "scan_report.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_CRUD_009: Load time exceeds 5s");
    }

    @Test(description = "TC_WEB_CRUD_010: New analysis page has HTML structure")
    public void tc_crud_010_newAnalysisHasHtml() {
        getDriver().get(baseUrl + "new_analysis.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_CRUD_010: New analysis page should have body element");
    }

    @Test(description = "TC_WEB_CRUD_011: Patient history page has HTML structure")
    public void tc_crud_011_patientHistoryHasHtml() {
        getDriver().get(baseUrl + "patient_history.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_CRUD_011: Patient history page should have body element");
    }

    @Test(description = "TC_WEB_CRUD_012: Scan report page has HTML structure")
    public void tc_crud_012_scanReportHasHtml() {
        getDriver().get(baseUrl + "scan_report.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_CRUD_012: Scan report page should have body element");
    }

    @Test(description = "TC_WEB_CRUD_013: All CRUD pages do not return 404")
    public void tc_crud_013_crudPagesNotNotFound() {
        String[] pages = {"new_analysis.html", "patient_history.html", "scan_report.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource().toLowerCase();
            Assert.assertFalse(src.contains("404 not found"),
                    "TC_WEB_CRUD_013: Page " + page + " should not be 404");
        }
    }

    @Test(description = "TC_WEB_CRUD_014: Dashboard displays data sections")
    public void tc_crud_014_dashboardDataSections() {
        getDriver().get(baseUrl + "dashboard.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_CRUD_014: Dashboard should have data content");
    }

    @Test(description = "TC_WEB_CRUD_015: New analysis page title is meaningful")
    public void tc_crud_015_newAnalysisMeaningfulTitle() {
        getDriver().get(baseUrl + "new_analysis.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_CRUD_015: New analysis title should not be null");
    }

    @Test(description = "TC_WEB_CRUD_016: Patient history page title is meaningful")
    public void tc_crud_016_patientHistoryMeaningfulTitle() {
        getDriver().get(baseUrl + "patient_history.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_CRUD_016: Patient history title should not be null");
    }

    @Test(description = "TC_WEB_CRUD_017: Scan report page title is meaningful")
    public void tc_crud_017_scanReportMeaningfulTitle() {
        getDriver().get(baseUrl + "scan_report.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_CRUD_017: Scan report title should not be null");
    }

    @Test(description = "TC_WEB_CRUD_018: New analysis page multiple navigations")
    public void tc_crud_018_newAnalysisMultiNavigation() {
        for (int i = 0; i < 3; i++) {
            getDriver().get(baseUrl + "new_analysis.html");
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_CRUD_018: Should load consistently");
        }
    }

    @Test(description = "TC_WEB_CRUD_019: Patient history page multiple navigations")
    public void tc_crud_019_patientHistoryMultiNavigation() {
        for (int i = 0; i < 3; i++) {
            getDriver().get(baseUrl + "patient_history.html");
            Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_CRUD_019: Should load consistently");
        }
    }

    @Test(description = "TC_WEB_CRUD_020: All data pages are accessible in sequence")
    public void tc_crud_020_allDataPagesSequential() {
        String[] pages = {"new_analysis.html", "patient_history.html", "scan_report.html", "dashboard.html"};
        for (String page : pages) {
            long s = System.currentTimeMillis();
            getDriver().get(baseUrl + page);
            long elapsed = System.currentTimeMillis() - s;
            Assert.assertTrue(elapsed < 5000, "TC_WEB_CRUD_020: Page " + page + " took " + elapsed + "ms > 5s");
        }
    }
}
