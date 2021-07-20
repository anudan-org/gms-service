
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DraftDisbursements",
    "Disbursementsinmyworkflow",
    "DisbursementAmount"
})
@Generated("jsonschema2pojo")
public class UpcomingDisbursements {

    @JsonProperty("DraftDisbursements")
    private Long draftDisbursements;
    @JsonProperty("Disbursementsinmyworkflow")
    private Long disbursementsinmyworkflow;
    @JsonProperty("DisbursementAmount")
    private Long disbursementAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public UpcomingDisbursements() {
    }

    public UpcomingDisbursements(Long draftDisbursements, Long disbursementsinmyworkflow, Long disbursementAmount) {
        this.draftDisbursements = draftDisbursements;
        this.disbursementsinmyworkflow = disbursementsinmyworkflow;
        this.disbursementAmount = disbursementAmount;
    }

    @JsonProperty("DraftDisbursements")
    public Long getDraftDisbursements() {
        return draftDisbursements;
    }

    @JsonProperty("DraftDisbursements")
    public void setDraftDisbursements(Long draftDisbursements) {
        this.draftDisbursements = draftDisbursements;
    }

    @JsonProperty("Disbursementsinmyworkflow")
    public Long getDisbursementsinmyworkflow() {
        return disbursementsinmyworkflow;
    }

    @JsonProperty("Disbursementsinmyworkflow")
    public void setDisbursementsinmyworkflow(Long disbursementsinmyworkflow) {
        this.disbursementsinmyworkflow = disbursementsinmyworkflow;
    }

    @JsonProperty("DisbursementAmount")
    public Long getDisbursementAmount() {
        return disbursementAmount;
    }

    @JsonProperty("DisbursementAmount")
    public void setDisbursementAmount(Long disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
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
