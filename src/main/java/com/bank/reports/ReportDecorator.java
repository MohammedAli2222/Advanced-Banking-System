package com.bank.reports;

public abstract class ReportDecorator implements ReportComponent {
    protected ReportComponent decoratedReport;

    public ReportDecorator(ReportComponent decoratedReport) {
        this.decoratedReport = decoratedReport;
    }

    @Override
    public String generate() {
        return decoratedReport.generate();
    }

    @Override
    public void add(ReportComponent component) {
        decoratedReport.add(component);
    }

    @Override
    public void remove(ReportComponent component) {
        decoratedReport.remove(component);
    }
}