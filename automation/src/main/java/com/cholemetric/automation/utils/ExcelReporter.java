package com.cholemetric.automation.utils;

import com.cholemetric.automation.config.AppiumConfig;
import com.cholemetric.automation.listeners.TestListener;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExcelReporter — Generates 7-sheet Excel report using Apache POI.
 * Sheets: All Tests, Passed, Failed, Skipped, Metrics, Defects, Pass Rate by Module
 */
public class ExcelReporter {

    private static final Logger log = LoggerFactory.getLogger(ExcelReporter.class);

    // Cell style colors
    private static final String COLOR_PASS    = "63BE7B"; // Green
    private static final String COLOR_FAIL    = "FF4444"; // Red
    private static final String COLOR_SKIP    = "FFD700"; // Gold
    private static final String COLOR_HEADER  = "1A237E"; // Dark blue
    private static final String COLOR_TITLE   = "0D47A1"; // Blue
    private static final String COLOR_ALT     = "E8EAF6"; // Light blue alt row

    private ExcelReporter() {}

    public static void generate() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dir = AppiumConfig.getExcelReportDir();
        new File(dir).mkdirs();

        List<TestListener.TestResult> all     = TestListener.getAllResults();
        List<TestListener.TestResult> passed  = all.stream().filter(r -> "PASS".equals(r.status)).collect(Collectors.toList());
        List<TestListener.TestResult> failed  = all.stream().filter(r -> "FAIL".equals(r.status)).collect(Collectors.toList());
        List<TestListener.TestResult> skipped = all.stream().filter(r -> "SKIP".equals(r.status)).collect(Collectors.toList());

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            // ── Sheet 1: All Test Cases ────────────────────────────────────────
            createTestSheet(wb, "All Test Cases", all, null, timestamp);

            // ── Sheet 2: Passed Tests ─────────────────────────────────────────
            createTestSheet(wb, "Passed Tests", passed, COLOR_PASS, timestamp);

            // ── Sheet 3: Failed Tests ─────────────────────────────────────────
            createTestSheet(wb, "Failed Tests", failed, COLOR_FAIL, timestamp);

            // ── Sheet 4: Skipped Tests ────────────────────────────────────────
            createTestSheet(wb, "Skipped Tests", skipped, COLOR_SKIP, timestamp);

            // ── Sheet 5: Execution Metrics ────────────────────────────────────
            createMetricsSheet(wb, all, passed, failed, skipped, timestamp);

            // ── Sheet 6: Defect Summary ───────────────────────────────────────
            createDefectSheet(wb, failed, timestamp);

            // ── Sheet 7: Pass Rate by Module ──────────────────────────────────
            createPassRateSheet(wb, all, timestamp);

            // Write main report
            String mainPath = dir + File.separator + "Automation_Test_Report_" + timestamp + ".xlsx";
            try (FileOutputStream fos = new FileOutputStream(mainPath)) {
                wb.write(fos);
            }
            log.info("Excel report generated: {}", mainPath);

