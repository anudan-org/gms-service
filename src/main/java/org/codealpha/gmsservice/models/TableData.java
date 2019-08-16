package org.codealpha.gmsservice.models;

public class TableData {

    private String name;
    private ColumnData[] columns;

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
}
