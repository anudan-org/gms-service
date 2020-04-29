
package org.codealpha.gmsservice.models.dashboard;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "totalGrants",
    "grantees",
    "totalGrantAmount",
    "activeUsers"
})
public class Summary {

    @JsonProperty("totalGrants")
    private Long totalGrants;
    @JsonProperty("grantees")
    private Long grantees;
    @JsonProperty("totalGrantAmount")
    private Long totalGrantAmount;
    @JsonProperty("activeUsers")
    private Long activeUsers;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Summary(Long totalGrants, Long grantees, Long totalGrantAmount, Long activeUsers) {
        this.totalGrants = totalGrants;
        this.grantees = grantees;
        this.totalGrantAmount = totalGrantAmount;
        this.activeUsers = activeUsers;
    }

    @JsonProperty("totalGrants")
    public Long getTotalGrants() {
        return totalGrants;
    }

    @JsonProperty("totalGrants")
    public void setTotalGrants(Long totalGrants) {
        this.totalGrants = totalGrants;
    }

    @JsonProperty("grantees")
    public Long getGrantees() {
        return grantees;
    }

    @JsonProperty("grantees")
    public void setGrantees(Long grantees) {
        this.grantees = grantees;
    }

    @JsonProperty("totalGrantAmount")
    public Long getTotalGrantAmount() {
        return totalGrantAmount;
    }

    @JsonProperty("totalGrantAmount")
    public void setTotalGrantAmount(Long totalGrantAmount) {
        this.totalGrantAmount = totalGrantAmount;
    }

    @JsonProperty("activeUsers")
    public Long getActiveUsers() {
        return activeUsers;
    }

    @JsonProperty("activeUsers")
    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
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
