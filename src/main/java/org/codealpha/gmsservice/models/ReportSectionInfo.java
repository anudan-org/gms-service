package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

public class ReportSectionInfo {
    private Long sectionId;
    private String sectionName;
    private Report report;

    public ReportSectionInfo(Long id, String name, Report report) {
        this.sectionId = id;
        this.sectionName = name;
        this.report = report;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
