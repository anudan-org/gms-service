package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "granter_active_grants_summary_disbursed")
public class GranterActiveGrantSummaryDisbursed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long granterId;
    @Column
    private String disbursementData;


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
}
