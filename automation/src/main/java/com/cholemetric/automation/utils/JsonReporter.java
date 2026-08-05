package com.cholemetric.automation.utils;

import com.cholemetric.automation.config.AppiumConfig;
import com.cholemetric.automation.listeners.TestListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JsonReporter — Exports test results to JSON and generates Markdown summary.
 */
public class JsonReporter {

    private static final Logger log = LoggerFactory.getLogger(JsonReporter.class);

    private JsonReporter() {}

    public static void generate() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String jsonDir = AppiumConfig.getJsonReportDir();
        new File(jsonDir).mkdirs();

        List<TestListener.TestResult> all     = TestListener.getAllResults();
        List<TestListener.TestResult> passed  = all.stream().filter(r -> "PASS".equals(r.status)).collect(Collectors.toList());
        List<TestListener.TestResult> failed  = all.stream().filter(r -> "FAIL".equals(r.status)).collect(Collectors.toList());
        List<TestListener.TestResult> skipped = all.stream().filter(r -> "SKIP".equals(r.status)).collect(Collectors.toList());

        double passRate = all.size() > 0 ? (passed.size() * 100.0 / all.size()) : 0;
        long totalDuration = all.stream().mapToLong(r -> r.durationMs).sum();

        // Build JSON structure
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportTitle",        "Cholemetric Android E2E Execution Report");
        report.put("generatedAt",        timestamp);
        report.put("appPackage",         "com.cholemetric.app");
        report.put("platform",           "Android");
        report.put("device",             AppiumConfig.getDeviceName());
        report.put("framework",          "Appium 2.x + Java + TestNG");
        report.put("totalTests",         all.size());
        report.put("passed",             passed.size());
        report.put("failed",             failed.size());
        report.put("skipped",            skipped.size());
        report.put("passRate",           String.format("%.2f%%", passRate));
        report.put("totalDurationMs",    totalDuration);

        // Test results
        report.put("results", all.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("testId",        r.testId);
            m.put("module",        r.module);
            m.put("testName",      r.testName);
            m.put("priority",      r.priority);
            m.put("status",        r.status);
            m.put("durationMs",    r.durationMs);
            m.put("failureReason", r.failureReason);
            m.put("screenshotPath",r.screenshotPath);
            return m;
        }).collect(Collectors.toList()));

        // Write JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String jsonPath = jsonDir + File.separator + "execution-results.json";
            mapper.writeValue(new File(jsonPath), report);
            log.info("JSON report generated: {}", jsonPath);
        } catch (IOException e) {
            log.error("Failed to write JSON report: {}", e.getMessage());
        }

        // Generate summary.md
        generateMarkdownSummary(all, passed, failed, skipped, passRate, totalDuration, timestamp);
    }

    private static void generateMarkdownSummary(
            List<TestListener.TestResult> all,
            List<TestListener.TestResult> passed,
            List<TestListener.TestResult> failed,
            List<TestListener.TestResult> skipped,
            double passRate, long totalDuration, String timestamp) {

        String summaryDir = AppiumConfig.getReportOutputDir() + File.separator + "Summary";
        new File(summaryDir).mkdirs();

        StringBuilder sb = new StringBuilder();
        sb.append("# 📱 Android Appium E2E Execution Summary\n\n");
        sb.append("| Field | Value |\n|---|---|\n");
        sb.append("| **Build Number** | `").append(System.getenv().getOrDefault("GITHUB_RUN_NUMBER", "LOCAL")).append("` |\n");
        sb.append("| **Execution Date** | `").append(timestamp).append("` |\n");
        sb.append("| **Git Commit** | `").append(System.getenv().getOrDefault("GITHUB_SHA", "N/A")).append("` |\n");
        sb.append("| **Branch** | `").append(System.getenv().getOrDefault("GITHUB_REF_NAME", "local")).append("` |\n");
        sb.append("| **App Package** | `com.cholemetric.app` |\n");
        sb.append("| **Device** | `").append(AppiumConfig.getDeviceName()).append("` |\n");
        sb.append("| **Android Version** | `").append(AppiumConfig.getPlatformVersion()).append("` |\n\n");

        sb.append("## 📊 Execution Metrics\n\n");
        sb.append("| Metric | Value |\n|---|---|\n");
        sb.append("| **Total Test Cases** | `").append(all.size()).append("` |\n");
        sb.append("| **Executed** | `").append(all.size()).append("` |\n");
        sb.append("| **Passed** | `✅ ").append(passed.size()).append("` |\n");
        sb.append("| **Failed** | `❌ ").append(failed.size()).append("` |\n");
        sb.append("| **Skipped** | `⏭️ ").append(skipped.size()).append("` |\n");
        sb.append("| **Pass Rate** | `").append(String.format("%.2f%%", passRate)).append("` |\n");
        sb.append("| **Fail Rate** | `").append(String.format("%.2f%%", 100 - passRate)).append("` |\n");
        sb.append("| **Total Duration** | `").append(totalDuration).append("ms` |\n\n");

        if (!passed.isEmpty()) {
            sb.append("## ✅ Passed Tests\n\n");
            passed.stream().limit(20).forEach(r ->
                sb.append("- `").append(r.testId).append("` — ").append(r.testName).append("\n"));
            if (passed.size() > 20) sb.append("- ... and ").append(passed.size() - 20).append(" more\n");
            sb.append("\n");
        }

        if (!failed.isEmpty()) {
            sb.append("## ❌ Failed Tests\n\n");
            failed.forEach(r -> {
                sb.append("- `").append(r.testId).append("` — ").append(r.testName).append("\n");
                sb.append("  - **Reason**: ").append(r.failureReason != null ? r.failureReason : "Unknown").append("\n");
            });
            sb.append("\n");
        }

        if (!skipped.isEmpty()) {
            sb.append("## ⏭️ Skipped Tests\n\n");
            skipped.forEach(r ->
                sb.append("- `").append(r.testId).append("` — ").append(r.testName).append("\n"));
            sb.append("\n");
        }

        sb.append("---\n*Generated by Cholemetric Appium Framework*\n");

        try (FileWriter fw = new FileWriter(summaryDir + File.separator + "summary.md")) {
            fw.write(sb.toString());
            log.info("Markdown summary generated.");
        } catch (IOException e) {
            log.error("Failed to write summary.md: {}", e.getMessage());
        }
    }
}
