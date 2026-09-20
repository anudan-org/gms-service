package org.codealpha.gmsservice.models;

import java.util.Map;

public class ClosureSectionInfo {
    private Long sectionId;
    private String sectionName;
    private Map<String, Object> closure;

    public ClosureSectionInfo(Long id, String name, Map<String, Object> closure) {
        this.sectionId = id;
        this.sectionName = name;
        this.closure = closure;
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

    public Map<String, Object> getClosure() {
        return closure;
    }

    public void setClosure(Map<String, Object> closure) {
        this.closure = closure;
    }
}
