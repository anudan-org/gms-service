package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.models.AssignedTo;
import org.codealpha.gmsservice.models.GrantAssignmentsVO;
import org.codealpha.gmsservice.models.GrantDetailVO;
import org.hibernate.validator.constraints.CodePointLength;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Entity
@Table(name = "grants")
@ApiModel(value = "Grant Model",description = "Data model of a Grant")
public class Grant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  @ApiModelProperty(name = "id",value = "Unique identifier of the grant",dataType = "Long")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "organization_id")
  @ApiModelProperty(name = "organization",value = "Grantee organization associated with the grant",dataType = "Organization")
  private Grantee organization;

  @ManyToOne
  @JoinColumn(name = "grantor_org_id")
  @ApiModelProperty(name = "grantorOrganization",value = "Granter or tenant organization associated with the grant",dataType = "Granter")
  private Granter grantorOrganization;

  @OneToMany(mappedBy = "grant")
  @JsonProperty("kpis")
  @OrderBy("kpiType ASC")
  @JsonIgnore
  private List<GrantKpi> kpis;

  @OneToMany(mappedBy = "grant")
  @JsonProperty("stringAttribute")
  @ApiModelProperty(name = "stringAttributes",value = "Grant template structure with values",dataType = "List<GrantStringAttributes>")
  private List<GrantStringAttribute> stringAttributes;

  @OneToMany(mappedBy = "grant")
  @JsonProperty("docAttribute")
  @JsonIgnore
  private List<GrantDocumentAttributes> documentAttributes;

  @Column(name = "name",columnDefinition = "text")
  @ApiModelProperty(name = "name",value = "Title of the grant",dataType = "String")
  private String name;

  @Column(name = "description",columnDefinition = "text")
  @ApiModelProperty(name = "description",value = "Description of the grant",dataType = "String")
  private String description;

  @Column
  @ApiModelProperty(name = "templateId",value = "Unique identified of teamplte associated with the grant",dataType = "Long")
  private Long templateId;

  @Transient
  @ApiModelProperty(name = "grantTemplate",value = "Template associated with the grant",dataType = "GranterGrantTemplate")
  private GranterGrantTemplate grantTemplate;

  @Column
  @ApiModelProperty(name = "amount",value = "Grant amount",dataType = "Double")
  private Double amount;

  @Column
  @ApiModelProperty(name = "createdAt",value = "Date when grant was created",dataType = "Date")
  private Date createdAt;

  @Column
  @ApiModelProperty(name = "createdBy",value = "Email id of user who created the grant",dataType = "String")
  private String createdBy;

  @Column
  @ApiModelProperty(name = "updatedAt",value = "Date when grant was updated",dataType = "Date")
  private Date updatedAt;

  @Column
  @ApiModelProperty(name = "updatedBy",value = "Email id of user who updated the grant",dataType = "String")
  private String updatedBy;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  @ApiModelProperty(name = "grantStatus",value = "Grant workflow status",dataType = "WorkflowStatus")
  private WorkflowStatus grantStatus;

  @Column
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(name = "statusName",value = "Grant status in text format",dataType = "String")
  private GrantStatus statusName;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private WorkflowStatus substatus;

  @Column
  @ApiModelProperty(name = "startDate",value = "Start date of the grant",dataType = "Date")
  private Date startDate;

  @Transient
  @ApiModelProperty(name = "stDate",value = "Formatted start date of the grant ",dataType = "String")
  private String stDate;

  @Column
  @ApiModelProperty(name = "endDate",value = "End date of the grant",dataType = "Date")
  private Date endDate;
  @Transient
  @ApiModelProperty(name = "enDate",value = "Formatted end date of the grant",dataType = "Date")
  private String enDate;

  @Column
  @ApiModelProperty(name = "representative",value = "Name of representative from Grantee organization",dataType = "String")
  private String representative;

  @Column
  @ApiModelProperty(name = "note",value = "Current note associated with the grant",dataType = "String")
  private String note;

  @Column
  @ApiModelProperty(name = "noteAdded",value = "Date when current note was associated with the grant",dataType = "Date")
  private Date noteAdded;

  @Column
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(name = "noteAddedBy",value = "Email id of the user who added the current note",dataType = "String")
  private String noteAddedBy;

  @Transient
  @ApiModelProperty(name = "noteAddedByUser",value = "User who added the current note",dataType = "User")
  private User noteAddedByUser;

  @OneToMany(mappedBy = "grant", cascade = CascadeType.ALL)
  @OrderBy("submitBy ASC")
  @JsonManagedReference
  @JsonIgnore
  private List<Submission> submissions;

  @Transient
  @ApiModelProperty(name = "actionAuthorities",value = "Allowed actions that can be performed by the user for current grant status",dataType = "List<WorkflowActionPermission>")
  private WorkflowActionPermission actionAuthorities;
  @Transient
  @ApiModelProperty(name = "actionAuthorities",value = "Allowed workflow status changes that can be performed by the user for current grant status",dataType = "List<WorkflowPermission>")
  private List<WorkFlowPermission> flowAuthorities;
  @Transient
  @ApiModelProperty(name = "grantDetails",value = "All grant section and section attributes and values of the grant",dataType = "GrantDetailVO")
  private GrantDetailVO grantDetails;
  @Transient
  @ApiModelProperty(name = "currentAssignment",value = "Current owner of grant based on grant status",dataType = "List<AssignedTo>")
  private List<AssignedTo> currentAssignment;
  @Transient
  @ApiModelProperty(name = "workflowAssignment",value = "Allowed workflow ownership assignments for the grant",dataType = "List<GrantAssignmentsVO>")
  private List<GrantAssignmentsVO> workflowAssignment;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Grantee organization) {
    this.organization = organization;
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


  public Organization getGrantorOrganization() {
    return grantorOrganization;
  }

  public void setGrantorOrganization(Granter grantorOrganization) {
    this.grantorOrganization = grantorOrganization;
  }

  public GrantStatus getStatusName() {
    return statusName;
  }

  public void setStatusName(GrantStatus status) {
    this.statusName = status;
  }

  public WorkflowStatus getSubstatus() {
    return substatus;
  }

  public void setSubstatus(WorkflowStatus substatus) {
    this.substatus = substatus;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public List<Submission> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<Submission> submissions) {
    this.submissions = submissions;
  }

  public WorkflowStatus getGrantStatus() {
    return grantStatus;
  }

  public void setGrantStatus(WorkflowStatus status) {
    this.grantStatus = status;
  }

  public List<GrantStringAttribute> getStringAttributes() {
    return stringAttributes;
  }

  public void setStringAttributes(
      List<GrantStringAttribute> stringAttributes) {
    this.stringAttributes = stringAttributes;
  }

  public List<GrantDocumentAttributes> getDocumentAttributes() {
    return documentAttributes;
  }

  public void setDocumentAttributes(
      List<GrantDocumentAttributes> documentAttributes) {
    this.documentAttributes = documentAttributes;
  }

  public List<GrantKpi> getKpis() {
    return kpis;
  }

  public void setKpis(List<GrantKpi> kpis) {
    this.kpis = kpis;
  }

  public WorkflowActionPermission getActionAuthorities() {
    return actionAuthorities;
  }

  public void setActionAuthorities(
      WorkflowActionPermission actionAuthorities) {
    this.actionAuthorities = actionAuthorities;
  }

  public List<WorkFlowPermission> getFlowAuthorities() {
    return flowAuthorities;
  }

  public void setFlowAuthorities(
      List<WorkFlowPermission> flowAuthorities) {
    this.flowAuthorities = flowAuthorities;
  }

  public GrantDetailVO getGrantDetails() {
    return grantDetails;
  }

  public void setGrantDetails(GrantDetailVO grantDetails) {
    this.grantDetails = grantDetails;
  }

  public String getStDate() {
    if(startDate==null){
      return "";
    }

    return new SimpleDateFormat("yyyy-MM-dd").format(startDate);
  }

  public void setStDate(String stDate) {
    this.stDate = stDate;
  }

  public String getEnDate() {

  if(endDate==null){
      return "";
    }
    return new SimpleDateFormat("yyyy-MM-dd").format(endDate);
  }

  public void setEnDate(String enDate) {
    this.enDate = enDate;
  }

  public void setAmount(Double amount){
    this.amount = amount;
  }

  public Double getAmount(){
    return this.amount;
  }

  public void setRepresentative(String rep){
    this.representative = rep;
  }

  public String getRepresentative(){
    return this.representative;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

    public GranterGrantTemplate getGrantTemplate() {
        return grantTemplate;
    }

    public void setGrantTemplate(GranterGrantTemplate grantTemplate) {
        this.grantTemplate = grantTemplate;
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

  public List<AssignedTo> getCurrentAssignment() {
    return currentAssignment;
  }

  public void setCurrentAssignment(List<AssignedTo> currentAssignment) {
    this.currentAssignment = currentAssignment;
  }

  public List<GrantAssignmentsVO> getWorkflowAssignment() {
    return workflowAssignment;
  }

  public void setWorkflowAssignment(List<GrantAssignmentsVO> workflowAssignment) {
    this.workflowAssignment = workflowAssignment;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Date getNoteAdded() {
    return noteAdded;
  }

  public void setNoteAdded(Date noteAdded) {
    this.noteAdded = noteAdded;
  }

  public String getNoteAddedBy() {
    return noteAddedBy;
  }

  public void setNoteAddedBy(String noteAddedBy) {
    this.noteAddedBy = noteAddedBy;
  }

  public User getNoteAddedByUser() {
    return noteAddedByUser;
  }

  public void setNoteAddedByUser(User noteAddedByUser) {
    this.noteAddedByUser = noteAddedByUser;
  }
}
