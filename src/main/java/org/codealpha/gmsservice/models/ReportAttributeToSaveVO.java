package org.codealpha.gmsservice.models;

public class ReportAttributeToSaveVO {

    private ReportDTO report;
    private SectionAttributesVO attr;

    public ReportDTO getReport() {
        return report;
    }

    public void setReport(ReportDTO report) {
        this.report = report;
    }

    public SectionAttributesVO getAttr() {
        return attr;
    }

    public void setAttr(SectionAttributesVO attr) {
        this.attr = attr;
    }
}
