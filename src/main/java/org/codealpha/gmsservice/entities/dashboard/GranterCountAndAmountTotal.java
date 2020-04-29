package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;

@Entity(name = "granter_count_and_amount_totals")
public class GranterCountAndAmountTotal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long granterId;
    @Column
    private Long totalGrantAmount;
    @Column
    private Long totalGrants;

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

    public Long getTotalGrantAmount() {
        return totalGrantAmount;
    }

    public void setTotalGrantAmount(Long totalGrantAmount) {
        this.totalGrantAmount = totalGrantAmount;
    }

    public Long getTotalGrants() {
        return totalGrants;
    }

    public void setTotalGrants(Long totalGrants) {
        this.totalGrants = totalGrants;
    }
}
