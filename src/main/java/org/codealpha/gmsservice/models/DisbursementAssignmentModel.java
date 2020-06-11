
package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Disbursement;

public class DisbursementAssignmentModel {
    private Disbursement disbursement;
    private DisbursementAssignmentsVO[] assignments;

    public Disbursement getDisbursement() {
        return disbursement;
    }

    public void setDisbursement(Disbursement disbursement) {
        this.disbursement = disbursement;
    }

    public DisbursementAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(DisbursementAssignmentsVO[] disbursementAssignmentsVO) {
        this.assignments = disbursementAssignmentsVO;
    }
}
