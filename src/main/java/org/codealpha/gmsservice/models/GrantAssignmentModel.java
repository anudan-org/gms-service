package org.codealpha.gmsservice.models;

public class GrantAssignmentModel {
    private GrantDTO grant;
    private GrantAssignmentsVO[] assignments;

    public GrantDTO getGrant() {
        return grant;
    }

    public void setGrant(GrantDTO grant) {
        this.grant = grant;
    }

    public GrantAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(GrantAssignmentsVO[] grantAssignmentsVO) {
        this.assignments = grantAssignmentsVO;
    }
}
