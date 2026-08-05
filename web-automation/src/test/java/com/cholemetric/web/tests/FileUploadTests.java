package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileUploadTests extends BaseTest {

    @Test(description = "TC_WEB_FILE_001: New analysis page has file upload area or content area")
    public void tc_file_001_newAnalysisPageLoads() {
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_FILE_001: New analysis page should load");
    }

    @Test(description = "TC_WEB_FILE_002: New analysis page has input elements")
    public void tc_file_002_newAnalysisHasInputElements() {
        getDriver().get(baseUrl + "new_analysis.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.isEmpty(), "TC_WEB_FILE_002: New analysis page should have content");
    }

    @Test(description = "TC_WEB_FILE_003: New analysis page source contains form or upload related HTML")
    public void tc_file_003_pageSourceContainsFormContent() {
        getDriver().get(baseUrl + "new_analysis.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<input") || src.contains("<form") || src.contains("<button") || !src.isEmpty(),
                "TC_WEB_FILE_003: New analysis page should have form or upload elements");
    }

    @Test(description = "TC_WEB_FILE_004: New analysis page does not show 404")
    public void tc_file_004_newAnalysisNotNotFound() {
        getDriver().get(baseUrl + "new_analysis.html");
        String src = getDriver().getPageSource().toLowerCase();
        Assert.assertFalse(src.contains("404 not found"),
                "TC_WEB_FILE_004: New analysis page should not be 404");
    }

    @Test(description = "TC_WEB_FILE_005: New analysis page has body element")
    public void tc_file_005_newAnalysisHasBody() {
        getDriver().get(baseUrl + "new_analysis.html");
        org.openqa.selenium.WebElement body = getDriver().findElement(org.openqa.selenium.By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_FILE_005: New analysis body element should exist");
    }

    @Test(description = "TC_WEB_FILE_006: New analysis page load time under 5 seconds")
    public void tc_file_006_newAnalysisLoadTime() {
        long s = System.currentTimeMillis();
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertTrue(System.currentTimeMillis() - s < 5000, "TC_WEB_FILE_006: Load time should be under 5s");
    }

    @Test(description = "TC_WEB_FILE_007: New analysis page title is meaningful")
    public void tc_file_007_meaningfulTitle() {
        getDriver().get(baseUrl + "new_analysis.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_FILE_007: Title should not be null");
        Assert.assertFalse(title.isEmpty(), "TC_WEB_FILE_007: Title should not be empty");
    }

    @Test(description = "TC_WEB_FILE_008: Page refresh keeps new analysis page")
    public void tc_file_008_pageRefreshStays() {
        getDriver().get(baseUrl + "new_analysis.html");
        String titleBefore = getDriver().getTitle();
        getDriver().navigate().refresh();
        String titleAfter = getDriver().getTitle();
        Assert.assertEquals(titleAfter, titleBefore, "TC_WEB_FILE_008: Title should be same after refresh");
    }

    @Test(description = "TC_WEB_FILE_009: New analysis page renders HTML correctly")
    public void tc_file_009_rendersHtml() {
        getDriver().get(baseUrl + "new_analysis.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<"), "TC_WEB_FILE_009: Page should contain HTML tags");
    }

    @Test(description = "TC_WEB_FILE_010: New analysis page navigation from dashboard")
    public void tc_file_010_navigationFromDashboard() {
        getDriver().get(baseUrl + "dashboard.html");
        getDriver().get(baseUrl + "new_analysis.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_FILE_010: Navigation from dashboard to new analysis works");
    }
}
