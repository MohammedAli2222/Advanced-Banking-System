package com.bank.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * خدمة إدارة السجلات (Logs)
 * توفر الوصول للسجلات الكاملة، التحذيرات، الأخطاء، والسجلات الحديثة.
 */
public class LogService {

    // قائمة محاكاة للسجلات
    private final List<LogEntry> logs;

    public LogService() {
        this.logs = new ArrayList<>();
        // إضافة سجلات تجريبية
        logs.add(new LogEntry("INFO", "System started successfully"));
        logs.add(new LogEntry("WARN", "Memory usage high"));
        logs.add(new LogEntry("ERROR", "Database connection failed"));
        logs.add(new LogEntry("INFO", "User logged in"));
        logs.add(new LogEntry("WARN", "CPU usage high"));
    }

    // =========================================
    // الدوال المطلوبة
    // =========================================

    public List<String> getAllLogs() {
        return logs.stream().map(LogEntry::toString).collect(Collectors.toList());
    }

    public List<String> getWarningLogs() {
        return logs.stream()
                .filter(log -> log.getLevel().equals("WARN"))
                .map(LogEntry::toString)
                .collect(Collectors.toList());
    }

    public List<String> getErrorLogs() {
        return logs.stream()
                .filter(log -> log.getLevel().equals("ERROR"))
                .map(LogEntry::toString)
                .collect(Collectors.toList());
    }

    public List<String> getRecentLogs(int limit) {
        return logs.stream()
                .skip(Math.max(0, logs.size() - limit))
                .map(LogEntry::toString)
                .collect(Collectors.toList());
    }

    // دالة لإضافة سجل جديد
    public void recordLog(String level, String message) {
        logs.add(new LogEntry(level, message));
    }

    // =========================================
    // فئة مساعدة LogEntry
    // =========================================
    private static class LogEntry {
        private final String level;
        private final String message;

        public LogEntry(String level, String message) {
            this.level = level;
            this.message = message;
        }

        public String getLevel() {
            return level;
        }

        @Override
        public String toString() {
            return "[" + level + "] " + message;
        }
    }
}
