package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.GrantClosure;
import org.codealpha.gmsservice.entities.Report;

public class ClosureDocInfo {
    private Long attachmentId;
    private GrantClosure closure;

    public ClosureDocInfo(Long attachmentId, GrantClosure closure) {
        this.attachmentId = attachmentId;
        this.closure = closure;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public GrantClosure getClosure() {
        return closure;
    }

    public void setClosure(GrantClosure closure) {
        this.closure = closure;
    }
}
