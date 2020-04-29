
package org.codealpha.gmsservice.models.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "totalGrants",
    "period",
    "disbursedAmount",
    "committedAmount",
    "details"
})
public class Filter {

    @JsonProperty("name")
    private String name;
    @JsonProperty("totalGrants")
    private Long totalGrants;
    @JsonProperty("period")
    private String period;
    @JsonProperty("disbursedAmount")
    private Long disbursedAmount;
    @JsonProperty("committedAmount")
    private Long committedAmount;
    @JsonProperty("details")
    private List<Detail> details = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("totalGrants")
    public Long getTotalGrants() {
        return totalGrants;
    }

    @JsonProperty("totalGrants")
    public void setTotalGrants(Long totalGrants) {
        this.totalGrants = totalGrants;
    }

    @JsonProperty("period")
    public String getPeriod() {
        return period;
    }

    @JsonProperty("period")
    public void setPeriod(String period) {
        this.period = period;
    }

    @JsonProperty("disbursedAmount")
    public Long getDisbursedAmount() {
        return disbursedAmount;
    }

    @JsonProperty("disbursedAmount")
    public void setDisbursedAmount(Long disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }

    @JsonProperty("committedAmount")
    public Long getCommittedAmount() {
        return committedAmount;
    }

    @JsonProperty("committedAmount")
    public void setCommittedAmount(Long committedAmount) {
        this.committedAmount = committedAmount;
    }

    @JsonProperty("details")
    public List<Detail> getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(List<Detail> details) {
        this.details = details;
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
