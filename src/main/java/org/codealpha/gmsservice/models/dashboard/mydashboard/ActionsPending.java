
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import java.util.HashMap;
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
    "Grants",
    "Reports",
    "DisbursementApprovals"
})
@Generated("jsonschema2pojo")
public class ActionsPending {

    @JsonProperty("Grants")
    private Long grants;
    @JsonProperty("Reports")
    private Long reports;
    @JsonProperty("DisbursementApprovals")
    private Long disbursementApprovals;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ActionsPending() {
    }

    public ActionsPending(Long grants, Long reports, Long disbursementApprovals) {
        this.grants = grants;
        this.reports = reports;
        this.disbursementApprovals = disbursementApprovals;
    }

    @JsonProperty("Grants")
    public Long getGrants() {
        return grants;
    }

    @JsonProperty("Grants")
    public void setGrants(Long grants) {
        this.grants = grants;
    }

    @JsonProperty("Reports")
    public Long getReports() {
        return reports;
    }

    @JsonProperty("Reports")
    public void setReports(Long reports) {
        this.reports = reports;
    }

    @JsonProperty("DisbursementApprovals")
    public Long getDisbursementApprovals() {
        return disbursementApprovals;
    }

    @JsonProperty("DisbursementApprovals")
    public void setDisbursementApprovals(Long disbursementApprovals) {
        this.disbursementApprovals = disbursementApprovals;
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
