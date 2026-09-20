package org.codealpha.gmsservice.models;

import java.util.Map;

public class ReportSectionInfo {
    private Long sectionId;
    private String sectionName;
    private Map<String, Object> report;

    public ReportSectionInfo(Long id, String name, Map<String, Object> report) {
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

    public Map<String, Object> getReport() {
        return report;
    }

    public void setReport(Map<String, Object> report) {
        this.report = report;
    }
}
