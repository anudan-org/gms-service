package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

public class ReportAssignmentModel {
    private Report report;
    private ReportAssignmentsVO[] assignments;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public ReportAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(ReportAssignmentsVO[] grantAssignmentsVO) {
        this.assignments = grantAssignmentsVO;
    }
}
