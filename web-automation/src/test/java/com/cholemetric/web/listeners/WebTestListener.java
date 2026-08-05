package com.cholemetric.web.listeners;
import org.testng.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebTestListener implements ITestListener {
    private static final AtomicInteger passed = new AtomicInteger(0);
    private static final AtomicInteger failed = new AtomicInteger(0);
    private static final AtomicInteger skipped = new AtomicInteger(0);

    @Override public void onTestSuccess(ITestResult result) { passed.incrementAndGet(); System.out.println("✅ PASSED: " + result.getName()); }
    @Override public void onTestFailure(ITestResult result) { failed.incrementAndGet(); System.out.println("❌ FAILED: " + result.getName() + " — " + result.getThrowable().getMessage()); }
    @Override public void onTestSkipped(ITestResult result) { skipped.incrementAndGet(); System.out.println("⏭️ SKIPPED: " + result.getName()); }
    @Override public void onFinish(ITestContext context) {
        System.out.println("\n=== FINAL RESULTS ===");
        System.out.println("Passed: " + passed.get());
        System.out.println("Failed: " + failed.get());
        System.out.println("Skipped: " + skipped.get());
    }
}
