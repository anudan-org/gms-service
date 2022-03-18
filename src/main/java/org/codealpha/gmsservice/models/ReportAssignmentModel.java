package org.codealpha.gmsservice.models;

public class ReportAssignmentModel {
    private ReportDTO report;
    private ReportAssignmentsVO[] assignments;

    public ReportDTO getReport() {
        return report;
    }

    public void setReport(ReportDTO report) {
        this.report = report;
    }

    public ReportAssignmentsVO[] getAssignments() {
        return assignments;
    }

    public void setAssignments(ReportAssignmentsVO[] grantAssignmentsVO) {
        this.assignments = grantAssignmentsVO;
    }
}
