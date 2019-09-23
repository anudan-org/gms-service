package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

public class DocInfo {
    private Long attachmentId;
    private Grant grant;

    public DocInfo(Long attachmentId, Grant grant) {
        this.attachmentId = attachmentId;
        this.grant = grant;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }
}
