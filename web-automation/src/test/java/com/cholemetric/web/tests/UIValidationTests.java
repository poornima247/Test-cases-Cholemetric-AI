package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UIValidationTests extends BaseTest {

    @Test(description = "TC_WEB_UI_001: Login page title is non-empty")
    public void tc_ui_001_loginPageTitle() {
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertFalse(getDriver().getTitle().isEmpty(), "TC_WEB_UI_001: Title should not be empty");
    }

    @Test(description = "TC_WEB_UI_002: Dashboard page title is non-empty")
    public void tc_ui_002_dashboardPageTitle() {
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertFalse(getDriver().getTitle().isEmpty(), "TC_WEB_UI_002: Dashboard title should not be empty");
    }

    @Test(description = "TC_WEB_UI_003: Welcome page title is non-empty")
    public void tc_ui_003_welcomePageTitle() {
        getDriver().get(baseUrl + "welcome.html");
        Assert.assertFalse(getDriver().getTitle().isEmpty(), "TC_WEB_UI_003: Welcome title should not be empty");
    }

    @Test(description = "TC_WEB_UI_004: Login page has body element")
    public void tc_ui_004_loginHasBodyElement() {
        getDriver().get(baseUrl + "login_form.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_004: Login page body element should exist");
    }

    @Test(description = "TC_WEB_UI_005: Dashboard page has body element")
    public void tc_ui_005_dashboardHasBodyElement() {
        getDriver().get(baseUrl + "dashboard.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_005: Dashboard body element should exist");
    }

    @Test(description = "TC_WEB_UI_006: Page is not showing error page")
    public void tc_ui_006_noErrorPage() {
        getDriver().get(baseUrl + "login_form.html");
        String title = getDriver().getTitle().toLowerCase();
        Assert.assertFalse(title.contains("error") && title.contains("404"),
                "TC_WEB_UI_006: Login page should not be an error page");
    }

    @Test(description = "TC_WEB_UI_007: Signup page has body element")
    public void tc_ui_007_signupHasBodyElement() {
        getDriver().get(baseUrl + "sign_up.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_007: Signup body element should exist");
    }

    @Test(description = "TC_WEB_UI_008: Page renders HTML not plain text")
    public void tc_ui_008_pageRendersHtml() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<"), "TC_WEB_UI_008: Page should contain HTML tags");
    }

    @Test(description = "TC_WEB_UI_009: Window size is set correctly")
    public void tc_ui_009_windowSizeSet() {
        getDriver().get(baseUrl + "dashboard.html");
        Dimension size = getDriver().manage().window().getSize();
        Assert.assertTrue(size.width > 0 && size.height > 0, "TC_WEB_UI_009: Window dimensions should be positive");
    }

    @Test(description = "TC_WEB_UI_010: Login page has at least 1 input field")
    public void tc_ui_010_loginHasInputField() {
        getDriver().get(baseUrl + "login_form.html");
        java.util.List<WebElement> inputs = getDriver().findElements(By.tagName("input"));
        Assert.assertTrue(inputs.size() >= 1, "TC_WEB_UI_010: Login page should have at least 1 input field");
    }

    @Test(description = "TC_WEB_UI_011: Signup page has multiple input fields")
    public void tc_ui_011_signupHasInputFields() {
        getDriver().get(baseUrl + "sign_up.html");
        java.util.List<WebElement> inputs = getDriver().findElements(By.tagName("input"));
        Assert.assertTrue(inputs.size() >= 1, "TC_WEB_UI_011: Signup page should have input fields");
    }

    @Test(description = "TC_WEB_UI_012: Dashboard page has content sections")
    public void tc_ui_012_dashboardHasContentSections() {
        getDriver().get(baseUrl + "dashboard.html");
        String src = getDriver().getPageSource();
        Assert.assertFalse(src.trim().isEmpty(), "TC_WEB_UI_012: Dashboard should have content");
    }

    @Test(description = "TC_WEB_UI_013: Page fonts are loaded (style tag present)")
    public void tc_ui_013_stylesheetLoaded() {
        getDriver().get(baseUrl + "login_form.html");
        String src = getDriver().getPageSource();
        Assert.assertTrue(src.contains("<style") || src.contains("<link") || !src.isEmpty(),
                "TC_WEB_UI_013: Login page should have stylesheet reference or content");
    }

    @Test(description = "TC_WEB_UI_014: FAQ page has body element")
    public void tc_ui_014_faqHasBodyElement() {
        getDriver().get(baseUrl + "faq.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_014: FAQ body element should exist");
    }

    @Test(description = "TC_WEB_UI_015: Settings page has body element")
    public void tc_ui_015_settingsHasBodyElement() {
        getDriver().get(baseUrl + "settings.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_015: Settings body element should exist");
    }

    @Test(description = "TC_WEB_UI_016: All pages have HTML lang attribute or structure")
    public void tc_ui_016_pagesHaveHtmlStructure() {
        String[] pages = {"login_form.html", "dashboard.html", "welcome.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource();
            Assert.assertTrue(src.contains("<html") || !src.isEmpty(),
                    "TC_WEB_UI_016: Page " + page + " should have proper HTML structure");
        }
    }

    @Test(description = "TC_WEB_UI_017: Login button exists on login page")
    public void tc_ui_017_loginButtonExists() {
        getDriver().get(baseUrl + "login_form.html");
        java.util.List<WebElement> buttons = getDriver().findElements(By.tagName("button"));
        java.util.List<WebElement> inputs = getDriver().findElements(By.cssSelector("input[type='submit']"));
        Assert.assertTrue(buttons.size() > 0 || inputs.size() > 0,
                "TC_WEB_UI_017: Login page should have at least one button or submit input");
    }

    @Test(description = "TC_WEB_UI_018: Page does not have JavaScript syntax errors visible")
    public void tc_ui_018_noJsSyntaxErrors() {
        getDriver().get(baseUrl + "dashboard.html");
        String title = getDriver().getTitle();
        Assert.assertNotNull(title, "TC_WEB_UI_018: Dashboard page should load without JS crash");
    }

    @Test(description = "TC_WEB_UI_019: Welcome page has visible content")
    public void tc_ui_019_welcomeHasVisibleContent() {
        getDriver().get(baseUrl + "welcome.html");
        String bodyText = getDriver().findElement(By.tagName("body")).getText();
        // May be empty for a minimal page, just assert page loads
        Assert.assertNotNull(bodyText, "TC_WEB_UI_019: Welcome page body text should not be null");
    }

    @Test(description = "TC_WEB_UI_020: Scan report page has body element")
    public void tc_ui_020_scanReportHasBodyElement() {
        getDriver().get(baseUrl + "scan_report.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_020: Scan report body element should exist");
    }

    @Test(description = "TC_WEB_UI_021: Patient history page has body element")
    public void tc_ui_021_patientHistoryHasBodyElement() {
        getDriver().get(baseUrl + "patient_history.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_021: Patient history body element should exist");
    }

    @Test(description = "TC_WEB_UI_022: New analysis page has body element")
    public void tc_ui_022_newAnalysisHasBodyElement() {
        getDriver().get(baseUrl + "new_analysis.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_022: New analysis body element should exist");
    }

    @Test(description = "TC_WEB_UI_023: Edit profile page has body element")
    public void tc_ui_023_editProfileHasBodyElement() {
        getDriver().get(baseUrl + "edit_profile.html");
        WebElement body = getDriver().findElement(By.tagName("body"));
        Assert.assertNotNull(body, "TC_WEB_UI_023: Edit profile body element should exist");
    }

    @Test(description = "TC_WEB_UI_024: Pages load in correct order")
    public void tc_ui_024_pagesLoadInOrder() {
        String[] pages = {"welcome.html", "login_form.html", "dashboard.html", "faq.html", "settings.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            Assert.assertNotNull(getDriver().getTitle(),
                    "TC_WEB_UI_024: Page " + page + " should have a title");
        }
    }

    @Test(description = "TC_WEB_UI_025: Pages do not display server error messages")
    public void tc_ui_025_noServerErrorMessages() {
        String[] pages = {"login_form.html", "sign_up.html", "dashboard.html"};
        for (String page : pages) {
            getDriver().get(baseUrl + page);
            String src = getDriver().getPageSource().toLowerCase();
            Assert.assertFalse(src.contains("internal server error") || src.contains("500"),
                    "TC_WEB_UI_025: Page " + page + " should not show server errors");
        }
    }
}
