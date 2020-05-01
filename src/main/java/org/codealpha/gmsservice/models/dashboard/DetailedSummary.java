package org.codealpha.gmsservice.models.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DetailedSummary {
    @JsonProperty("name")
    protected String name;

    public DetailedSummary(String name) {
        this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
}
