package org.codealpha.gmsservice.models;

public class ReportWithNote {
    private ReportDTO report;
    private String note;

    public ReportDTO getReport() {
        return report;
    }

    public void setReport(ReportDTO report) {
        this.report = report;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
