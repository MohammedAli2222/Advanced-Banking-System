package com.bank.reports;

public class ExportDecorator extends ReportDecorator {

    public ExportDecorator(ReportComponent decoratedReport) {
        super(decoratedReport);
    }

    @Override
    public String generate() {
        String report = super.generate();
        return "[EXPORTED TO PDF]\n" + report + "[END OF PDF EXPORT]\n";
    }
}