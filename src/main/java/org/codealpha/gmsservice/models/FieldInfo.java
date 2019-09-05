package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class FieldInfo {
    private Long attributeId;
    private Grant grant;

    public FieldInfo(Long id, Grant grant) {
        this.attributeId = id;
        this.grant = grant;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }
}
