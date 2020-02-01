package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Report;

public class ReportDocInfo {
    private Long attachmentId;
    private Report report;

    public ReportDocInfo(Long attachmentId, Report grant) {
        this.attachmentId = attachmentId;
        this.report = grant;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
