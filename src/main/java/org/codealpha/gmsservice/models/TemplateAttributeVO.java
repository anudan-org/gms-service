package org.codealpha.gmsservice.models;

import java.util.List;

public class TemplateAttributeVO {

    private String name;
    private int order;
    private String type;
    private List<TableData> tableValue;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TableData> getTableValue() {
        return tableValue;
    }

    public void setTableValue(List<TableData> tableValue) {
        this.tableValue = tableValue;
    }
}
