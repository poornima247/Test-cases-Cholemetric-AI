package com.cholemetric.automation.pages;

/**
 * ScanResultsPage — Page Object for ScanResultsActivity
 */
public class ScanResultsPage extends BasePage {
    private static final String RESULT_TITLE   = "com.cholemetric.app:id/tvResultTitle";
    private static final String RESULT_STATUS  = "com.cholemetric.app:id/tvDiagnosis";
    private static final String CONFIDENCE     = "com.cholemetric.app:id/tvConfidence";
    private static final String PATIENT_NAME   = "com.cholemetric.app:id/tvPatientName";
    private static final String SAVE_BTN       = "com.cholemetric.app:id/btnSave";
    private static final String NEW_ANALYSIS_BTN = "com.cholemetric.app:id/btnNewAnalysis";
    private static final String BACK_BTN       = "com.cholemetric.app:id/ivBack";
    private static final String SCAN_IMAGE     = "com.cholemetric.app:id/ivScanResult";
    private static final String SHARE_BTN      = "com.cholemetric.app:id/btnShare";
    private static final String REPORT_BTN     = "com.cholemetric.app:id/btnViewReport";

    public ScanResultsPage() { super(); }

    public void clickSave()       { tapById(SAVE_BTN); }
    public void clickNewAnalysis(){ tapById(NEW_ANALYSIS_BTN); }
    public void clickBack()       { tapById(BACK_BTN); }
    public void clickShare()      { tapById(SHARE_BTN); }
    public void clickViewReport() { tapById(REPORT_BTN); }

    public boolean isResultPageVisible()    { return isVisibleById(RESULT_TITLE); }
    public boolean isDiagnosisVisible()     { return isVisibleById(RESULT_STATUS); }
    public boolean isConfidenceVisible()    { return isVisibleById(CONFIDENCE); }
    public boolean isSaveButtonVisible()    { return isVisibleById(SAVE_BTN); }
    public boolean isScanImageVisible()     { return isVisibleById(SCAN_IMAGE); }
    public boolean isOnScanResultsActivity(){ return isOnActivity("ScanResultsActivity"); }

    public String getDiagnosisText() {
        try { return getTextById(RESULT_STATUS); } catch (Exception e) { return ""; }
    }
    public String getConfidenceText() {
        try { return getTextById(CONFIDENCE); } catch (Exception e) { return ""; }
    }
    public String getPatientName() {
        try { return getTextById(PATIENT_NAME); } catch (Exception e) { return ""; }
    }
}
