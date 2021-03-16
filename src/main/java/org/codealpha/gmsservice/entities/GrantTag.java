package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity(name = "grant_tags")
public class GrantTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long grantId;
    @Column
    private Long orgTagId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }

    public Long getOrgTagId() {
        return orgTagId;
    }

    public void setOrgTagId(Long orgTagId) {
        this.orgTagId = orgTagId;
    }
}
