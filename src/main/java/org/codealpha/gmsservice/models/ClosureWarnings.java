package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Disbursement;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Report;

import java.util.List;

public class ClosureWarnings {
    private List<Disbursement> disbursementsInProgress;
    private List<Report> reportsInProgress;
    private Grant grantInAmendment;

    public ClosureWarnings(List<Disbursement> disbursementsInProgress, List<Report> reportsInProgress, Grant grantInAmendment) {
        this.disbursementsInProgress = disbursementsInProgress;
        this.reportsInProgress = reportsInProgress;
        this.grantInAmendment = grantInAmendment;
    }

    public List<Disbursement> getDisbursementsInProgress() {
        return disbursementsInProgress;
    }

    public void setDisbursementsInProgress(List<Disbursement> disbursementsInProgress) {
        this.disbursementsInProgress = disbursementsInProgress;
    }

    public List<Report> getReportsInProgress() {
        return reportsInProgress;
    }

    public void setReportsInProgress(List<Report> reportsInProgress) {
        this.reportsInProgress = reportsInProgress;
    }

    public Grant getGrantInAmendment() {
        return grantInAmendment;
    }

    public void setGrantInAmendment(Grant grantInAmendment) {
        this.grantInAmendment = grantInAmendment;
    }
}
