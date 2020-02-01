package org.codealpha.gmsservice.models;

import java.util.List;

public class TemplateSectionVO {
    private String name;
    private int order;
    private List<TemplateAttributeVO> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<TemplateAttributeVO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<TemplateAttributeVO> attributes) {
        this.attributes = attributes;
    }
}
