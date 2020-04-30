package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;

@Entity(name = "granter_grantees")
public class GranterGrantee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long granterId;
    @Column
    private Long granteeTotals;

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

    public Long getGranteeTotals() {
        return granteeTotals;
    }

    public void setGranteeTotals(Long granteeTotals) {
        this.granteeTotals = granteeTotals;
    }
}
