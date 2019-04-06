package org.codealpha.gmsservice.models;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.constants.GrantSubStatus;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.Organization;
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
  private GrantStatus status;
  private GrantSubStatus substatus;
  private Date startDate;
  private Date endDate;
  private List<GrantKpi> kpis;
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

  public GrantStatus getStatus() {
    return status;
  }

  public void setStatus(GrantStatus status) {
    this.status = status;
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

  public List<GrantKpi> getKpis() {
    return kpis;
  }

  public void setKpis(List<GrantKpi> kpis) {
    this.kpis = kpis;
  }

  public GrantVO build(Grant grant){
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(grant.getClass());
    GrantVO vo = new GrantVO();
    for(PropertyDescriptor descriptor:propertyDescriptors){
      if(!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grant);
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
    return vo;
  }
}
