package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "granter_grants_summary_disbursed")
public class GranterGrantSummaryDisbursed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long grantId;
    @Column
    private Long granterId;
    @Column
    private String disbursementData;
    @Column
    private String internalStatus;
    @Column
    private Date startDate;
    @Column
    private Long grantAmount;


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

    public String getDisbursementData() {
        return disbursementData;
    }

    public void setDisbursementData(String disbursementData) {
        this.disbursementData = disbursementData;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Long grantAmount) {
        this.grantAmount = grantAmount;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }
}
