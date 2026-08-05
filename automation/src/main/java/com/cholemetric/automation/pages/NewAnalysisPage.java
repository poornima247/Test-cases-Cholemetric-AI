package com.cholemetric.automation.pages;

/**
 * NewAnalysisPage — Page Object for NewAnalysisActivity
 */
public class NewAnalysisPage extends BasePage {
    private static final String PATIENT_NAME    = "com.cholemetric.app:id/etPatientName";
    private static final String PATIENT_AGE     = "com.cholemetric.app:id/etPatientAge";
    private static final String PATIENT_ID      = "com.cholemetric.app:id/etPatientId";
    private static final String UPLOAD_BTN      = "com.cholemetric.app:id/btnUploadScan";
    private static final String ANALYZE_BTN     = "com.cholemetric.app:id/btnAnalyze";
    private static final String SCAN_PREVIEW    = "com.cholemetric.app:id/ivScanPreview";
    private static final String ERROR_TEXT      = "com.cholemetric.app:id/tvError";
    private static final String PROGRESS_BAR    = "com.cholemetric.app:id/progressBar";
    private static final String NOTES_FIELD     = "com.cholemetric.app:id/etNotes";
    private static final String BACK_BTN        = "com.cholemetric.app:id/ivBack";

    public NewAnalysisPage() { super(); }

    public NewAnalysisPage enterPatientName(String name) { typeTextById(PATIENT_NAME, name); return this; }
    public NewAnalysisPage enterPatientAge(String age)   { typeTextById(PATIENT_AGE, age); return this; }
    public NewAnalysisPage enterPatientId(String id)     { typeTextById(PATIENT_ID, id); return this; }
    public NewAnalysisPage enterNotes(String notes)      { typeTextById(NOTES_FIELD, notes); return this; }
    public void clickUpload()   { tapById(UPLOAD_BTN); }
    public void clickAnalyze()  { tapById(ANALYZE_BTN); }
    public void clickBack()     { tapById(BACK_BTN); }

    public boolean isNewAnalysisPageVisible()  { return isVisibleById(PATIENT_NAME); }
    public boolean isUploadButtonVisible()     { return isVisibleById(UPLOAD_BTN); }
    public boolean isAnalyzeButtonVisible()    { return isVisibleById(ANALYZE_BTN); }
    public boolean isScanPreviewVisible()      { return isVisibleById(SCAN_PREVIEW); }
    public boolean isErrorVisible()            { return isVisibleById(ERROR_TEXT); }
    public boolean isProgressBarVisible()      { return isVisibleById(PROGRESS_BAR); }
    public boolean isOnNewAnalysisActivity()   { return isOnActivity("NewAnalysisActivity"); }

    public String getErrorMessage() {
        try { return getTextById(ERROR_TEXT); } catch (Exception e) { return ""; }
    }
}
