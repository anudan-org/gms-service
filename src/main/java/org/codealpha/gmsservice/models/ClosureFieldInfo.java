package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.codealpha.gmsservice.entities.Report;

public class ClosureFieldInfo {
    private Long attributeId;
    private Long stringAttributeId;
    private GrantClosure closure;

    public ClosureFieldInfo(Long id, Long stringAttrId, GrantClosure closure) {
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

    public GrantClosure getClosure() {
        return closure;
    }

    public void setClosure(GrantClosure closure) {
        this.closure = closure;
    }

    public Long getStringAttributeId() {
        return stringAttributeId;
    }

    public void setStringAttributeId(Long stringAttributeId) {
        this.stringAttributeId = stringAttributeId;
    }
}
