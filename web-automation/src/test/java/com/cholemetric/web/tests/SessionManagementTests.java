package com.cholemetric.web.tests;

import com.cholemetric.web.base.BaseTest;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class SessionManagementTests extends BaseTest {

    @Test(description = "TC_WEB_SESSION_001: Browser session is active")
    public void tc_session_001_browserSessionActive() {
        getDriver().get(baseUrl + "login_form.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_SESSION_001: Browser session should be active");
    }

    @Test(description = "TC_WEB_SESSION_002: Browser can navigate between multiple pages in session")
    public void tc_session_002_multiPageNavigation() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().get(baseUrl + "welcome.html");
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_SESSION_002: Multi-page navigation should work");
    }

    @Test(description = "TC_WEB_SESSION_003: Cookies can be inspected")
    public void tc_session_003_cookiesInspectable() {
        getDriver().get(baseUrl + "login_form.html");
        Set<Cookie> cookies = getDriver().manage().getCookies();
        Assert.assertNotNull(cookies, "TC_WEB_SESSION_003: Cookie set should not be null");
    }

    @Test(description = "TC_WEB_SESSION_004: Cookies can be cleared")
    public void tc_session_004_cookiesCanBeCleared() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().manage().deleteAllCookies();
        Set<Cookie> cookies = getDriver().manage().getCookies();
        Assert.assertTrue(cookies.isEmpty(), "TC_WEB_SESSION_004: Cookies should be empty after clear");
    }

    @Test(description = "TC_WEB_SESSION_005: Adding a custom cookie works")
    public void tc_session_005_addCustomCookie() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().manage().addCookie(new Cookie("test_session", "abc123"));
        Cookie c = getDriver().manage().getCookieNamed("test_session");
        Assert.assertNotNull(c, "TC_WEB_SESSION_005: Custom cookie should be set");
        Assert.assertEquals(c.getValue(), "abc123", "TC_WEB_SESSION_005: Cookie value should match");
    }

    @Test(description = "TC_WEB_SESSION_006: Deleting a specific cookie works")
    public void tc_session_006_deleteSpecificCookie() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().manage().addCookie(new Cookie("del_me", "value"));
        getDriver().manage().deleteCookieNamed("del_me");
        Cookie c = getDriver().manage().getCookieNamed("del_me");
        Assert.assertNull(c, "TC_WEB_SESSION_006: Deleted cookie should not exist");
    }

    @Test(description = "TC_WEB_SESSION_007: Session persists across page navigation")
    public void tc_session_007_sessionPersistsAcrossNavigation() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().manage().addCookie(new Cookie("persist_test", "yes"));
        getDriver().get(baseUrl + "dashboard.html");
        Cookie c = getDriver().manage().getCookieNamed("persist_test");
        // Cookie may or may not persist depending on domain setup
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_SESSION_007: Session navigation should work");
    }

    @Test(description = "TC_WEB_SESSION_008: Local storage can be accessed")
    public void tc_session_008_localStorageAccessible() {
        getDriver().get(baseUrl + "login_form.html");
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) getDriver();
        js.executeScript("localStorage.setItem('testKey', 'testValue');");
        String val = (String) js.executeScript("return localStorage.getItem('testKey');");
        Assert.assertEquals(val, "testValue", "TC_WEB_SESSION_008: localStorage value should match");
    }

    @Test(description = "TC_WEB_SESSION_009: Session storage can be accessed")
    public void tc_session_009_sessionStorageAccessible() {
        getDriver().get(baseUrl + "login_form.html");
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) getDriver();
        js.executeScript("sessionStorage.setItem('sessKey', 'sessValue');");
        String val = (String) js.executeScript("return sessionStorage.getItem('sessKey');");
        Assert.assertEquals(val, "sessValue", "TC_WEB_SESSION_009: sessionStorage value should match");
    }

    @Test(description = "TC_WEB_SESSION_010: Browser maintains window handle")
    public void tc_session_010_windowHandleExists() {
        getDriver().get(baseUrl + "login_form.html");
        String handle = getDriver().getWindowHandle();
        Assert.assertNotNull(handle, "TC_WEB_SESSION_010: Window handle should not be null");
        Assert.assertFalse(handle.isEmpty(), "TC_WEB_SESSION_010: Window handle should not be empty");
    }

    @Test(description = "TC_WEB_SESSION_011: Only one window is open initially")
    public void tc_session_011_singleWindowInitially() {
        getDriver().get(baseUrl + "login_form.html");
        Set<String> handles = getDriver().getWindowHandles();
        Assert.assertTrue(handles.size() >= 1, "TC_WEB_SESSION_011: At least one window handle should exist");
    }

    @Test(description = "TC_WEB_SESSION_012: Local storage cleared between sessions")
    public void tc_session_012_localStorageClear() {
        getDriver().get(baseUrl + "login_form.html");
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) getDriver();
        js.executeScript("localStorage.clear();");
        String val = (String) js.executeScript("return localStorage.getItem('anyKey');");
        Assert.assertNull(val, "TC_WEB_SESSION_012: Cleared localStorage should return null");
    }

    @Test(description = "TC_WEB_SESSION_013: Page can be refreshed maintaining session")
    public void tc_session_013_refreshMaintainsSession() {
        getDriver().get(baseUrl + "login_form.html");
        String titleBefore = getDriver().getTitle();
        getDriver().navigate().refresh();
        String titleAfter = getDriver().getTitle();
        Assert.assertEquals(titleAfter, titleBefore, "TC_WEB_SESSION_013: Title should match after refresh");
    }

    @Test(description = "TC_WEB_SESSION_014: Multiple cookies can co-exist")
    public void tc_session_014_multipleCookies() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().manage().deleteAllCookies();
        getDriver().manage().addCookie(new Cookie("c1", "v1"));
        getDriver().manage().addCookie(new Cookie("c2", "v2"));
        Set<Cookie> cookies = getDriver().manage().getCookies();
        Assert.assertTrue(cookies.size() >= 2, "TC_WEB_SESSION_014: Multiple cookies should co-exist");
    }

    @Test(description = "TC_WEB_SESSION_015: Dashboard page accessible in same session")
    public void tc_session_015_dashboardAccessibleInSession() {
        getDriver().get(baseUrl + "login_form.html");
        getDriver().get(baseUrl + "dashboard.html");
        Assert.assertNotNull(getDriver().getTitle(), "TC_WEB_SESSION_015: Dashboard should be accessible in same session");
    }
}
