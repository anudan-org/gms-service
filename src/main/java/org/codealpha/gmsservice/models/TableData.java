package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TableData {

    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String header;
    private ColumnData[] columns;
    private boolean enteredByGrantee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnData[] getColumns() {
        return columns;
    }

    public void setColumns(ColumnData[] columns) {
        this.columns = columns;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isEnteredByGrantee() {
        return enteredByGrantee;
    }

    public void setEnteredByGrantee(boolean enteredByGrantee) {
        this.enteredByGrantee = enteredByGrantee;
    }
}
