
package org.codealpha.gmsservice.models.dashboard.mydashboard;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "disbursement"
})
@Generated("jsonschema2pojo")
public class Summary__1 {

    @JsonProperty("disbursement")
    private List<Disbursement> disbursement = null;

    @JsonProperty("disbursement")
    public List<Disbursement> getDisbursement() {
        return disbursement;
    }

    @JsonProperty("disbursement")
    public void setDisbursement(List<Disbursement> disbursement) {
        this.disbursement = disbursement;
    }

}
