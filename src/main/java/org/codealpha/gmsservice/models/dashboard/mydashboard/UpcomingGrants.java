
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
    "DraftGrants",
    "Grantsinmyworkflow",
    "GrantAmount"
})
@Generated("jsonschema2pojo")
public class UpcomingGrants {

    @JsonProperty("DraftGrants")
    private Long draftGrants;
    @JsonProperty("Grantsinmyworkflow")
    private Long grantsinmyworkflow;
    @JsonProperty("GrantAmount")
    private Long grantAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public UpcomingGrants() {
    }

    public UpcomingGrants(Long draftGrants, Long grantsinmyworkflow, Long grantAmount) {
        this.draftGrants = draftGrants;
        this.grantsinmyworkflow = grantsinmyworkflow;
        this.grantAmount = grantAmount;
    }

    @JsonProperty("DraftGrants")
    public Long getDraftGrants() {
        return draftGrants;
    }

    @JsonProperty("DraftGrants")
    public void setDraftGrants(Long draftGrants) {
        this.draftGrants = draftGrants;
    }

    @JsonProperty("Grantsinmyworkflow")
    public Long getGrantsinmyworkflow() {
        return grantsinmyworkflow;
    }

    @JsonProperty("Grantsinmyworkflow")
    public void setGrantsinmyworkflow(Long grantsinmyworkflow) {
        this.grantsinmyworkflow = grantsinmyworkflow;
    }

    @JsonProperty("GrantAmount")
    public Long getGrantAmount() {
        return grantAmount;
    }

    @JsonProperty("GrantAmount")
    public void setGrantAmount(Long grantAmount) {
        this.grantAmount = grantAmount;
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
