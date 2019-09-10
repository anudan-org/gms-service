package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class GrantVO {

  private Long id;
  private Grantee organization;
  private Granter grantorOrganization;
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
  private String stDate;
  private Date endDate;
  private String enDate;
  private String representative;
  private Double amount;
  private List<AssignedTo> currentAssignment;
  private List<Submission> submissions;
  private WorkflowActionPermission actionAuthorities;
  private List<WorkFlowPermission> flowAuthorities;
  private GrantDetailVO grantDetails;
  private List<GrantKpi> kpis;
  private Long templateId;
  private GranterGrantTemplate grantTemplate;
  private Long grantId;
  @JsonIgnore
  private List<GrantStringAttribute> stringAttributes;
  @JsonIgnore
  private List<GrantDocumentAttributes> documentAttributes;

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

  public void setOrganization(Grantee organization) {
    this.organization = organization;
  }

  public Organization getGrantorOrganization() {
    return grantorOrganization;
  }

  public void setGrantorOrganization(Granter grantorOrganization) {
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

  public List<AssignedTo> getCurrentAssignment() {
    return currentAssignment;
  }

  public void setCurrentAssignment(List<AssignedTo> currentAssignment) {
    this.currentAssignment = currentAssignment;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {

    this.endDate = endDate;
  }

  public String getStDate() {
    return stDate;
  }

  public void setStDate(String stDate) {
    this.stDate = stDate;
  }

  public String getEnDate() {
    return enDate;
  }

  public void setEnDate(String enDate) {
    this.enDate = enDate;
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

  public List<Submission> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<Submission> submissions) {
    this.submissions = submissions;
  }

  public GrantDetailVO getGrantDetails() {
    return grantDetails;
  }

  public void setGrantDetails(GrantDetailVO attributes) {
    this.grantDetails = attributes;
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
    Collections.sort(kpis);
    this.kpis = kpis;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

    public Long getGrantId() {
        return grantId;
    }

    public void setGrantId(Long grantId) {
        this.grantId = grantId;
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

    public GranterGrantTemplate getGrantTemplate() {
        return grantTemplate;
    }

    public void setGrantTemplate(GranterGrantTemplate grantTemplate) {
        this.grantTemplate = grantTemplate;
    }

    public GrantVO build(Grant grant, List<GrantSpecificSection> sections,
                         WorkflowPermissionService workflowPermissionService,
                         User user, AppConfig submissionWindow) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(grant.getClass());
    GrantVO vo = new GrantVO();
    Submission submissionVOList = null;
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grant);
          PropertyDescriptor voPd = BeanUtils
              .getPropertyDescriptor(vo.getClass(), descriptor.getName());
           if (voPd.getName().equalsIgnoreCase("stringAttributes")) {
            GrantDetailVO grantDetailVO = null;
            grantDetailVO = vo.getGrantDetails();
            if(grantDetailVO == null){
              grantDetailVO = new GrantDetailVO();
            }
            grantDetailVO = grantDetailVO.buildStringAttributes(sections, (List<GrantStringAttribute>) value);
            vo.setGrantDetails(grantDetailVO);
          } else if (voPd.getName().equalsIgnoreCase("documentAttributes")) {
            GrantDetailVO grantDetailVO = null;
            grantDetailVO = vo.getGrantDetails();
            if(grantDetailVO == null){
              grantDetailVO = new GrantDetailVO();
            }
            grantDetailVO = grantDetailVO.buildDocumentAttributes((List<GrantDocumentAttributes>) value);
            vo.setGrantDetails(grantDetailVO);
          } else {
            voPd.getWriteMethod().invoke(vo, value);
          }
        } catch (IllegalAccessException e) {
          logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }

    Collections.sort(vo.getGrantDetails().getSections());
    vo.setFlowAuthorities(workflowPermissionService
        .getGrantFlowPermissions(vo.grantorOrganization.getId(), user.getUserRoles(),vo.grantStatus.getId()));
    vo.setActionAuthorities(workflowPermissionService
        .getGrantActionPermissions(vo.getGrantorOrganization().getId(),
            user.getUserRoles(),vo.getGrantStatus().getId()));

    return vo;
  }


}
