package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.springframework.beans.BeanUtils;

public class KpiSubmissionVO {

  private Long Id;
  @JsonIgnore
  private GrantKpi grantKpi;
  private Date submitByDate;
  private Date submittedOn;
  private WorkflowStatus submissionStatus;
  private List<GrantQuantitativeKpiDataVO> grantQuantitativeKpiData;
  private List<GrantQualitativeKpiDataVO> grantQualitativeKpiData;
  private String statusName;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  List<WorkFlowPermission> flowAuthority;
  private String title;

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }

  public GrantKpi getGrantKpi() {
    return grantKpi;
  }

  public void setGrantKpi(GrantKpi grantKpi) {
    this.grantKpi = grantKpi;
  }

  public Date getSubmitByDate() {
    return submitByDate;
  }

  public void setSubmitByDate(Date submitByDate) {
    this.submitByDate = submitByDate;
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

  public void setSubmissionStatus(WorkflowStatus status) {
    this.submissionStatus = status;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
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

  public List<GrantQuantitativeKpiDataVO> getGrantQuantitativeKpiData() {
    return grantQuantitativeKpiData;
  }

  public void setGrantQuantitativeKpiData(
      List<GrantQuantitativeKpiDataVO> grantQuantitativeKpiData) {
    this.grantQuantitativeKpiData = grantQuantitativeKpiData;
  }

  public List<GrantQualitativeKpiDataVO> getGrantQualitativeKpiData() {
    return grantQualitativeKpiData;
  }

  public void setGrantQualitativeKpiData(
      List<GrantQualitativeKpiDataVO> grantQualitativeKpiData) {
    this.grantQualitativeKpiData = grantQualitativeKpiData;
  }

  public List<WorkFlowPermission> getFlowAuthority() {
    return flowAuthority;
  }

  public void setFlowAuthority(
      List<WorkFlowPermission> flowAuthority) {
    this.flowAuthority = flowAuthority;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /*public KpiSubmissionVO build(QuantitativeKpiSubmission submission, WorkflowPermissionService workflowPermissionService, User user, AppConfig submissionWindow) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(submission.getClass());
    KpiSubmissionVO vo = new KpiSubmissionVO();
    /*List<GrantQuantitativeKpiDataVO> quantitativeKpiDataList=null;
    List<GrantQualitativeKpiDataVO> qualitativeKpiDataList=null;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(submission);
          PropertyDescriptor voPd = BeanUtils
              .getPropertyDescriptor(vo.getClass(), descriptor.getName());

          if (voPd.getName().equalsIgnoreCase("grantQuantitativeKpiData")) {
            quantitativeKpiDataList = new ArrayList<>();
              for (GrantQuantitativeKpiData grantQuantitativeKpiData : submission.getGrantQuantitativeKpiData()) {
                GrantQuantitativeKpiDataVO quantitativeKpiDataVO = new GrantQuantitativeKpiDataVO()
                    .build(grantQuantitativeKpiData, workflowPermissionService, user,
                        submissionWindow);
                quantitativeKpiDataList.add(quantitativeKpiDataVO);
              }
            vo.setGrantQuantitativeKpiData(quantitativeKpiDataList);

          } else if (voPd.getName().equalsIgnoreCase("grantQualitativeKpiData")) {

            qualitativeKpiDataList = new ArrayList<>();
              for (GrantQualitativeKpiData grantQualitativeKpiData : submission.getGrantQualitativeKpiData()) {
                GrantQualitativeKpiDataVO qualitativeKpiDataVO = new GrantQualitativeKpiDataVO()
                    .build(grantQualitativeKpiData, workflowPermissionService, user,
                        submissionWindow);
                qualitativeKpiDataList.add(qualitativeKpiDataVO);
              }
            vo.setGrantQualitativeKpiData(qualitativeKpiDataList);
          } else {
            voPd.getWriteMethod().invoke(vo, value);
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }

    DateTime today = DateTime.now();
    if (today.isBefore(
        new DateTime(submission.getSubmitByDate())
            .withTimeAtStartOfDay()
            .plusDays(1)) && today
        .isAfter(new DateTime(submission.getSubmitByDate())
            .minusDays(Integer.valueOf(submissionWindow.getConfigValue())))
        && !workflowPermissionService.getKPIFlowPermissions(
        submission.getGrantKpi().getGrant()
            .getGrantorOrganization().getId(),
        user.getRole().getId(), submission.getSubmissionStatus().getId())
        .isEmpty()) {
      vo.setFlowAuthority(workflowPermissionService.getKPIFlowPermissions(
          submission.getGrantKpi().getGrant()
              .getGrantorOrganization().getId(),
          user.getRole().getId(), submission.getSubmissionStatus().getId()));
    }
    return vo;
  }*/
}
