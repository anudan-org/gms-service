
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "disbursement",
    "summary",
    "statusSummary"
})
@Generated("jsonschema2pojo")
public class Summary__1 {

    @JsonProperty("disbursement")
    private List<Disbursement> disbursement = null;
    @JsonProperty("summary")
    private List<Summary__2> summary = null;
    @JsonProperty("statusSummary")
    private List<StatusSummary> statusSummary = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("disbursement")
    public List<Disbursement> getDisbursement() {
        return disbursement;
    }

    @JsonProperty("disbursement")
    public void setDisbursement(List<Disbursement> disbursement) {
        this.disbursement = disbursement;
    }

    @JsonProperty("summary")
    public List<Summary__2> getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(List<Summary__2> summary) {
        this.summary = summary;
    }

    @JsonProperty("statusSummary")
    public List<StatusSummary> getStatusSummary() {
        return statusSummary;
    }

    @JsonProperty("statusSummary")
    public void setStatusSummary(List<StatusSummary> statusSummary) {
        this.statusSummary = statusSummary;
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
