
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "values"
})
@Generated("jsonschema2pojo")
public class Disbursement {

    @JsonProperty("name")
    private String name;
    @JsonProperty("values")
    private Value[] values = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Disbursement() {
    }

    public Disbursement(String name, Value[] values) {
        this.name = name;
        this.values = values;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("values")
    public Value[] getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(Value[] values) {
        this.values = values;
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
