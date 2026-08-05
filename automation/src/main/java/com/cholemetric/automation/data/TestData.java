package com.cholemetric.automation.data;

/**
 * TestData — Centralized test data repository for all test cases.
 */
public class TestData {

    private TestData() {}

    // ── Authentication ────────────────────────────────────────────────────────
    public static final String VALID_EMAIL            = "test@cholemetric.com";
    public static final String VALID_PASSWORD         = "TestPass@123";
    public static final String WRONG_PASSWORD         = "WrongPass@456";
    public static final String INVALID_EMAIL_FORMAT   = "notanemail";
    public static final String INVALID_EMAIL_NO_TLD   = "test@nodomain";
    public static final String EMPTY_STRING           = "";
    public static final String SPACES_ONLY            = "   ";
    public static final String VERY_LONG_EMAIL        = "a".repeat(100) + "@example.com";
    public static final String SQL_INJECTION_EMAIL    = "' OR '1'='1";
    public static final String XSS_EMAIL              = "<script>alert('xss')</script>@test.com";
    public static final String SPECIAL_CHARS_EMAIL    = "test+tag@exam-ple.co.in";
    public static final String UNICODE_EMAIL          = "üser@möbius.de";
    public static final String VERY_LONG_PASSWORD     = "P@ssw0rd" + "a".repeat(200);
    public static final String SPECIAL_CHARS_PASSWORD = "P@$$w0rd!#%^&*()";

    // ── Registration ──────────────────────────────────────────────────────────
    public static final String NEW_DOCTOR_NAME        = "Dr. Jane Smith";
    public static final String NEW_DOCTOR_EMAIL       = "new.doctor." + System.currentTimeMillis() + "@hospital.com";
    public static final String NEW_DOCTOR_PASSWORD    = "NewPass@2024";
    public static final String NEW_DOCTOR_HOSPITAL    = "City General Hospital";
    public static final String NEW_DOCTOR_SPEC        = "Radiology";
    public static final String EXISTING_EMAIL         = "test@cholemetric.com"; // already registered
    public static final String SHORT_NAME             = "A";
    public static final String VERY_LONG_NAME         = "Dr. " + "A".repeat(200);
    public static final String NUMERIC_NAME           = "12345";
    public static final String SPECIAL_CHARS_NAME     = "Dr. O'Brien-Smith";

    // ── Profile ───────────────────────────────────────────────────────────────
    public static final String UPDATED_NAME           = "Dr. Updated Name";
    public static final String UPDATED_HOSPITAL       = "New City Hospital";
    public static final String UPDATED_SPECIALIZATION = "Cardiology";
    public static final String EMPTY_NAME             = "";
    public static final String VERY_LONG_HOSPITAL     = "H".repeat(300);

    // ── Password Change ───────────────────────────────────────────────────────
    public static final String NEW_VALID_PASSWORD     = "NewValidPass@789";
    public static final String SHORT_PASSWORD         = "123";
    public static final String MISMATCHED_PASSWORD    = "Different@Pass";

    // ── Patient / Analysis Data ───────────────────────────────────────────────
    public static final String PATIENT_NAME_1         = "John Doe";
    public static final String PATIENT_NAME_2         = "Jane Smith";
    public static final String PATIENT_AGE_VALID      = "45";
    public static final String PATIENT_AGE_ZERO       = "0";
    public static final String PATIENT_AGE_NEGATIVE   = "-5";
    public static final String PATIENT_AGE_OVER_150   = "200";
    public static final String PATIENT_ID_1           = "PAT-2024-001";
    public static final String PATIENT_ID_2           = "PAT-2024-002";
    public static final String PATIENT_ID_SPECIAL     = "PAT/2024#001";
    public static final String NOTES_SHORT            = "Routine checkup";
    public static final String NOTES_LONG             = "Patient presents with " + "detailed ".repeat(50) + "symptoms.";
    public static final String NOTES_SPECIAL_CHARS    = "Notes: <b>bold</b> & 'quoted' text";

    // ── Search Data ───────────────────────────────────────────────────────────
    public static final String SEARCH_VALID_PATIENT   = "John";
    public static final String SEARCH_NO_RESULTS      = "ZZZNORESULTSZZZ";
    public static final String SEARCH_SINGLE_CHAR     = "J";
    public static final String SEARCH_NUMBERS         = "123";
    public static final String SEARCH_SPECIAL         = "@#$";
    public static final String SEARCH_EMPTY           = "";

    // ── Forgot Password ───────────────────────────────────────────────────────
    public static final String RESET_VALID_EMAIL      = "test@cholemetric.com";
    public static final String RESET_UNKNOWN_EMAIL    = "unknown@nowhere.com";
    public static final String RESET_INVALID_FORMAT   = "notvalid";

    // ── UI Text (expected values) ─────────────────────────────────────────────
    public static final String EXPECTED_APP_NAME      = "Cholemetric";
    public static final String EXPECTED_WELCOME_TEXT  = "Welcome";
    public static final String EXPECTED_LOGIN_TITLE   = "Login";
    public static final String EXPECTED_DASHBOARD_BTN = "New Analysis";
    public static final String EXPECTED_SETTINGS_TITLE= "Settings";
    public static final String EXPECTED_FAQ_TITLE     = "Help & FAQ";

    // ── Error Messages ────────────────────────────────────────────────────────
    public static final String ERR_INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ERR_MISSING_FIELDS      = "Missing required fields";
    public static final String ERR_EMAIL_EXISTS        = "Email already exists";
    public static final String ERR_WRONG_CURRENT_PASS  = "Current password is incorrect";
    public static final String ERR_PASS_MISMATCH       = "Passwords do not match";

    // ── Test Boundaries ───────────────────────────────────────────────────────
    public static final int    MAX_LOGIN_ATTEMPTS      = 5;
    public static final int    SPLASH_DURATION_MS      = 3000;
    public static final int    API_TIMEOUT_MS          = 10000;
    public static final int    ANIMATION_DURATION_MS   = 500;
}
