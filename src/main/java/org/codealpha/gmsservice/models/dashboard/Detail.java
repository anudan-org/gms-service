
package org.codealpha.gmsservice.models.dashboard;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "summary"
})
public class Detail {

    @JsonProperty("name")
    private String name;
    @JsonProperty("summary")
    private Map<String,List<DetailedSummary>> summary = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Detail(String name, Map<String,List<DetailedSummary>> summary) {
        this.name = name;
        this.summary = summary;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("summary")
    public Map<String,List<DetailedSummary>> getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(Map<String,List<DetailedSummary>> summary) {
        this.summary = summary;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
