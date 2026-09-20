package org.codealpha.gmsservice.models;

import java.util.Map;

public class ClosureDocInfo {
    private Long attachmentId;
    private Map<String, Object> closure;

    public ClosureDocInfo(Long attachmentId, Map<String, Object> closure) {
        this.attachmentId = attachmentId;
        this.closure = closure;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Map<String, Object> getClosure() {
        return closure;
    }

    public void setClosure(Map<String, Object> closure) {
        this.closure = closure;
    }
}
