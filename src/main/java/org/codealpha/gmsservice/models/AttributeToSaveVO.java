package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class AttributeToSaveVO {

    private Grant grant;
    private SectionAttributesVO attr;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public SectionAttributesVO getAttr() {
        return attr;
    }

    public void setAttr(SectionAttributesVO attr) {
        this.attr = attr;
    }
}
