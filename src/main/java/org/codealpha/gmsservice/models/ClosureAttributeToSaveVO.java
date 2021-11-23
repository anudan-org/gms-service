package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.codealpha.gmsservice.entities.Report;

public class ClosureAttributeToSaveVO {

    private GrantClosure closure;
    private SectionAttributesVO attr;

    public GrantClosure getClosure() {
        return closure;
    }

    public void setClosure(GrantClosure closure) {
        this.closure = closure;
    }

    public SectionAttributesVO getAttr() {
        return attr;
    }

    public void setAttr(SectionAttributesVO attr) {
        this.attr = attr;
    }
}
