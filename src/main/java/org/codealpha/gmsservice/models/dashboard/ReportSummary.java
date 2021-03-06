
package org.codealpha.gmsservice.models.dashboard;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "value"
})
public class ReportSummary extends DetailedSummary {

    @JsonProperty("value")
    private Long value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public ReportSummary(String name, Long value) {
        super(name);
        this.value = value;
    }

    @JsonProperty("value")
    public Long getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Long value) {
        this.value = value;
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
