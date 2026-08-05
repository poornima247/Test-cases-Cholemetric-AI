package com.cholemetric.automation.pages;

import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

/**
 * DashboardPage — Page Object for DashboardActivity
 */
public class DashboardPage extends BasePage {
    private static final String WELCOME_TEXT      = "com.cholemetric.app:id/tvWelcomeName";
    private static final String DOCTOR_NAME       = "com.cholemetric.app:id/tvDoctorName";
    private static final String NEW_ANALYSIS_BTN  = "com.cholemetric.app:id/btnNewAnalysis";
    private static final String PATIENT_SCANS_BTN = "com.cholemetric.app:id/btnPatientScans";
    private static final String SETTINGS_BTN      = "com.cholemetric.app:id/btnSettings";
    private static final String SCAN_COUNT        = "com.cholemetric.app:id/tvScanCount";
    private static final String RECENT_SCANS_LIST = "com.cholemetric.app:id/rvRecentScans";
    private static final String LOGOUT_ICON       = "com.cholemetric.app:id/ivLogout";
    private static final String HOSPITAL_TEXT     = "com.cholemetric.app:id/tvHospital";
    private static final String PROGRESS_BAR      = "com.cholemetric.app:id/progressBar";
    private static final String EMPTY_STATE       = "com.cholemetric.app:id/tvEmptyState";

    public DashboardPage() { super(); }

    public void clickNewAnalysis()    { tapById(NEW_ANALYSIS_BTN); }
    public void clickPatientScans()   { tapById(PATIENT_SCANS_BTN); }
    public void clickSettings()       { tapById(SETTINGS_BTN); }
    public void clickLogout()         { tapById(LOGOUT_ICON); }

    public boolean isDashboardVisible()       { return isVisibleById(NEW_ANALYSIS_BTN); }
    public boolean isWelcomeMessageVisible()  { return isVisibleById(WELCOME_TEXT); }
    public boolean isDoctorNameVisible()      { return isVisibleById(DOCTOR_NAME); }
    public boolean isNewAnalysisBtnVisible()  { return isVisibleById(NEW_ANALYSIS_BTN); }
    public boolean isPatientScansBtnVisible() { return isVisibleById(PATIENT_SCANS_BTN); }
    public boolean isSettingsBtnVisible()     { return isVisibleById(SETTINGS_BTN); }
    public boolean isLogoutIconVisible()      { return isVisibleById(LOGOUT_ICON); }
    public boolean isProgressBarVisible()     { return isVisibleById(PROGRESS_BAR); }
    public boolean isEmptyStateVisible()      { return isVisibleById(EMPTY_STATE); }
    public boolean isOnDashboardActivity()    { return isOnActivity("DashboardActivity"); }

    public String getDoctorName() {
        try { return getTextById(DOCTOR_NAME); } catch (Exception e) { return ""; }
    }
    public String getScanCount() {
        try { return getTextById(SCAN_COUNT); } catch (Exception e) { return "0"; }
    }
    public String getHospitalText() {
        try { return getTextById(HOSPITAL_TEXT); } catch (Exception e) { return ""; }
    }

    public boolean isRecentScansListVisible() {
        return isVisibleById(RECENT_SCANS_LIST);
    }

    public List<WebElement> getRecentScansItems() {
        return findAllById("com.cholemetric.app:id/scanItem");
    }

    public void waitForDashboardToLoad() {
        waitForVisibilityById(NEW_ANALYSIS_BTN);
    }

    /** Alias for clickLogout() — taps the logout icon */
    public void logout() { clickLogout(); }

    /** Alias for clickSettings() — opens the Settings screen */
    public void openSettings() { clickSettings(); }
}
