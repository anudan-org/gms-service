
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DraftClosures",
    "Closuresinmyworkflow",
    "ActualSpent"
})
@Generated("jsonschema2pojo")
public class UpcomingClosures {

    @JsonProperty("DraftClosures")
    private Long draftClosures;
    @JsonProperty("Closuresinmyworkflow")
    private Long closuresinmyworkflow;
    @JsonProperty("ActualSpent")
    private Long actualSpent;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public UpcomingClosures() {
    }

    public UpcomingClosures(Long draftClosures, Long closuresinmyworkflow, Long actualSpent) {
        this.draftClosures = draftClosures;
        this.closuresinmyworkflow = closuresinmyworkflow;
        this.actualSpent = actualSpent;
    }

    @JsonProperty("DraftClosures")
    public Long getDraftClosures() {
        return draftClosures;
    }

    @JsonProperty("DraftClosures")
    public void setDraftClosures(Long draftClosures) {
        this.draftClosures = draftClosures;
    }

    @JsonProperty("Closuresinmyworkflow")
    public Long getClosuresinmyworkflow() {
        return closuresinmyworkflow;
    }

    @JsonProperty("Closuresinmyworkflow")
    public void setClosuresinmyworkflow(Long closuresinmyworkflow) {
        this.closuresinmyworkflow = closuresinmyworkflow;
    }

    @JsonProperty("ActualSpent")
    public Long getActualSpent() {
        return actualSpent;
    }

    @JsonProperty("ActualSpent")
    public void setActualSpent(Long actualSpent) {
        this.actualSpent = actualSpent;
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
