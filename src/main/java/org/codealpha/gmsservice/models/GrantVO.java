package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.constants.GrantSubStatus;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.codealpha.gmsservice.entities.WorkflowStatePermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.entities.WorkflowStatusTransition;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class GrantVO {

  private Long id;
  private Organization organization;
  private Organization grantorOrganization;
  private String name;
  private String description;
  private Date createdAt;
  private String createdBy;
  private Date updatedAt;
  private String updatedBy;
  private WorkflowStatus grantStatus;
  private GrantStatus statusName;
  private WorkflowStatus substatus;
  private Date startDate;
  private Date endDate;
  private List<SubmissionVO> submissions;
  private WorkflowActionPermission actionAuthorities;
  private List<WorkFlowPermission> flowAuthorities;

  private static Logger logger = LoggerFactory.getLogger(GrantVO.class);

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public Organization getGrantorOrganization() {
    return grantorOrganization;
  }

  public void setGrantorOrganization(Organization grantorOrganization) {
    this.grantorOrganization = grantorOrganization;
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

  public WorkflowStatus getGrantStatus() {
    return grantStatus;
  }

  public void setGrantStatus(WorkflowStatus grantStatus) {
    this.grantStatus = grantStatus;
  }

  public GrantStatus getStatusName() {
    return statusName;
  }

  public void setStatusName(GrantStatus statusName) {
    this.statusName = statusName;
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

  public List<SubmissionVO> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<SubmissionVO> submissions) {
    this.submissions = submissions;
  }

  @OneToMany(mappedBy = "grant", fetch = FetchType.LAZY)
  public GrantVO build(Grant grant,
      WorkflowPermissionService workflowPermissionService,
      User user, AppConfig submissionWindow) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(grant.getClass());
    GrantVO vo = new GrantVO();
    List<SubmissionVO> submissionVOList = null;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grant);
          PropertyDescriptor voPd = BeanUtils
              .getPropertyDescriptor(vo.getClass(), descriptor.getName());
          if (voPd.getName().equalsIgnoreCase("submissions")) {
            submissionVOList = new ArrayList<>();
            for (Submission submission : (List<Submission>) value) {
              try {
                SubmissionVO submissionVO = new SubmissionVO()
                    .build(submission, workflowPermissionService, user, submissionWindow);

                submissionVOList.add(submissionVO);
              } catch (ParseException pe) {
                logger.error(pe.getMessage(), pe);
              }
            }
            vo.setSubmissions(submissionVOList);
            System.out.println("KPIS called");
          } else {
            voPd.getWriteMethod().invoke(vo, value);
          }
        } catch (IllegalAccessException e) {
          logger.error(e.getMessage(),e);
        } catch (InvocationTargetException e) {
          logger.error(e.getMessage(),e);
        }
      }
    }
    vo.setFlowAuthorities(workflowPermissionService
        .getGrantFlowPermissions(vo.grantorOrganization.getId(), user.getRole().getId()));
    vo.setActionAuthorities(workflowPermissionService
        .getGrantActionPermissions(vo.getGrantorOrganization().getId(),
            user.getRole().getId()));

    return vo;
  }


}
