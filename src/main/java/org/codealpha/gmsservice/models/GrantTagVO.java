package org.codealpha.gmsservice.models;

public class GrantTagVO {

    private Long id;
    private String tagName;
    private Long grantId;
    private Long orgTagId;

    public GrantTagVO() {
    }

    public GrantTagVO(Long id, String tagName, Long grantId, Long orgTagId) {
        this.id = id;
        this.tagName = tagName;
        this.grantId = grantId;
        this.orgTagId = orgTagId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
    }

    public Long getOrgTagId() {
        return orgTagId;
    }

    public void setOrgTagId(Long orgTagId) {
        this.orgTagId = orgTagId;
    }
}
