package com.cholemetric.automation.pages;

import java.util.List;
import org.openqa.selenium.WebElement;

/**
 * PatientScansPage — Page Object for PatientScansActivity
 */
public class PatientScansPage extends BasePage {
    private static final String SCANS_LIST    = "com.cholemetric.app:id/rvPatientScans";
    private static final String SCAN_ITEM     = "com.cholemetric.app:id/scanItem";
    private static final String SEARCH_FIELD  = "com.cholemetric.app:id/etSearch";
    private static final String EMPTY_STATE   = "com.cholemetric.app:id/tvEmptyScans";
    private static final String BACK_BTN      = "com.cholemetric.app:id/ivBack";
    private static final String PAGE_TITLE    = "com.cholemetric.app:id/tvTitle";
    private static final String FILTER_BTN    = "com.cholemetric.app:id/ivFilter";
    private static final String CLEAR_ALL_BTN = "com.cholemetric.app:id/btnClearAll";
    private static final String PROGRESS_BAR  = "com.cholemetric.app:id/progressBar";

    public PatientScansPage() { super(); }

    public void clickBack()         { tapById(BACK_BTN); }
    public void clickFilter()       { tapById(FILTER_BTN); }
    public void clickClearAll()     { tapById(CLEAR_ALL_BTN); }
    public void enterSearch(String q) { typeTextById(SEARCH_FIELD, q); }
    public void clearSearch()        { findById(SEARCH_FIELD).clear(); }

    public boolean isScansListVisible()   { return isVisibleById(SCANS_LIST); }
    public boolean isEmptyStateVisible()  { return isVisibleById(EMPTY_STATE); }
    public boolean isSearchFieldVisible() { return isVisibleById(SEARCH_FIELD); }
    public boolean isProgressBarVisible() { return isVisibleById(PROGRESS_BAR); }
    public boolean isOnPatientScansActivity() { return isOnActivity("PatientScansActivity"); }

    public List<WebElement> getScanItems() {
        return findAllById(SCAN_ITEM);
    }

    public int getScanCount() {
        return getScanItems().size();
    }

    public void clickScanItem(int index) {
        List<WebElement> items = getScanItems();
        if (index < items.size()) {
            items.get(index).click();
        }
    }

    public String getPageTitle() {
        try { return getTextById(PAGE_TITLE); } catch (Exception e) { return ""; }
    }
}
