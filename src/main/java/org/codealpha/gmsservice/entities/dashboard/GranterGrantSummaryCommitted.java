package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "granter_grants_summary_committed")
public class GranterGrantSummaryCommitted {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long granterId;
    @Column
    private Long grantCount;
    @Column
    private Date periodStart;
    @Column
    private Date periodEnd;
    @Column
    private Long committedAmount;
    @Column
    private String internalStatus;

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

    public Long getGrantCount() {
        return grantCount;
    }

    public void setGrantCount(Long grantCount) {
        this.grantCount = grantCount;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Long getCommittedAmount() {
        return committedAmount;
    }

    public void setCommittedAmount(Long committedAmount) {
        this.committedAmount = committedAmount;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }
}
