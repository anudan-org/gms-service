
package org.codealpha.gmsservice.models.dashboard;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "values"
})
public class DisbursalSummary extends DetailedSummary {

    @JsonProperty("values")
    private DisbursementData[] values;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public DisbursalSummary(String name, DisbursementData[] values) {
        super(name);
        this.values = values;
    }

    @JsonProperty("values")
    public DisbursementData[] getValue() {
        return values;
    }

    @JsonProperty("values")
    public void setValue(DisbursementData[] value) {
        this.values = value;
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
