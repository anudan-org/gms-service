package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class GrantTagsDTO {
    private Long id;
    private Grant grant;
    private Long orgTagId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public Long getOrgTagId() {
        return orgTagId;
    }

    public void setOrgTagId(Long orgTagId) {
        this.orgTagId = orgTagId;
    }
}
