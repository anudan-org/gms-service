
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DraftReports",
    "Reportsinmyworkflow",
    "ReportAmount"
})
@Generated("jsonschema2pojo")
public class UpcomingReports {

    @JsonProperty("DraftReports")
    private Long draftReports;
    @JsonProperty("Reportsinmyworkflow")
    private Long reportsinmyworkflow;
    @JsonProperty("ReportAmount")
    private Long reportAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public UpcomingReports() {
    }

    public UpcomingReports(Long draftReports, Long reportsinmyworkflow, Long reportAmount) {
        this.draftReports = draftReports;
        this.reportsinmyworkflow = reportsinmyworkflow;
        this.reportAmount = reportAmount;
    }

    @JsonProperty("DraftReports")
    public Long getDraftReports() {
        return draftReports;
    }

    @JsonProperty("DraftReports")
    public void setDraftReports(Long draftReports) {
        this.draftReports = draftReports;
    }

    @JsonProperty("Reportsinmyworkflow")
    public Long getReportsinmyworkflow() {
        return reportsinmyworkflow;
    }

    @JsonProperty("Reportsinmyworkflow")
    public void setReportsinmyworkflow(Long reportsinmyworkflow) {
        this.reportsinmyworkflow = reportsinmyworkflow;
    }

    @JsonProperty("ReportAmount")
    public Long getReportAmount() {
        return reportAmount;
    }

    @JsonProperty("ReportAmount")
    public void setReportAmount(Long reportAmount) {
        this.reportAmount = reportAmount;
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
