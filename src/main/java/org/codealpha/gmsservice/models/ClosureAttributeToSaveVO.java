package org.codealpha.gmsservice.models;

public class ClosureAttributeToSaveVO {

    private GrantClosureDTO closure;
    private SectionAttributesVO attr;

    public GrantClosureDTO getClosure() {
        return closure;
    }

    public void setClosure(GrantClosureDTO closure) {
        this.closure = closure;
    }

    public SectionAttributesVO getAttr() {
        return attr;
    }

    public void setAttr(SectionAttributesVO attr) {
        this.attr = attr;
    }
}
