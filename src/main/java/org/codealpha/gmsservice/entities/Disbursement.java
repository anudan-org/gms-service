package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "disbursements")
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Double requestedAmount;
    @Column(columnDefinition = "text")
    private String reason;
    @Column
    private Date requestedOn;
    @Column
    private Long requestedBy;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private WorkflowStatus status;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Grant grant;
    @Transient
    private List<DisbursementAssignment> assignments;
    @Transient
    private List<WorkFlowPermission> flowPermissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Date requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Long getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Long requestedBy) {
        this.requestedBy = requestedBy;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public void setAssignments(List<DisbursementAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<DisbursementAssignment> getAssignments() {
        return assignments;
    }

    public void setFlowPermissions(List<WorkFlowPermission> flowPermissions) {
        this.flowPermissions = flowPermissions;
    }

    public List<WorkFlowPermission> getFlowPermissions() {
        return flowPermissions;
    }
}
