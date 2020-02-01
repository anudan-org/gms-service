package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

public class ReportAttributeToSaveVO {

    private Report report;
    private SectionAttributesVO attr;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public SectionAttributesVO getAttr() {
        return attr;
    }

    public void setAttr(SectionAttributesVO attr) {
        this.attr = attr;
    }
}
