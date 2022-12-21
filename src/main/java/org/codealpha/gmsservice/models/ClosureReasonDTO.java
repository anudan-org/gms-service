package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Organization;

import java.util.Date;

public class ClosureReasonDTO {
    private Long id;
    private String reason;
    private Organization organization;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
    private boolean enabled;
    private boolean deleted;
    private Long usagecount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    

    public ClosureReasonDTO(Long id, String reason, Organization organization, Date createdAt, String createdBy,
            Date updatedAt, String updatedBy, boolean enabled, boolean deleted, Long usagecount) {
        this.id = id;
        this.reason = reason;
        this.organization = organization;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.enabled = enabled;
        this.deleted = deleted;
        this.usagecount = usagecount;
    }
    

    public ClosureReasonDTO() {
    }

    public Long getUsagecount() {
        return usagecount;
    }

    public void setUsagecount(Long usagecount) {
        this.usagecount = usagecount;
    }
}