            // Write separate passed/failed reports
            writeFilteredReport(dir, "Passed_Test_Cases_" + timestamp + ".xlsx", passed, COLOR_PASS);
            writeFilteredReport(dir, "Failed_Test_Cases_" + timestamp + ".xlsx", failed, COLOR_FAIL);
            writeExecutionSummary(dir, "Execution_Summary_" + timestamp + ".xlsx", all, passed, failed, skipped, timestamp);

        } catch (IOException e) {
            log.error("Failed to generate Excel report: {}", e.getMessage(), e);
        }
    }

    private static void createTestSheet(XSSFWorkbook wb, String sheetName,
                                        List<TestListener.TestResult> results,
                                        String rowColorHex, String timestamp) {
        XSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setColumnWidth(0, 4000);   // Test ID
        sheet.setColumnWidth(1, 4000);   // Module
        sheet.setColumnWidth(2, 10000);  // Test Name
        sheet.setColumnWidth(3, 2500);   // Priority
        sheet.setColumnWidth(4, 2500);   // Status
        sheet.setColumnWidth(5, 3500);   // Duration
        sheet.setColumnWidth(6, 12000);  // Failure Reason

        // Title row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Cholemetric Android E2E — " + sheetName + " — " + timestamp);
        titleCell.setCellStyle(createTitleStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // Header row
        Row headerRow = sheet.createRow(1);
        String[] headers = {"Test ID", "Module", "Test Name", "Priority", "Status", "Duration (ms)", "Failure Reason"};
        XSSFCellStyle headerStyle = createHeaderStyle(wb);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowNum = 2;
        boolean alternate = false;
        for (TestListener.TestResult r : results) {
            Row row = sheet.createRow(rowNum++);
            XSSFCellStyle rowStyle = createDataStyle(wb,
                rowColorHex != null ? rowColorHex : getStatusColor(r.status), alternate);

            row.createCell(0).setCellValue(r.testId);
            row.createCell(1).setCellValue(r.module);
            row.createCell(2).setCellValue(r.testName);
            row.createCell(3).setCellValue(r.priority);
            row.createCell(4).setCellValue(r.status);
            row.createCell(5).setCellValue(r.durationMs);
            row.createCell(6).setCellValue(r.failureReason != null ? r.failureReason : "");

            for (int c = 0; c < 7; c++) { row.getCell(c).setCellStyle(rowStyle); }
            alternate = !alternate;
        }

        // Auto filter
        if (results.size() > 0) {
            sheet.setAutoFilter(new CellRangeAddress(1, 1, 0, 6));
        }
    }

    private static void createMetricsSheet(XSSFWorkbook wb, List<TestListener.TestResult> all,
                                            List<TestListener.TestResult> passed,
                                            List<TestListener.TestResult> failed,
                                            List<TestListener.TestResult> skipped,
                                            String timestamp) {
        XSSFSheet sheet = wb.createSheet("Execution Metrics");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 5000);

        Row titleRow = sheet.createRow(0);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Cholemetric E2E — Execution Metrics — " + timestamp);
        tc.setCellStyle(createTitleStyle(wb));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        String[][] metrics = {
            {"Total Test Cases", String.valueOf(all.size())},
            {"Passed", String.valueOf(passed.size())},
            {"Failed", String.valueOf(failed.size())},
            {"Skipped", String.valueOf(skipped.size())},
            {"Pass Rate (%)", all.size() > 0 ?
                String.format("%.2f", (passed.size() * 100.0 / all.size())) + "%" : "0%"},
            {"Fail Rate (%)", all.size() > 0 ?
                String.format("%.2f", (failed.size() * 100.0 / all.size())) + "%" : "0%"},
            {"Total Duration (ms)", String.valueOf(all.stream().mapToLong(r -> r.durationMs).sum())},
            {"Report Generated", timestamp},
            {"App Package", "com.cholemetric.app"},
            {"Framework", "Appium 2.x + Java + TestNG"},
        };

        XSSFCellStyle labelStyle = createHeaderStyle(wb);
        XSSFCellStyle valueStyle = wb.createCellStyle();
        valueStyle.setAlignment(HorizontalAlignment.LEFT);

        int rowNum = 2;
        for (String[] row : metrics) {
            Row r = sheet.createRow(rowNum++);
            Cell lbl = r.createCell(0); lbl.setCellValue(row[0]); lbl.setCellStyle(labelStyle);
            Cell val = r.createCell(1); val.setCellValue(row[1]); val.setCellStyle(valueStyle);
        }
    }

    private static void createDefectSheet(XSSFWorkbook wb, List<TestListener.TestResult> failed, String timestamp) {
        XSSFSheet sheet = wb.createSheet("Defect Summary");
        sheet.setColumnWidth(0, 3500);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 12000);
        sheet.setColumnWidth(4, 4000);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Test ID", "Module", "Test Name", "Failure Reason", "Duration (ms)"};
        XSSFCellStyle hs = createHeaderStyle(wb);
        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i); c.setCellValue(headers[i]); c.setCellStyle(hs);
        }

        int rowNum = 1;
        XSSFCellStyle failStyle = createDataStyle(wb, COLOR_FAIL, false);
        for (TestListener.TestResult r : failed) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.testId);
            row.createCell(1).setCellValue(r.module);
            row.createCell(2).setCellValue(r.testName);
            row.createCell(3).setCellValue(r.failureReason != null ? r.failureReason : "Unknown");
            row.createCell(4).setCellValue(r.durationMs);
            for (int c = 0; c < 5; c++) row.getCell(c).setCellStyle(failStyle);
        }
    }

    private static void createPassRateSheet(XSSFWorkbook wb, List<TestListener.TestResult> all, String timestamp) {
        XSSFSheet sheet = wb.createSheet("Pass Rate by Module");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 4000);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Module", "Total", "Passed", "Failed", "Pass Rate"};
        XSSFCellStyle hs = createHeaderStyle(wb);
        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i); c.setCellValue(headers[i]); c.setCellStyle(hs);
        }

        // Group by module
        java.util.Map<String, List<TestListener.TestResult>> grouped =
            all.stream().collect(Collectors.groupingBy(r -> r.module));

        int rowNum = 1;
        for (java.util.Map.Entry<String, List<TestListener.TestResult>> e : grouped.entrySet()) {
            long total  = e.getValue().size();
            long pass   = e.getValue().stream().filter(r -> "PASS".equals(r.status)).count();
            long fail   = e.getValue().stream().filter(r -> "FAIL".equals(r.status)).count();
            double rate = total > 0 ? (pass * 100.0 / total) : 0;

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getKey());
            row.createCell(1).setCellValue(total);
            row.createCell(2).setCellValue(pass);
            row.createCell(3).setCellValue(fail);
            row.createCell(4).setCellValue(String.format("%.1f%%", rate));

            XSSFCellStyle style = createDataStyle(wb, rate >= 95 ? COLOR_PASS : COLOR_FAIL, false);
            for (int c = 0; c < 5; c++) row.getCell(c).setCellStyle(style);
        }
    }

    private static void writeFilteredReport(String dir, String fileName,
                                             List<TestListener.TestResult> results, String colorHex) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            createTestSheet(wb, "Test Cases", results, colorHex,
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            try (FileOutputStream fos = new FileOutputStream(dir + File.separator + fileName)) {
                wb.write(fos);
            }
        }
    }

    private static void writeExecutionSummary(String dir, String fileName,
        List<TestListener.TestResult> all, List<TestListener.TestResult> passed,
        List<TestListener.TestResult> failed, List<TestListener.TestResult> skipped,
        String timestamp) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            createMetricsSheet(wb, all, passed, failed, skipped, timestamp);
            try (FileOutputStream fos = new FileOutputStream(dir + File.separator + fileName)) {
                wb.write(fos);
            }
        }
    }

    // ── Style Helpers ─────────────────────────────────────────────────────────

    private static XSSFCellStyle createTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true); font.setFontHeightInPoints((short)14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(hexToXSSFColor(wb, COLOR_TITLE));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static XSSFCellStyle createHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true); font.setFontHeightInPoints((short)11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(hexToXSSFColor(wb, COLOR_HEADER));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private static XSSFCellStyle createDataStyle(XSSFWorkbook wb, String colorHex, boolean alternate) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont(); font.setFontHeightInPoints((short)10);
        style.setFont(font);
        if (alternate) {
            style.setFillForegroundColor(hexToXSSFColor(wb, COLOR_ALT));
        } else {
            style.setFillForegroundColor(hexToXSSFColor(wb, colorHex));
        }
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN); style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private static XSSFColor hexToXSSFColor(XSSFWorkbook wb, String hex) {
        byte[] rgb = new byte[3];
        rgb[0] = (byte) Integer.parseInt(hex.substring(0, 2), 16);
        rgb[1] = (byte) Integer.parseInt(hex.substring(2, 4), 16);
        rgb[2] = (byte) Integer.parseInt(hex.substring(4, 6), 16);
        return new XSSFColor(rgb, new DefaultIndexedColorMap());
    }

    private static String getStatusColor(String status) {
        if ("PASS".equals(status)) return COLOR_PASS;
        if ("FAIL".equals(status)) return COLOR_FAIL;
        return COLOR_SKIP;
    }
}
