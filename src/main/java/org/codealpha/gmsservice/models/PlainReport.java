package org.codealpha.gmsservice.models;

import java.util.List;

public class PlainReport {
    private String name;
    private String startDate;
    private String endDate;
    private String dueDate;
    private List<PlainSection> sections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public List<PlainSection> getSections() {
        return sections;
    }

    public void setSections(List<PlainSection> sections) {
        this.sections = sections;
    }
}
