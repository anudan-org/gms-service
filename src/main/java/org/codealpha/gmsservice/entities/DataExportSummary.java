package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "data_extract_logs")
public class DataExportSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String summaryFor;
    @Column(name = "extract_request_by")
    private String extractRequestBy;
    @Column
    private Date extractRequestedOn;
    @Column
    private Integer recordsRetrieved;

    public DataExportSummary(String summaryFor, String extractRequestBy, Date extractRequestedOn, Integer recordsRetrieved) {
        this.summaryFor = summaryFor;
        this.extractRequestBy = extractRequestBy;
        this.extractRequestedOn = extractRequestedOn;
        this.recordsRetrieved = recordsRetrieved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummaryFor() {
        return summaryFor;
    }

    public void setSummaryFor(String summaryFor) {
        this.summaryFor = summaryFor;
    }

    public String getExtractRequestBy() {
        return extractRequestBy;
    }

    public void setExtractRequestBy(String extractRequestBy) {
        this.extractRequestBy = extractRequestBy;
    }

    public Date getExtractRequestedOn() {
        return extractRequestedOn;
    }

    public void setExtractRequestedOn(Date extractRequestedOn) {
        this.extractRequestedOn = extractRequestedOn;
    }

    public Integer getRecordsRetrieved() {
        return recordsRetrieved;
    }

    public void setRecordsRetrieved(Integer recordsRetrieved) {
        this.recordsRetrieved = recordsRetrieved;
    }
}
