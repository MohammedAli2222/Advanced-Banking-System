package com.bank.reports;

public class WatermarkDecorator extends ReportDecorator {

    public WatermarkDecorator(ReportComponent decoratedReport) {
        super(decoratedReport);
    }

    @Override
    public String generate() {
        String report = super.generate();
        return "[WATERMARK: CONFIDENTIAL]\n" + report + "[END WATERMARK]\n";
    }
}