package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.BaseEntity;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

public class GrantQualitativeKpiDataVO extends BaseEntity {

  private Long id;
  private String goal;
  private String actuals;
  private GrantKpi grantKpi;
  private String statusName;
  private WorkflowStatus status;
  private Date submitByDate;
  private Date submittedOnDate;
  private List<WorkFlowPermission> flowAuthority;
  private WorkflowActionPermission actionAuthority;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public String getActuals() {
    return actuals;
  }

  public void setActuals(String actuals) {
    this.actuals = actuals;
  }

  public GrantKpi getGrantKpi() {
    return grantKpi;
  }

  public void setGrantKpi(GrantKpi grantKpi) {
    this.grantKpi = grantKpi;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

  public WorkflowStatus getStatus() {
    return status;
  }

  public void setStatus(WorkflowStatus status) {
    this.status = status;
  }

  public Date getSubmitByDate() {
    return submitByDate;
  }

  public void setSubmitByDate(Date submitByDate) {
    this.submitByDate = submitByDate;
  }

  public Date getSubmittedOnDate() {
    return submittedOnDate;
  }

  public void setSubmittedOnDate(Date submittedOnDate) {
    this.submittedOnDate = submittedOnDate;
  }

  public List<WorkFlowPermission> getFlowAuthority() {
    return flowAuthority;
  }

  public void setFlowAuthority(
      List<WorkFlowPermission> flowAuthority) {
    this.flowAuthority = flowAuthority;
  }

  public WorkflowActionPermission getActionAuthority() {
    return actionAuthority;
  }

  public void setActionAuthority(
      WorkflowActionPermission actionAuthority) {
    this.actionAuthority = actionAuthority;
  }

  public GrantQualitativeKpiDataVO build(GrantQualitativeKpiData grantQualitativeKpiData, WorkflowPermissionService workflowPermissionService, User user, AppConfig submissionWindow) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils
        .getPropertyDescriptors(grantQualitativeKpiData.getClass());
    GrantQualitativeKpiDataVO vo = new GrantQualitativeKpiDataVO();
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grantQualitativeKpiData);
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

    DateTime today = DateTime.now();
    if (today.isBefore(
        new DateTime(grantQualitativeKpiData.getSubmitByDate()).withTimeAtStartOfDay()
            .plusDays(1)) && today.isAfter(new DateTime(grantQualitativeKpiData.getSubmitByDate())
        .minusDays(Integer.valueOf(submissionWindow.getConfigValue()))) && !workflowPermissionService.getKPIFlowPermissions(
        grantQualitativeKpiData.getGrantKpi().getGrant().getGrantorOrganization().getId(),
        user.getRole().getId()).isEmpty()) {
      vo.setFlowAuthority(workflowPermissionService.getKPIFlowPermissions(
          grantQualitativeKpiData.getGrantKpi().getGrant().getGrantorOrganization().getId(),
          user.getRole().getId()));
    }

    return vo;
  }
}
