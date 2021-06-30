package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TableData {

    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String header;
    private ColumnData[] columns;
    private boolean enteredByGrantee;
    private boolean status;
    private boolean saved;
    private Long actualDisbursementId;
    private Long disbursementId;
    private Long reportId;
    private boolean showForGrantee = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnData[] getColumns() {
        return columns;
    }

    public void setColumns(ColumnData[] columns) {
        this.columns = columns;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isEnteredByGrantee() {
        return enteredByGrantee;
    }

    public void setEnteredByGrantee(boolean enteredByGrantee) {
        this.enteredByGrantee = enteredByGrantee;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public Long getActualDisbursementId() {
        return actualDisbursementId;
    }

    public void setActualDisbursementId(Long actualDisbursementId) {
        this.actualDisbursementId = actualDisbursementId;
    }

    public Long getDisbursementId() {
        return disbursementId;
    }

    public void setDisbursementId(Long disbursementId) {
        this.disbursementId = disbursementId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public boolean isShowForGrantee() {
        return showForGrantee;
    }

    public void setShowForGrantee(boolean showForGrantee) {
        this.showForGrantee = showForGrantee;
    }
}
