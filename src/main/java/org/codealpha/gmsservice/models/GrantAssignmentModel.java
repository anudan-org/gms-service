package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class GrantAssignmentModel {
    private Grant grant;
    private GrantAssignmentsVO[] grantAssignmentsVO;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public GrantAssignmentsVO[] getGrantAssignmentsVO() {
        return grantAssignmentsVO;
    }

    public void setGrantAssignmentsVO(GrantAssignmentsVO[] grantAssignmentsVO) {
        this.grantAssignmentsVO = grantAssignmentsVO;
    }
}
