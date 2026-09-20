package org.codealpha.gmsservice.models;

import java.util.Map;

public class SectionInfo {
    private Long sectionId;
    private String sectionName;
    private Map<String, Object> grant;

    public SectionInfo(Long id, String name, Map<String, Object> grant) {
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

    public Map<String, Object> getGrant() {
        return grant;
    }

    public void setGrant(Map<String, Object> grant) {
        this.grant = grant;
    }
}
