package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.constants.Frequency;
import org.codealpha.gmsservice.constants.KPIStatus;
import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.springframework.beans.BeanUtils;

public class GrantKpiVO {

  private Long id;
  private String title;
  private String description;
  private boolean scheduled;
  private int periodicity;
  private Frequency frequency;
  private KPIStatus status;
  private KpiType kpiType;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  @JsonIgnore
  private Grant grant;
  List<KpiSubmissionVO> submissions;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isScheduled() {
    return scheduled;
  }

  public void setScheduled(boolean scheduled) {
    this.scheduled = scheduled;
  }

  public int getPeriodicity() {
    return periodicity;
  }

  public void setPeriodicity(int periodicity) {
    this.periodicity = periodicity;
  }

  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  public KPIStatus getStatus() {
    return status;
  }

  public void setStatus(KPIStatus status) {
    this.status = status;
  }

  public KpiType getKpiType() {
    return kpiType;
  }

  public void setKpiType(KpiType kpiType) {
    this.kpiType = kpiType;
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

  public Grant getGrant() {
    return grant;
  }

  public void setGrant(Grant grant) {
    this.grant = grant;
  }

  public List<KpiSubmissionVO> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<KpiSubmissionVO> submissions) {
    this.submissions = submissions;
  }

  public GrantKpiVO build(GrantKpi kpi, WorkflowPermissionService workflowPermissionService,
      User user, AppConfig submissionWindow) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(kpi.getClass());
    GrantKpiVO vo = new GrantKpiVO();
    /*List<SubmissionVO> submissionVOList=null;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(kpi);
          PropertyDescriptor voPd = BeanUtils
              .getPropertyDescriptor(vo.getClass(), descriptor.getName());

          if (voPd.getName().equalsIgnoreCase("submissions")) {
            submissionVOList = new ArrayList<>();
            for(Submission submission: kpi.getSubmissions()) {
                KpiSubmissionVO submissionVO = new KpiSubmissionVO()
                    .build(submission, workflowPermissionService, user,
                        submissionWindow);
                submissionVOList.add(submissionVO);
            }

            vo.setSubmissions(submissionVOList);

          } else {
            voPd.getWriteMethod().invoke(vo, value);
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }*/
    return vo;
  }
}
