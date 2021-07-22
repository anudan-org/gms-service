
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
    "ActionsPending",
    "UpcomingGrants"
})
@Generated("jsonschema2pojo")
public class Summary {

    @JsonProperty("ActionsPending")
    private ActionsPending actionsPending;
    @JsonProperty("UpcomingGrants")
    private UpcomingGrants upcomingGrants;
    @JsonProperty("UpcomingReports")
    private UpcomingReports upcomingReports;
    @JsonProperty("upcomingDisbursements")
    private UpcomingDisbursements upcomingDisbursements;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("ActionsPending")
    public ActionsPending getActionsPending() {
        return actionsPending;
    }

    @JsonProperty("ActionsPending")
    public void setActionsPending(ActionsPending actionsPending) {
        this.actionsPending = actionsPending;
    }

    @JsonProperty("UpcomingGrants")
    public UpcomingGrants getUpcomingGrants() {
        return upcomingGrants;
    }

    @JsonProperty("UpcomingGrants")
    public void setUpcomingGrants(UpcomingGrants upcomingGrants) {
        this.upcomingGrants = upcomingGrants;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public UpcomingReports getUpcomingReports() {
        return upcomingReports;
    }

    public void setUpcomingReports(UpcomingReports upcomingReports) {
        this.upcomingReports = upcomingReports;
    }

    public UpcomingDisbursements getUpcomingDisbursements() {
        return upcomingDisbursements;
    }

    public void setUpcomingDisbursements(UpcomingDisbursements upcomingDisbursements) {
        this.upcomingDisbursements = upcomingDisbursements;
    }
}
