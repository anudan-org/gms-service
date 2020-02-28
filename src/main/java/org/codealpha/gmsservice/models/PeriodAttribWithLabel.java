package org.codealpha.gmsservice.models;

import java.util.List;

public class PeriodAttribWithLabel {

    private String periodLabel;
    private List<SectionAttributesVO> attributes;

    public PeriodAttribWithLabel(String periodLabel, List<SectionAttributesVO> attributes) {
        this.periodLabel = periodLabel;
        this.attributes = attributes;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public List<SectionAttributesVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SectionAttributesVO> attributes) {
        this.attributes = attributes;
    }
}
