package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class SectionInfo {
    private Long sectionId;
    private String sectionName;
    private Grant grant;

    public SectionInfo(Long id, String name, Grant grant) {
        this.sectionId = id;
        this.sectionName = name;
        this.grant = grant;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }
}
