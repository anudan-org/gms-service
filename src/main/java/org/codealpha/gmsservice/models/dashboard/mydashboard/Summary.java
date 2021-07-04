
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.codealpha.gmsservice.models.dashboard.mydashboard.UpcomingGrants;

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

}
