package com.cholemetric.automation.pages;

/**
 * ScanReportPage — Page Object for ScanReportActivity
 */
public class ScanReportPage extends BasePage {
    private static final String REPORT_TITLE   = "com.cholemetric.app:id/tvReportTitle";
    private static final String DIAGNOSIS      = "com.cholemetric.app:id/tvDiagnosis";
    private static final String CONFIDENCE     = "com.cholemetric.app:id/tvConfidence";
    private static final String PATIENT_NAME   = "com.cholemetric.app:id/tvPatientName";
    private static final String DATE_TEXT      = "com.cholemetric.app:id/tvDate";
    private static final String NOTES_TEXT     = "com.cholemetric.app:id/tvNotes";
    private static final String DELETE_BTN     = "com.cholemetric.app:id/btnDelete";
    private static final String SHARE_BTN      = "com.cholemetric.app:id/btnShare";
    private static final String BACK_BTN       = "com.cholemetric.app:id/ivBack";
    private static final String SCAN_IMAGE     = "com.cholemetric.app:id/ivScan";
    private static final String CONFIRM_DELETE = "com.cholemetric.app:id/btnConfirmDelete";

    public ScanReportPage() { super(); }

    public void clickDelete()       { tapById(DELETE_BTN); }
    public void clickShare()        { tapById(SHARE_BTN); }
    public void clickBack()         { tapById(BACK_BTN); }
    public void clickConfirmDelete(){ tapById(CONFIRM_DELETE); }

    public boolean isReportPageVisible()   { return isVisibleById(REPORT_TITLE); }
    public boolean isDiagnosisVisible()    { return isVisibleById(DIAGNOSIS); }
    public boolean isDeleteButtonVisible() { return isVisibleById(DELETE_BTN); }
    public boolean isScanImageVisible()    { return isVisibleById(SCAN_IMAGE); }
    public boolean isOnScanReportActivity(){ return isOnActivity("ScanReportActivity"); }

    public String getDiagnosis()    { try { return getTextById(DIAGNOSIS); }    catch (Exception e) { return ""; } }
    public String getConfidence()   { try { return getTextById(CONFIDENCE); }   catch (Exception e) { return ""; } }
    public String getPatientName()  { try { return getTextById(PATIENT_NAME); } catch (Exception e) { return ""; } }
    public String getDate()         { try { return getTextById(DATE_TEXT); }     catch (Exception e) { return ""; } }
}
