package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.ActualDisbursement;

import java.util.List;

public class PlainDisbursement {
    private Double requestedAmount;
    private String commentary;
    private String grantName;
    private List<ActualDisbursement> actualDisbursement;
    private String currentOwner;
    private String currentStatus;
    private String currentInternalStatus;
    private boolean external;

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getGrantName() {
        return grantName;
    }

    public void setGrantName(String grantName) {
        this.grantName = grantName;
    }

    public List<ActualDisbursement> getActualDisbursement() {
        return actualDisbursement;
    }

    public void setActualDisbursement(List<ActualDisbursement> actualDisbursement) {
        this.actualDisbursement = actualDisbursement;
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
