package org.codealpha.gmsservice.models;

import java.util.List;

public class PlainSection {

    private String name;
    private int order;
    private List<PlainAttribute> attributes;

    public PlainSection(String name, int order, List<PlainAttribute> attributes) {
        this.name = name;
        this.attributes = attributes;
        this.order=order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlainAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<PlainAttribute> attributes) {
        this.attributes = attributes;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
