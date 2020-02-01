package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

public class ReportWithNote {
    private Report report;
    private String note;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
