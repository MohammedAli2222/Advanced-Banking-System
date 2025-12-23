package com.bank.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * خدمة إدارة الجلسات للمستخدمين.
 * تحاكي دورة حياة الجلسات مع دعم الاستعلام عن الجلسات النشطة.
 */
public class SessionService {

    // محاكاة بيانات الجلسات: userId -> { token, startTimeMillis }
    private final Map<String, Long> activeSessions = new HashMap<>();

    // التحقق من الجلسة
    public boolean validateSession(String userId) {
        return activeSessions.containsKey(userId);
    }

    // إنشاء جلسة جديدة (توليد معرف افتراضي)
    public String createSession(String userId) {
        long now = System.currentTimeMillis();
        activeSessions.put(userId, now);
        return "Token_" + userId + "_" + now;
    }

    // إنهاء الجلسة
    public void terminateSession(String userId) {
        activeSessions.remove(userId);
    }

    // ===================== الدوال الجديدة =====================

    /**
     * عدد الجلسات النشطة
     */
    public int getActiveSessions() {
        return activeSessions.size();
    }

    /**
     * قائمة المستخدمين النشطين
     */
    public List<String> getActiveUsers() {
        return activeSessions.keySet().stream().collect(Collectors.toList());
    }

    /**
     * مدة الجلسة الحالية بالثواني
     */
    public double getSessionDuration(String userId) {
        if (!activeSessions.containsKey(userId)) return 0.0;
        long startTime = activeSessions.get(userId);
        long now = System.currentTimeMillis();
        return (now - startTime) / 1000.0; // بالثواني
    }
}
