package com.bank.reports;

import java.util.ArrayList;
import java.util.List;

public class ReportComposite implements ReportComponent {
    private List<ReportComponent> components = new ArrayList<>();
    private String title;

    public ReportComposite(String title) {
        this.title = title;
    }

    @Override
    public String generate() {
        StringBuilder report = new StringBuilder();
        report.append("=== " + title + " ===\n");
        for (ReportComponent component : components) {
            report.append(component.generate()).append("\n");
        }
        report.append("=== End of " + title + " ===\n");
        return report.toString();
    }

    @Override
    public void add(ReportComponent component) {
        components.add(component);
    }

    @Override
    public void remove(ReportComponent component) {
        components.remove(component);
    }
}