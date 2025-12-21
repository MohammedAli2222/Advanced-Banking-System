package com.bank.reports;

public class SignatureDecorator extends ReportDecorator {

    public SignatureDecorator(ReportComponent decoratedReport) {
        super(decoratedReport);
    }

    @Override
    public String generate() {
        String report = super.generate();
        return report + "Signed by: Bank Manager [Digital Signature]\n";
    }
}