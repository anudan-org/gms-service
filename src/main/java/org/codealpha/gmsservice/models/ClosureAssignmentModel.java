package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.codealpha.gmsservice.entities.Report;

public class ClosureAssignmentModel {
    private GrantClosure closure;
    private ClosureAssignmentsVO[] assignments;

    public GrantClosure getClosure() {
        return closure;
    }

    public void setClosure(GrantClosure closure) {
        this.closure = closure;
    }

    public ClosureAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(ClosureAssignmentsVO[] assignments) {
        this.assignments = assignments;
    }
}
