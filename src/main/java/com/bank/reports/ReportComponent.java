package com.bank.reports;

public interface ReportComponent {
    String generate();
    void add(ReportComponent component);  // للـ composite فقط
    void remove(ReportComponent component);  // للـ composite فقط
}