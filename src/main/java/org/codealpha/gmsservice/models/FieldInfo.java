package org.codealpha.gmsservice.models;

import java.util.Map;

public class FieldInfo {
    private Long attributeId;
    private Long stringAttributeId;
    private Map<String, Object> grant;

    public FieldInfo(Long id, Long stringAttrId, Map<String, Object> grant) {
        this.attributeId = id;
        this.stringAttributeId = stringAttrId;
        this.grant = grant;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Map<String, Object> getGrant() {
        return grant;
    }

    public void setGrant(Map<String, Object> grant) {
        this.grant = grant;
    }

    public Long getStringAttributeId() {
        return stringAttributeId;
    }

    public void setStringAttributeId(Long stringAttributeId) {
        this.stringAttributeId = stringAttributeId;
    }
}
