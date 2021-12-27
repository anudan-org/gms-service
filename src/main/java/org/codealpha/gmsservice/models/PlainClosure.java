package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.ClosureReason;

import java.util.List;

public class PlainClosure {
    private ClosureReason reason;
    private String name;
    private String referenceNo;
    private String description;
    private List<PlainSection> sections;
    private String currentOwner;
    private String currentStatus;
    private String currentInternalStatus;
    private boolean external;

    public ClosureReason getReason() {
        return reason;
    }

    public void setReason(ClosureReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PlainSection> getSections() {
        return sections;
    }

    public void setSections(List<PlainSection> sections) {
        this.sections = sections;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }
}
