package org.codealpha.gmsservice.models;

import java.util.Map;

public class DocInfo {
    private Long attachmentId;
    private Map<String, Object> grant;

    public DocInfo(Long attachmentId, Map<String, Object> grant) {
        this.attachmentId = attachmentId;
        this.grant = grant;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Map<String, Object> getGrant() {
        return grant;
    }

    public void setGrant(Map<String, Object> grant) {
        this.grant = grant;
    }
}
