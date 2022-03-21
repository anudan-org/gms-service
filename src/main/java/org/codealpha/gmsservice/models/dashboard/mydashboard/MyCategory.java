
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "summary",
    "filters"
})
@Generated("jsonschema2pojo")
public class MyCategory {

    @JsonProperty("name")
    private String name;
    @JsonProperty("summary")
    private Summary summary;
    @JsonProperty("filters")
    private List<Filter> filters = null;
    @JsonProperty("canshowdashboard")
    private boolean canShowDashboard = true;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("summary")
    public Summary getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    @JsonProperty("filters")
    public List<Filter> getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isCanShowDashboard() {
        return canShowDashboard;
    }

    public void setCanShowDashboard(boolean canShowDashboard) {
        this.canShowDashboard = canShowDashboard;
    }
}
