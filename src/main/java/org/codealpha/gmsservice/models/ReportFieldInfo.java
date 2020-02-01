package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

public class ReportFieldInfo {
    private Long attributeId;
    private Long stringAttributeId;
    private Report report;

    public ReportFieldInfo(Long id, Long stringAttrId, Report report) {
        this.attributeId = id;
        this.stringAttributeId = stringAttrId;
        this.report = report;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Long getStringAttributeId() {
        return stringAttributeId;
    }

    public void setStringAttributeId(Long stringAttributeId) {
        this.stringAttributeId = stringAttributeId;
    }
}
