package com.bank.dashboard;



import java.util.List;

/**
 * Facade موحد للوحة معلومات النظام.
 * يجمع Monitoring, Logging, Statistics, Sessions في واجهة واحدة.
 */
public class SystemDashboard {

    private final MonitoringService monitoringService;
    private final LogService logService;
    private final StatisticsService statisticsService;
    private final SessionService sessionService;

    public SystemDashboard() {
        this.monitoringService = new MonitoringService();
        this.logService = new LogService();
        this.statisticsService = new StatisticsService();
        this.sessionService = new SessionService();
    }

    // -------------------- System Overview --------------------
    public String getSystemOverview() {
        return String.format(
                "System Health: %s\nCPU Usage: %.2f%%\nMemory Usage: %.2f%%",
                monitoringService.getSystemHealth(),
                monitoringService.getCpuUsage(),
                monitoringService.getMemoryUsage()
        );
    }

    // -------------------- Logs --------------------
    public List<String> getRecentLogs(int limit) {
        return logService.getRecentLogs(limit);
    }

    public List<String> getWarningLogs() {
        return logService.getWarningLogs();
    }

    public List<String> getErrorLogs() {
        return logService.getErrorLogs();
    }

    public List<String> getAllLogs() {
        return logService.getAllLogs();
    }

    // -------------------- Statistics --------------------
    public String getStatisticsSummary() {
        return String.format(
                "Total Customers: %d\nTotal Accounts: %d\nTotal Transactions: %d\nDaily Transactions: %d\nMonthly Revenue: %.2f USD",
                statisticsService.getTotalCustomers(),
                statisticsService.getTotalAccounts(),
                statisticsService.getTotalTransactions(),
                statisticsService.getDailyTransactionCount(),
                statisticsService.getMonthlyRevenue()
        );
    }

    // -------------------- Sessions --------------------
    public List<String> getActiveUsers() {
        return sessionService.getActiveUsers();
    }

    public int getActiveSessions() {
        return sessionService.getActiveSessions();
    }

    public double getSessionDuration(String userId) {
        return sessionService.getSessionDuration(userId);
    }

    // -------------------- Access Services Directly --------------------
    public MonitoringService getMonitoringService() {
        return monitoringService;
    }

    public LogService getLogService() {
        return logService;
    }

    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }
}
