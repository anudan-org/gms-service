package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "report_string_attribute_attachments")
public class ReportStringAttributeAttachments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column
    private String location;
    @Column
    private int version;
    @Column
    private String title;
    @Column
    private String type;
    @Column
    private Date createdOn;
    @Column
    private String createdBy;
    @Column
    private Date updatedOn;
    @Column
    private String updatedBy;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @JsonIgnore
    private ReportStringAttribute reportStringAttribute;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ReportStringAttribute getReportStringAttribute() {
        return reportStringAttribute;
    }

    public void setReportStringAttribute(ReportStringAttribute reportStringAttribute) {
        this.reportStringAttribute = reportStringAttribute;
    }
}
