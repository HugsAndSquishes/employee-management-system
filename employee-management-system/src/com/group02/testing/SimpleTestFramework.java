package com.group02.testing;

public class SimpleTestFramework {
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void assertEquals(Object expected, Object actual, String message) {
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            System.out.println("✓ PASS: " + message);
            passedTests++;
        } else {
            System.out.println("✗ FAIL: " + message);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual: " + actual);
            failedTests++;
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (condition) {
            System.out.println("✓ PASS: " + message);
            passedTests++;
        } else {
            System.out.println("✗ FAIL: " + message);
            failedTests++;
        }
    }

    public static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    public static void assertNotNull(Object obj, String message) {
        assertTrue(obj != null, message);
    }

    public static void printTestSummary() {
        System.out.println("\n====== Test Summary ======");
        System.out.println("Tests passed: " + passedTests);
        System.out.println("Tests failed: " + failedTests);
        System.out.println("Total tests: " + (passedTests + failedTests));
        System.out.println("==========================");
    }

    public static void resetCounters() {
        passedTests = 0;
        failedTests = 0;
    }
}