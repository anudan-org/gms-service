package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ColumnData {
    private String name;
    private String value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataType;

    public ColumnData() {
    }

    public ColumnData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ColumnData(String name, String value, String dataType) {
        this.name = name;
        this.value = value;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
