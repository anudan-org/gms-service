package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "disbursement_documents")
public class DisbursementDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String location;
    @Column
    private Date uploadedOn;
    @Column
    private Long uploadedBy;
    @Column
    private String name;
    @Column
    private String extension;
    @Column
    private Long disbursementId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Date uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getDisbursementId() {
        return disbursementId;
    }

    public void setDisbursementId(Long grantId) {
        this.disbursementId = grantId;
    }

}