package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;

@Entity(name = "granter_report_statuses")
@Cacheable(value = false)
public class GranterReportSummaryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long granterId;
    @Column
    private String internalStatus;
    @Column
    private String status;
    @Column
    private int count;
    @Column
    private String grantType;
    @Column
    private Long statusId;
    @Column
    private Long workflowId;
    @Column
    private Long grantTypeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGranterId() {
        return granterId;
    }

    public void setGranterId(Long granterId) {
        this.granterId = granterId;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Long getGrantTypeId() {
        return grantTypeId;
    }

    public void setGrantTypeId(Long grantTypeId) {
        this.grantTypeId = grantTypeId;
    }
}
