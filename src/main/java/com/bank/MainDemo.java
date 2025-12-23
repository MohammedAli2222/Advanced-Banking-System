package com.bank;


import com.bank.dashboard.SystemDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainDemo {

    private static final Logger logger = LoggerFactory.getLogger(MainDemo.class);

    public static void main(String[] args) {

        SystemDashboard sysDash = new SystemDashboard();

        System.out.println(sysDash.getSystemOverview());
        System.out.println(sysDash.getStatisticsSummary());
        System.out.println("Active users: " + sysDash.getActiveUsers());
        System.out.println("Recent logs: " + sysDash.getRecentLogs(5));

    }
}
