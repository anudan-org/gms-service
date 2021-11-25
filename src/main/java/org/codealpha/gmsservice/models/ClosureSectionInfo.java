package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.codealpha.gmsservice.entities.Report;

public class ClosureSectionInfo {
    private Long sectionId;
    private String sectionName;
    private GrantClosure closure;

    public ClosureSectionInfo(Long id, String name, GrantClosure closure) {
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

    public GrantClosure getClosure() {
        return closure;
    }

    public void setClosure(GrantClosure closure) {
        this.closure = closure;
    }
}
