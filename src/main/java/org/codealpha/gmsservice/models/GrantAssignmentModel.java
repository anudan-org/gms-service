package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class GrantAssignmentModel {
    private Grant grant;
    private GrantAssignmentsVO[] assignments;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public GrantAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(GrantAssignmentsVO[] grantAssignmentsVO) {
        this.assignments = grantAssignmentsVO;
    }
}
