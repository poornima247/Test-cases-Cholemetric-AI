package com.cholemetric.automation.listeners;

import com.cholemetric.automation.drivers.DriverManager;
import com.cholemetric.automation.utils.ScreenshotUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TestListener — TestNG listener for capturing pass/fail status,
 * screenshots on failure, and building the test result list.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static final List<TestResult> results = Collections.synchronizedList(new ArrayList<>());

    // ── ITestListener Callbacks ───────────────────────────────────────────────

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ STARTING: [{}] {}", getTestId(result), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✅ PASSED: [{}] {} — {}ms", getTestId(result), result.getName(), getDuration(result));
        TestResult tr = buildResult(result, "PASS", null, null);
        if (DriverManager.isDriverInitialized()) {
            tr.screenshotPath = ScreenshotUtil.captureOnPass(DriverManager.getDriver(), result.getName());
        }
        results.add(tr);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("❌ FAILED: [{}] {} — {}ms", getTestId(result), result.getName(), getDuration(result));
        String failReason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown failure";
        String stackTrace = getStackTrace(result.getThrowable());

        String screenshotPath = "";
        if (DriverManager.isDriverInitialized()) {
            screenshotPath = ScreenshotUtil.captureOnFailure(DriverManager.getDriver(), result.getName());
        }
        TestResult tr = buildResult(result, "FAIL", failReason, stackTrace);
        tr.screenshotPath = screenshotPath;
        results.add(tr);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⏭️ SKIPPED: [{}] {}", getTestId(result), result.getName());
        String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Skipped";
        results.add(buildResult(result, "SKIP", reason, null));
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        log.error("⏱️ TIMEOUT: [{}] {}", getTestId(result), result.getName());
        onTestFailure(result);
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("═══ TEST SUITE STARTED: {} ═══", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("═══ TEST SUITE FINISHED: {} ═══", context.getName());
        log.info("TOTAL: {} | PASSED: {} | FAILED: {} | SKIPPED: {}",
            results.size(),
            results.stream().filter(r -> "PASS".equals(r.status)).count(),
            results.stream().filter(r -> "FAIL".equals(r.status)).count(),
            results.stream().filter(r -> "SKIP".equals(r.status)).count());
    }

    // ── Static Accessors ──────────────────────────────────────────────────────

    public static List<TestResult> getAllResults() { return Collections.unmodifiableList(results); }
    public static void clearResults() { results.clear(); }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TestResult buildResult(ITestResult r, String status, String reason, String stack) {
        TestResult tr = new TestResult();
        tr.testId       = getTestId(r);
        tr.module       = getModule(r);
        tr.testName     = r.getName();
        tr.priority     = getPriority(r);
        tr.status       = status;
        tr.durationMs   = getDuration(r);
        tr.failureReason = reason;
        tr.stackTrace   = stack;
        return tr;
    }

    private String getTestId(ITestResult r) {
        // Extract TC_XXX_NNN from method name or description
        String desc = r.getMethod().getDescription();
        if (desc != null && desc.contains(" - ")) {
            return desc.split(" - ")[0].trim();
        }
        String name = r.getName();
        if (name.startsWith("TC_")) {
            String[] parts = name.split("_");
            if (parts.length >= 3) return parts[0] + "_" + parts[1] + "_" + parts[2];
        }
        return "TC_UNKNOWN";
    }

    private String getModule(ITestResult r) {
        String[] groups = r.getMethod().getGroups();
        if (groups.length > 0) return groups[0].toUpperCase();
        return r.getTestClass().getRealClass().getSimpleName()
            .replace("Tests", "").replace("Test", "");
    }

    private String getPriority(ITestResult r) {
        int p = r.getMethod().getPriority();
        if (p <= 1) return "CRITICAL";
        if (p <= 3) return "HIGH";
        if (p <= 5) return "MEDIUM";
        return "LOW";
    }

    private long getDuration(ITestResult r) {
        return r.getEndMillis() - r.getStartMillis();
    }

    private String getStackTrace(Throwable t) {
        if (t == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n");
        for (StackTraceElement e : t.getStackTrace()) {
            if (sb.length() > 2000) { sb.append("... (truncated)"); break; }
            sb.append("  at ").append(e.toString()).append("\n");
        }
        return sb.toString();
    }

    // ── TestResult Model ──────────────────────────────────────────────────────

    public static class TestResult {
        public String testId;
        public String module;
        public String testName;
        public String priority;
        public String status;         // PASS / FAIL / SKIP
        public long   durationMs;
        public String failureReason;
        public String stackTrace;
        public String screenshotPath;
    }
}
