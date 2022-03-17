package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SubmissionVO {

  private static Logger logger = LoggerFactory.getLogger(SubmissionVO.class);

  private Long id;
  private Grant grant;
  private String title;
  private String submitBy;
  protected Date submittedOn;
  private WorkflowStatus submissionStatus;
  private List<GrantQuantitativeKpiData> quantitiaveKpisubmissions;
  private List<GrantQualitativeKpiData> qualitativeKpiSubmissions;
  private List<GrantDocumentKpiData> documentKpiSubmissions;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  private WorkflowActionPermission actionAuthorities;
  private List<WorkFlowPermission> flowAuthorities;
  private List<SubmissionNote> submissionNotes;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Grant getGrant() {
    return grant;
  }

  public void setGrant(Grant grant) {
    this.grant = grant;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubmitBy() {
    return submitBy;
  }

  public void setSubmitBy(Date submitBy) {

    this.submitBy = new SimpleDateFormat("yyyy-MM-dd").format(submitBy);
  }

  public Date getSubmittedOn() {
    return submittedOn;
  }

  public void setSubmittedOn(Date submittedOn) {
    this.submittedOn = submittedOn;
  }

  public WorkflowStatus getSubmissionStatus() {
    return submissionStatus;
  }

  public void setSubmissionStatus(WorkflowStatus submissionStatus) {
    this.submissionStatus = submissionStatus;
  }

  public List<GrantQuantitativeKpiData> getQuantitiaveKpisubmissions() {
    return quantitiaveKpisubmissions;
  }

  public void setQuantitiaveKpisubmissions(
      List<GrantQuantitativeKpiData> quantitiaveKpisubmissions) {
    this.quantitiaveKpisubmissions = quantitiaveKpisubmissions;
  }

  public List<GrantQualitativeKpiData> getQualitativeKpiSubmissions() {
    return qualitativeKpiSubmissions;
  }

  public void setQualitativeKpiSubmissions(
      List<GrantQualitativeKpiData> qualitativeKpiSubmissions) {
    this.qualitativeKpiSubmissions = qualitativeKpiSubmissions;
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

  public List<GrantDocumentKpiData> getDocumentKpiSubmissions() {
    return documentKpiSubmissions;
  }

  public void setDocumentKpiSubmissions(
      List<GrantDocumentKpiData> documentKpiSubmissions) {
    this.documentKpiSubmissions = documentKpiSubmissions;
  }

  public List<SubmissionNote> getSubmissionNotes() {
    return submissionNotes;
  }

  public void setSubmissionNotes(
      List<SubmissionNote> submissionNotes) {
    this.submissionNotes = submissionNotes;
  }

  public SubmissionVO build(Submission submission,
      WorkflowPermissionService workflowPermissionService, User user, AppConfig submissionWindow)
       {
    PropertyDescriptor[] propertyDescriptors = BeanUtils
        .getPropertyDescriptors(submission.getClass());
    SubmissionVO vo = new SubmissionVO();
    List<SubmissionVO> submissionVOList = null;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(submission);
          PropertyDescriptor voPd = BeanUtils
              .getPropertyDescriptor(vo.getClass(), descriptor.getName());

          voPd.getWriteMethod().invoke(vo, value);

        } catch (IllegalAccessException e) {
          logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }

    List<WorkFlowPermission> flowPermissions = workflowPermissionService
        .getSubmissionFlowPermissions(vo.getGrant().getGrantorOrganization().getId(),
            user.getUserRoles(), vo.submissionStatus.getId());
    Date submissionWindowStart = new DateTime(submission.getSubmitBy())
        .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

    if (!flowPermissions.isEmpty() && DateTime.now().toDate().after(submissionWindowStart)) {
      vo.setFlowAuthorities(flowPermissions);
    }

    WorkflowActionPermission actionPermission = workflowPermissionService
        .getSubmissionActionPermission(vo.grant.getGrantorOrganization().getId(),
            user.getUserRoles());
    vo.setActionAuthorities(actionPermission);
    return vo;
  }
}
