
package org.codealpha.gmsservice.models.dashboard;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "value","internalStatus"
})
public class ReportStatusSummary extends DetailedSummary {

    @JsonProperty("value")
    private Long value;
    @JsonProperty("internalStatus")
    private String internalStatus;
    @JsonProperty("grantType")
    private String grantType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ReportStatusSummary(String name, String internalStatus, Long value,String grantType) {
        super(name);
        this.internalStatus = internalStatus;
        this.value = value;
        this.grantType = grantType;
    }

    @JsonProperty("value")
    public Long getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Long value) {
        this.value = value;
    }

    @JsonProperty("internalStatus")
    public String getInternalStatus() {
        return internalStatus;
    }

    @JsonProperty("internalStatus")
    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
