package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.ClosureAssignmentHistory;
import org.codealpha.gmsservice.entities.ReportAssignmentHistory;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkflowStatus;

import java.util.List;

public class ClosureAssignmentsVO {

    private Long id;
    private Long grantId;
    private Long stateId;
    private WorkflowStatus stateName;
    private Long assignmentId;
    private User assignmentUser;
    private boolean anchor = false;
    private String customAssignments;
    private List<ClosureAssignmentHistory> history;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
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

    public String getCustomAssignments() {
        return customAssignments;
    }

    public void setCustomAssignments(String customAssignments) {
        this.customAssignments = customAssignments;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }

    public List<ClosureAssignmentHistory> getHistory() {
        return history;
    }

    public void setHistory(List<ClosureAssignmentHistory> history) {
        this.history = history;
    }
}
