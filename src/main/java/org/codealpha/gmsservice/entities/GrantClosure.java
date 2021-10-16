package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.codealpha.gmsservice.models.ClosureDetailVO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "grant_closure")
public class GrantClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String reason;
    @Column
    private Long templateId;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Grant grant;
    @Column
    private Date movedOn;
    @Column
    private Long createBy;
    @Column
    private Date createdAt;
    @Column
    private Long updatedBy;
    @Column
    private Date updateAt;
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private WorkflowStatus status;
    @OneToMany(mappedBy = "grant")
    private List<ClosureAssignments> workflowAssignment;
    @Transient
    private ClosureDetailVO closureDetails;
    @OneToMany(mappedBy = "report", fetch = FetchType.EAGER)
    @JsonProperty("stringAttribute")
    @ApiModelProperty(name = "stringAttributes", value = "Report template structure with values", dataType = "List<ReportStringAttributes>")
    private List<ClosureStringAttribute> stringAttributes;




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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public Date getMovedOn() {
        return movedOn;
    }

    public void setMovedOn(Date movedOn) {
        this.movedOn = movedOn;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public List<ClosureAssignments> getWorkflowAssignment() {
        return workflowAssignment;
    }

    public void setWorkflowAssignment(List<ClosureAssignments> workflowAssignment) {
        this.workflowAssignment = workflowAssignment;
    }

    public ClosureDetailVO getClosureDetails() {
        return closureDetails;
    }

    public void setClosureDetails(ClosureDetailVO closureDetails) {
        this.closureDetails = closureDetails;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public List<ClosureStringAttribute> getStringAttributes() {
        return stringAttributes;
    }

    public void setStringAttributes(List<ClosureStringAttribute> stringAttributes) {
        this.stringAttributes = stringAttributes;
    }
}
