package org.codealpha.gmsservice.models.dashboard;

public class DisbursementData {
    private String name;
    private String value;
    private Long count;

    public DisbursementData(String name, String value,Long count) {
        this.name = name;
        this.value = value;
        this.count = count;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
