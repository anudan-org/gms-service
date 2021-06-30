package org.codealpha.gmsservice.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GrantToFix {
    @Id
    private Long id;
    private String referenceNo;
    private String value;
    private String stringAttributes;
    private Long grantId;
    private String status;

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStringAttributes() {
        return stringAttributes;
    }

    public void setStringAttributes(String stringAttributes) {
        this.stringAttributes = stringAttributes;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
