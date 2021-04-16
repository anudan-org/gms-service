package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "grant_tags")
public class GrantTagCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @JsonIgnore
    private GrantCard grant;
    @Column
    private Long orgTagId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrantCard getGrant() {
        return grant;
    }

    public void setGrant(GrantCard grant) {
        this.grant = grant;
    }

    public Long getOrgTagId() {
        return orgTagId;
    }

    public void setOrgTagId(Long orgTagId) {
        this.orgTagId = orgTagId;
    }
}
