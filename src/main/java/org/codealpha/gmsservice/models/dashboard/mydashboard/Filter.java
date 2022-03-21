
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "totalGrants",
    "granteeOrgs",
    "grantswithnoapprovedreports",
    "grantswithnokpis",
    "period",
    "disbursedAmount",
    "committedAmount",
    "details"
})
@Generated("jsonschema2pojo")
public class Filter {

    @JsonProperty("name")
    private String name;
    @JsonProperty("totalGrants")
    private Long totalGrants;
    @JsonProperty("granteeOrgs")
    private Long granteeOrgs;
    @JsonProperty("grantswithnoapprovedreports")
    private Long grantswithnoapprovedreports;
    @JsonProperty("grantswithnokpis")
    private Long grantswithnokpis;
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

    @JsonProperty("granteeOrgs")
    public Long getGranteeOrgs() {
        return granteeOrgs;
    }

    @JsonProperty("granteeOrgs")
    public void setGranteeOrgs(Long granteeOrgs) {
        this.granteeOrgs = granteeOrgs;
    }

    @JsonProperty("grantswithnoapprovedreports")
    public Long getGrantswithnoapprovedreports() {
        return grantswithnoapprovedreports;
    }

    @JsonProperty("grantswithnoapprovedreports")
    public void setGrantswithnoapprovedreports(Long grantswithnoapprovedreports) {
        this.grantswithnoapprovedreports = grantswithnoapprovedreports;
    }

    @JsonProperty("grantswithnokpis")
    public Long getGrantswithnokpis() {
        return grantswithnokpis;
    }

    @JsonProperty("grantswithnokpis")
    public void setGrantswithnokpis(Long grantswithnokpis) {
        this.grantswithnokpis = grantswithnokpis;
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
