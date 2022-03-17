package org.codealpha.gmsservice.models;

import java.util.List;

public class PlainGrant {
    private String name;
    private String startDate;
    private String endDate;
    private String implementingOrganizationName;
    private String implementingOrganizationRepresentative;
    private Double amount;
    private String referenceNo;
    private List<PlainSection> sections;
    private String currentOwner;
    private String currentStatus;
    private String currentInternalStatus;
    private boolean external;

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

    public String getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(String currentOwner) {
        this.currentOwner = currentOwner;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentInternalStatus() {
        return currentInternalStatus;
    }

    public void setCurrentInternalStatus(String currentInternalStatus) {
        this.currentInternalStatus = currentInternalStatus;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }
}
