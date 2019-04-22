package org.codealpha.gmsservice.models;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.constants.GrantSubStatus;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.springframework.beans.BeanUtils;

public class GrantVO {

  private Long id;
  private Organization organization;
  private Organization grantorOrganization;
  private String name;
  private String description;
  private LocalDateTime createdAt;
  private String createdBy;
  private LocalDateTime updatedAt;
  private String updatedBy;
  private WorkflowStatus grantStatus;
  private GrantStatus statusName;
  private GrantSubStatus substatus;
  private Date startDate;
  private Date endDate;
  private List<GrantKpiVO> kpis;
  private List<WorkFlowPermission> flowAuthority;
  private WorkflowActionPermission actionAuthority;
  private List<String> alerts;
  private List<String> notifications;

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public GrantStatus getStatusName() {
    return statusName;
  }

  public void setStatusName(GrantStatus status) {
    this.statusName = status;
  }

  public GrantSubStatus getSubstatus() {
    return substatus;
  }

  public void setSubstatus(GrantSubStatus substatus) {
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

  public List<String> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<String> alerts) {
    this.alerts = alerts;
  }

  public List<String> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<String> notifications) {
    this.notifications = notifications;
  }

  public List<GrantKpiVO> getKpis() {
    return kpis;
  }

  public void setKpis(List<GrantKpiVO> kpis) {
    this.kpis = kpis;
  }

  public List<WorkFlowPermission> getFlowAuthority() {
    return flowAuthority;
  }

  public void setFlowAuthority(
      List<WorkFlowPermission> permissions) {
    this.flowAuthority = permissions;
  }

  public WorkflowActionPermission getActionAuthority() {
    return actionAuthority;
  }

  public void setActionAuthority(
      WorkflowActionPermission actionAuthority) {
    this.actionAuthority = actionAuthority;
  }

  public WorkflowStatus getGrantStatus() {
    return grantStatus;
  }

  public void setGrantStatus(WorkflowStatus status) {
    this.grantStatus = status;
  }

  public GrantVO build(Grant grant,
      WorkflowPermissionService workflowPermissionService,
      User user, AppConfig submissionWindow) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(grant.getClass());
    GrantVO vo = new GrantVO();
    List<GrantKpiVO> grantKpiVOList = null;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grant);
          PropertyDescriptor voPd = BeanUtils
              .getPropertyDescriptor(vo.getClass(), descriptor.getName());
          if(voPd.getName().equalsIgnoreCase("kpis")){
            grantKpiVOList = new ArrayList<>();
            for(GrantKpi kpi :(List<GrantKpi>)value){
              GrantKpiVO kpiVO = new GrantKpiVO().build(kpi,workflowPermissionService,user,submissionWindow);
              grantKpiVOList.add(kpiVO);
            }
            vo.setKpis(grantKpiVOList);
            System.out.println("KPIS called");
          }else {
            voPd.getWriteMethod().invoke(vo, value);
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    vo.setFlowAuthority(workflowPermissionService
        .getGrantFlowPermissions(vo.grantorOrganization.getId(), user.getRole().getId()));
    vo.setActionAuthority(workflowPermissionService
        .getGrantActionPermissions(vo.getGrantorOrganization().getId(),
            user.getRole().getId()));

    return vo;
  }


}
