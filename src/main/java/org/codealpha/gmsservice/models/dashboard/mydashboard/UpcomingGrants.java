
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import javax.annotation.Generated;
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
    private Long grantsInMyWorkflow;
    @JsonProperty("GrantAmount")
    private Long grantAmount;

    @JsonProperty("DraftGrants")
    public Long getDraftGrants() {
        return draftGrants;
    }

    @JsonProperty("DraftGrants")
    public void setDraftGrants(Long draftGrants) {
        this.draftGrants = draftGrants;
    }

    @JsonProperty("Grantsinmyworkflow")
    public Long getGrantsInMyWorkflow() {
        return grantsInMyWorkflow;
    }

    @JsonProperty("Grantsinmyworkflow")
    public void setGrantsInMyWorkflow(Long grantsInMyWorkflow) {
        this.grantsInMyWorkflow = grantsInMyWorkflow;
    }

    @JsonProperty("GrantAmount")
    public Long getGrantAmount() {
        return grantAmount;
    }

    @JsonProperty("GrantAmount")
    public void setGrantAmount(Long grantAmount) {
        this.grantAmount = grantAmount;
    }

}
