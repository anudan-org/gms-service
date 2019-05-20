package org.codealpha.gmsservice.models;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

public class SubmissionVO {

  private Long id;
  private Grant grant;
  private String title;
  private Date submitBy;
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

  public Date getSubmitBy() {
    return submitBy;
  }

  public void setSubmitBy(Date submitBy) {
    this.submitBy = submitBy;
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

  public SubmissionVO build(Submission submission,
      WorkflowPermissionService workflowPermissionService, User user, AppConfig submissionWindow)
      throws ParseException {
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
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }

    List<WorkFlowPermission> flowPermissions = workflowPermissionService
        .getSubmissionFlowPermissions(vo.getGrant().getGrantorOrganization().getId(),
            user.getRole().getId(), vo.submissionStatus.getId());
    Date submissionWindowStart = new DateTime(submission.getSubmitBy())
        .minusDays(Integer.valueOf(submissionWindow.getConfigValue()) + 1).toDate();

    if (!flowPermissions.isEmpty() && DateTime.now().toDate().after(submissionWindowStart)) {
      vo.setFlowAuthorities(flowPermissions);
    }

    WorkflowActionPermission actionPermission = workflowPermissionService
        .getSubmissionActionPermission(vo.grant.getGrantorOrganization().getId(),
            user.getRole().getId());
    vo.setActionAuthorities(actionPermission);
    return vo;
  }
}
