package com.bank.utils;

/**
 * Utility for synchronized printing to stdout/stderr to avoid interleaved lines
 * when many threads print concurrently.
 */
public final class PrintUtil {
    private static final Object PRINT_LOCK = new Object();

    private PrintUtil() {}

    public static void println(String msg) {
        synchronized (PRINT_LOCK) {
            System.out.println(msg);
        }
    }

    public static void printf(String format, Object... args) {
        synchronized (PRINT_LOCK) {
            System.out.printf(format, args);
            System.out.println();
        }
    }

    public static void errln(String msg) {
        synchronized (PRINT_LOCK) {
            System.err.println(msg);
        }
    }
}