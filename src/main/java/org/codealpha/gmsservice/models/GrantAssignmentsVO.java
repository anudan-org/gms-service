package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantAssignmentHistory;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkflowStatus;

import java.util.List;

public class GrantAssignmentsVO {

    private Long id;
    private Long grantId;
    private Long stateId;
    private WorkflowStatus stateName;
    private Long assignments;
    private User assignmentUser;
    private boolean anchor = false;
    private List<GrantAssignmentHistory> history;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public WorkflowStatus getStateName() {
        return stateName;
    }

    public void setStateName(WorkflowStatus stateName) {
        this.stateName = stateName;
    }

    public Long getAssignments() {
        return assignments;
    }

    public void setAssignments(Long assignments) {
        this.assignments = assignments;
    }

    public User getAssignmentUser() {
        return assignmentUser;
    }

    public void setAssignmentUser(User assignmentUser) {
        this.assignmentUser = assignmentUser;
    }

    public boolean isAnchor() {
        return anchor;
    }

    public void setAnchor(boolean anchor) {
        this.anchor = anchor;
    }

    public List<GrantAssignmentHistory> getHistory() {
        return history;
    }

    public void setHistory(List<GrantAssignmentHistory> history) {
        this.history = history;
    }

}
