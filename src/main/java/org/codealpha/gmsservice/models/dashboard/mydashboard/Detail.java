
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.codealpha.gmsservice.models.dashboard.mydashboard.DueOverdueSummary;
import org.codealpha.gmsservice.models.dashboard.mydashboard.Summary__1;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "summary",
    "DueOverdueSummary",
    "ApprovedReportsSummary"
})
@Generated("jsonschema2pojo")
public class Detail {

    @JsonProperty("name")
    private String name;
    @JsonProperty("summary")
    private Summary__1 summary;
    @JsonProperty("DueOverdueSummary")
    private List<DueOverdueSummary> dueOverdueSummary = null;
    @JsonProperty("ApprovedReportsSummary")
    private List<ApprovedReportsSummary> approvedReportsSummary = null;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("summary")
    public Summary__1 getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(Summary__1 summary) {
        this.summary = summary;
    }

    @JsonProperty("DueOverdueSummary")
    public List<DueOverdueSummary> getDueOverdueSummary() {
        return dueOverdueSummary;
    }

    @JsonProperty("DueOverdueSummary")
    public void setDueOverdueSummary(List<DueOverdueSummary> dueOverdueSummary) {
        this.dueOverdueSummary = dueOverdueSummary;
    }

    @JsonProperty("ApprovedReportsSummary")
    public List<ApprovedReportsSummary> getApprovedReportsSummary() {
        return approvedReportsSummary;
    }

    @JsonProperty("ApprovedReportsSummary")
    public void setApprovedReportsSummary(List<ApprovedReportsSummary> approvedReportsSummary) {
        this.approvedReportsSummary = approvedReportsSummary;
    }

}
