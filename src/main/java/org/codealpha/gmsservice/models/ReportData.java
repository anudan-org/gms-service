package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

import java.util.List;

public class ReportData {

    private Long count;
    private List<Report> reports;

    public ReportData() {
    }

    public ReportData(Long count, List<Report> reports) {
        this.count = count;
        this.reports = reports;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
