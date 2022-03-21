package org.codealpha.gmsservice.models;

public class AttributeToSaveVO {

    private GrantDTO grant;
    private SectionAttributesVO attr;

    public GrantDTO getGrant() {
        return grant;
    }

    public void setGrant(GrantDTO grant) {
        this.grant = grant;
    }

    public SectionAttributesVO getAttr() {
        return attr;
    }

    public void setAttr(SectionAttributesVO attr) {
        this.attr = attr;
    }
}
