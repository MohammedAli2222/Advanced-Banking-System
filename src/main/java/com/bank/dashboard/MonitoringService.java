package com.bank.dashboard;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * خدمة مراقبة النظام
 * توفر إحصائيات مثل استهلاك الذاكرة، المعالج، وحالة النظام العامة.
 */
public class MonitoringService {

    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    public MonitoringService() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
    }

    /**
     * النسبة المئوية لاستهلاك الذاكرة heap
     */
    public double getMemoryUsage() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long used = heapUsage.getUsed();
        long max = heapUsage.getMax();
        return ((double) used / max) * 100.0;
    }

    /**
     * النسبة المئوية لاستخدام المعالج
     * ملاحظة: OperatingSystemMXBean الأصلي لا يعطي دقة عالية، يمكن استخدام ThreadMXBean أو أدوات خارجية
     */
    public double getCpuUsage() {
        // افتراضي محاكاة
        return Math.random() * 100; // محاكاة رقم بين 0-100%
    }

    /**
     * حالة النظام العامة
     */
    public String getSystemHealth() {
        double memUsage = getMemoryUsage();
        double cpuUsage = getCpuUsage();

        if (memUsage < 70 && cpuUsage < 75) {
            return "Healthy";
        } else if (memUsage < 90 && cpuUsage < 90) {
            return "Warning";
        } else {
            return "Critical";
        }
    }
}
