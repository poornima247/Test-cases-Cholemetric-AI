package com.cholemetric.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.cholemetric.automation.config.AppiumConfig;
import com.cholemetric.automation.listeners.TestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * HtmlReporter — Generates rich HTML reports using ExtentReports.
 */
public class HtmlReporter {

    private static final Logger log = LoggerFactory.getLogger(HtmlReporter.class);
    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;

    private HtmlReporter() {}

    public static void initReport() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dir = AppiumConfig.getHtmlReportDir();
        new File(dir).mkdirs();

        String reportPath = dir + File.separator + "execution-report.html";
        sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Cholemetric E2E Test Report");
        sparkReporter.config().setReportName("Android Appium E2E — Cholemetric");
        sparkReporter.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");
        sparkReporter.config().setCss(
            ".badge-primary { background-color: #1565C0 !important; } " +
            ".card { border-radius:8px !important; } " +
            "body { font-family: 'Segoe UI', sans-serif !important; }"
        );

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("App", "Cholemetric Android");
        extent.setSystemInfo("Platform", "Android " + AppiumConfig.getPlatformVersion());
        extent.setSystemInfo("Device", AppiumConfig.getDeviceName());
        extent.setSystemInfo("Framework", "Appium 2.x + Java + TestNG");
        extent.setSystemInfo("App Package", AppiumConfig.getAppPackage());
        extent.setSystemInfo("Report Generated", timestamp);
    }

    public static void generateReport() {
        if (extent == null) { initReport(); }

        List<TestListener.TestResult> results = TestListener.getAllResults();
        for (TestListener.TestResult r : results) {
            ExtentTest test = extent.createTest(
                "[" + r.testId + "] " + r.testName,
                "Module: " + r.module + " | Priority: " + r.priority
            );
            test.assignCategory(r.module);

            if ("PASS".equals(r.status)) {
                test.pass("✅ Test PASSED — Duration: " + r.durationMs + "ms");
                if (r.screenshotPath != null && !r.screenshotPath.isEmpty()) {
                    try { test.addScreenCaptureFromPath(r.screenshotPath, "Pass Screenshot"); }
                    catch (Exception e) { log.warn("Could not attach screenshot"); }
                }
            } else if ("FAIL".equals(r.status)) {
                test.fail("❌ Test FAILED — " + (r.failureReason != null ? r.failureReason : "Unknown reason"));
                if (r.screenshotPath != null && !r.screenshotPath.isEmpty()) {
                    try { test.addScreenCaptureFromPath(r.screenshotPath, "Failure Screenshot"); }
                    catch (Exception e) { log.warn("Could not attach screenshot"); }
                }
                if (r.stackTrace != null && !r.stackTrace.isEmpty()) {
                    test.fail("<pre>" + r.stackTrace + "</pre>");
                }
            } else {
                test.skip("⏭️ Test SKIPPED — " + (r.failureReason != null ? r.failureReason : "Skipped by framework"));
            }
        }
        extent.flush();
        log.info("HTML report generated at: {}", AppiumConfig.getHtmlReportDir());
    }

    public static ExtentReports getExtent() {
        if (extent == null) initReport();
        return extent;
    }
}
