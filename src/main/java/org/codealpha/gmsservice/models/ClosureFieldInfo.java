package org.codealpha.gmsservice.models;

import java.util.Map;

public class ClosureFieldInfo {
    private Long attributeId;
    private Long stringAttributeId;
    private Map<String, Object> closure;

    public ClosureFieldInfo(Long id, Long stringAttrId, Map<String, Object> closure) {
        this.attributeId = id;
        this.stringAttributeId = stringAttrId;
        this.closure = closure;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Map<String, Object> getClosure() {
        return closure;
    }

    public void setClosure(Map<String, Object> closure) {
        this.closure = closure;
    }

    public Long getStringAttributeId() {
        return stringAttributeId;
    }

    public void setStringAttributeId(Long stringAttributeId) {
        this.stringAttributeId = stringAttributeId;
    }
}
