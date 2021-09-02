package org.codealpha.gmsservice.models;

import java.util.List;
import java.util.Map;

public class PlainGrant {
    private String name;
    private String startDate;
    private String endDate;
    private String implementingOrganizationName;
    private String implementingOrganizationRepresentative;
    private Double amount;
    private String referenceNo;
    private List<PlainSection> sections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImplementingOrganizationName() {
        return implementingOrganizationName;
    }

    public void setImplementingOrganizationName(String implementingOrganizationName) {
        this.implementingOrganizationName = implementingOrganizationName;
    }

    public String getImplementingOrganizationRepresentative() {
        return implementingOrganizationRepresentative;
    }

    public void setImplementingOrganizationRepresentative(String implementingOrganizationRepresentative) {
        this.implementingOrganizationRepresentative = implementingOrganizationRepresentative;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<PlainSection> getSections() {
        return sections;
    }

    public void setSections(List<PlainSection> sections) {
        this.sections = sections;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }
}
