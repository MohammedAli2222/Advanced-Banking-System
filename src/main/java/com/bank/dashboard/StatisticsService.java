package com.bank.dashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * خدمة الإحصائيات للوحة التحكم
 * توفر مؤشرات رقمية عن النظام مثل عدد المعاملات، الإيرادات، وعدد الحسابات والعملاء.
 */
public class StatisticsService {

    private int totalTransactions;
    private int dailyTransactionCount;
    private double monthlyRevenue;
    private int totalAccounts;
    private int totalCustomers;

    public StatisticsService() {
        // بيانات تجريبية
        this.totalTransactions = 1250;
        this.dailyTransactionCount = 45;
        this.monthlyRevenue = 10250.75;
        this.totalAccounts = 320;
        this.totalCustomers = 150;
    }

    // =========================================
    // الدوال المطلوبة
    // =========================================

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public int getDailyTransactionCount() {
        return dailyTransactionCount;
    }

    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    // دوال لتحديث البيانات (اختياري)
    public void addTransaction(double amount) {
        totalTransactions++;
        dailyTransactionCount++;
        monthlyRevenue += amount;
    }

    public void addCustomer() {
        totalCustomers++;
    }

    public void addAccount() {
        totalAccounts++;
    }
}
