package org.codealpha.gmsservice.models;

public class ClosureAssignmentModel {
    private GrantClosureDTO closure;
    private ClosureAssignmentsVO[] assignments;

    public GrantClosureDTO getClosure() {
        return closure;
    }

    public void setClosure(GrantClosureDTO closure) {
        this.closure = closure;
    }

    public ClosureAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(ClosureAssignmentsVO[] assignments) {
        this.assignments = assignments;
    }
}
