package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantStringAttributeAttachments;

import java.util.List;

public class PlainAttribute {
    private String name;
    private int order;
    private String type;
    private String value;
    private List<TableData> tableValue;
    private Long target;
    private String frequency;
    List<GrantStringAttributeAttachments> attachments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<TableData> getTableValue() {
        return tableValue;
    }

    public void setTableValue(List<TableData> tableValue) {
        this.tableValue = tableValue;
    }

    public List<GrantStringAttributeAttachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<GrantStringAttributeAttachments> attachments) {
        this.attachments = attachments;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
