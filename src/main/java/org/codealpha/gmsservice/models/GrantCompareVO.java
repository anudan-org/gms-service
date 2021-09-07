package org.codealpha.gmsservice.models;

import java.util.List;

public class GrantCompareVO {
    private String checkType;
    private List<PlainGrant> grants;

    public GrantCompareVO(String checkType, List<PlainGrant> grantToCompare) {
        this.checkType = checkType;
        this.grants = grantToCompare;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public List<PlainGrant> getGrants() {
        return grants;
    }

    public void setGrants(List<PlainGrant> grants) {
        this.grants = grants;
    }
}
