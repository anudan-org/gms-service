
package org.codealpha.gmsservice.models.dashboard;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Object> additionalProperties = new HashMap<>();
    @JsonProperty("donors")
    private Long donors;
    @JsonProperty("plannedFundOthers")
    private Long plannedFundOthers;
    @JsonProperty("actualFundOthers")
    private Long actualFundOthers;

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

    @JsonProperty("plannedFundOthers")
    public Long getPlannedFundOthers() {
        return plannedFundOthers;
    }

    @JsonProperty("plannedFundOthers")
    public void setPlannedFundOthers(Long plannedFundOthers) {
        this.plannedFundOthers = plannedFundOthers;
    }

    @JsonProperty("actualFundOthers")
    public Long getActualFundOthers() {
        return actualFundOthers;
    }

    @JsonProperty("actualFundOthers")
    public void setActualFundOthers(Long actualFundOthers) {
        this.actualFundOthers = actualFundOthers;
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

    public Long getDonors() {
        return donors;
    }

    public void setDonors(Long donors) {
        this.donors = donors;
    }
}
