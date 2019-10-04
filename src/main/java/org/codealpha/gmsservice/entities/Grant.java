package org.codealpha.gmsservice.entities;

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
public class Grant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "organization_id")
  private Grantee organization;

  @ManyToOne
  @JoinColumn(name = "grantor_org_id")
  private Granter grantorOrganization;

  @OneToMany(mappedBy = "grant")
  @JsonProperty("kpis")
  @OrderBy("kpiType ASC")
  private List<GrantKpi> kpis;

  @OneToMany(mappedBy = "grant")
  @JsonProperty("stringAttribute")
  private List<GrantStringAttribute> stringAttributes;

  @OneToMany(mappedBy = "grant")
  @JsonProperty("docAttribute")
  private List<GrantDocumentAttributes> documentAttributes;

  @Column(name = "name",columnDefinition = "text")
  private String name;

  @Column(name = "description",columnDefinition = "text")
  private String description;

  @Column
  private Long templateId;

  @Transient
  private GranterGrantTemplate grantTemplate;

  @Column
  private Double amount;

  @Column
  private Date createdAt;

  @Column
  private String createdBy;

  @Column
  private Date updatedAt;

  @Column
  private String updatedBy;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus grantStatus;

  @Column
  @Enumerated(EnumType.STRING)
  private GrantStatus statusName;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus substatus;

  @Column
  private Date startDate;
  @Transient
  private String stDate;

  @Column
  private Date endDate;
  @Transient
  private String enDate;

  @Column
  private String representative;

  @Column
  private String note;

  @Column
  private Date noteAdded;

  @Column
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String noteAddedBy;

  @Transient
  private User noteAddedByUser;

  @OneToMany(mappedBy = "grant", cascade = CascadeType.ALL)
  @OrderBy("submitBy ASC")
  @JsonManagedReference
  private List<Submission> submissions;

  @Transient
  private WorkflowActionPermission actionAuthorities;
  @Transient
  private List<WorkFlowPermission> flowAuthorities;
  @Transient
  private GrantDetailVO grantDetails;
  @Transient
  private List<AssignedTo> currentAssignment;
  @Transient
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
