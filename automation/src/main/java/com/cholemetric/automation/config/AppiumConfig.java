package com.cholemetric.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * AppiumConfig — Loads and provides access to all configuration properties.
 */
public class AppiumConfig {

    private static final Properties props = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream is = AppiumConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                // Try system resource fallback
                try (InputStream isAlt = AppiumConfig.class.getResourceAsStream("/" + CONFIG_FILE)) {
                    if (isAlt != null) props.load(isAlt);
                }
            } else {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private AppiumConfig() {}

    public static String get(String key) {
        // Check system property first, then env, then config file
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) return sysProp;
        String envVar = System.getenv(key.replace(".", "_").toUpperCase());
        if (envVar != null && !envVar.isEmpty()) return envVar;
        return props.getProperty(key, "");
    }

    public static int getInt(String key, int defaultValue) {
        try { return Integer.parseInt(get(key)); } catch (NumberFormatException e) { return defaultValue; }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val.isEmpty()) return defaultValue;
        return Boolean.parseBoolean(val);
    }

    // ── Appium ────────────────────────────────────────────────────────────────
    public static String getAppiumUrl()          { return get("appium.url"); }
    public static String getPlatform()           { return get("appium.platform"); }
    public static String getDeviceName()         { return get("appium.device.name"); }
    public static String getPlatformVersion()    { return get("appium.platform.version"); }
    public static String getAppPackage()         { return get("appium.app.package"); }
    public static String getAppActivity()        { return get("appium.app.activity"); }
    public static String getAppPath()            { return get("appium.app.path"); }
    public static String getAutomationName()     { return get("appium.automation.name"); }
    public static int    getImplicitWait()       { return getInt("appium.implicit.wait", 10); }
    public static int    getExplicitWait()       { return getInt("appium.explicit.wait", 20); }
    public static boolean isNoReset()            { return getBoolean("appium.no.reset", false); }
    public static int    getNewCommandTimeout()  { return getInt("appium.new.command.timeout", 300); }

    // ── Test Config ───────────────────────────────────────────────────────────
    public static int    getRetryCount()                 { return getInt("test.retry.count", 2); }
    public static boolean isScreenshotOnFailure()        { return getBoolean("test.screenshot.on.failure", true); }
    public static int    getParallelThreads()            { return getInt("test.parallel.threads", 1); }

    // ── Report ────────────────────────────────────────────────────────────────
    public static String getReportOutputDir()     { return get("report.output.dir"); }
    public static String getHtmlReportDir()       { return get("report.html.dir"); }
    public static String getExcelReportDir()      { return get("report.excel.dir"); }
    public static String getJsonReportDir()       { return get("report.json.dir"); }
    public static String getScreenshotsDir()      { return get("report.screenshots.dir"); }
    public static String getLogsDir()             { return get("report.logs.dir"); }

    // ── Credentials ───────────────────────────────────────────────────────────
    public static String getValidEmail()          { return get("test.valid.email"); }
    public static String getValidPassword()       { return get("test.valid.password"); }
    public static String getInvalidEmail()        { return get("test.invalid.email"); }
    public static String getInvalidPassword()     { return get("test.invalid.password"); }
    public static String getNewEmail()            { return get("test.new.email"); }
    public static String getNewPassword()         { return get("test.new.password"); }
    public static String getDoctorName()          { return get("test.doctor.name"); }
    public static String getHospital()            { return get("test.hospital"); }
    public static String getSpecialization()      { return get("test.specialization"); }
}
