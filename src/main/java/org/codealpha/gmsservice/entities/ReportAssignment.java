package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity(name = "report_assignments")
public class ReportAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long reportId;

    @Column
    private Long stateId;

    @Column
    private Long assignment;

    @Column
    private boolean anchor = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long grantId) {
        this.reportId = grantId;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getAssignment() {
        return assignment;
    }

    public void setAssignment(Long assignment) {
        this.assignment = assignment;
    }

    public boolean isAnchor() {
        return anchor;
    }

    public void setAnchor(boolean anchor) {
        this.anchor = anchor;
    }
}
