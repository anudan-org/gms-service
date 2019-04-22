package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.BaseEntity;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.KpiSubmission;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.entities.WorkFlowPermission;
import org.codealpha.gmsservice.entities.WorkflowActionPermission;
import org.codealpha.gmsservice.entities.WorkflowStatus;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.springframework.beans.BeanUtils;

public class GrantQuantitativeKpiDataVO extends BaseEntity {

  private Long id;
  private Integer goal;
  private Integer actuals;
  @JsonIgnore
  private KpiSubmission kpiSubmission;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public Integer getGoal() {
    return goal;
  }

  public void setGoal(Integer goal) {
    this.goal = goal;
  }

  public Integer getActuals() {
    return actuals;
  }

  public void setActuals(Integer actuals) {
    this.actuals = actuals;
  }

  public KpiSubmission getKpiSubmission() {
    return kpiSubmission;
  }

  public void setKpiSubmission(KpiSubmission kpiSubmission) {
    this.kpiSubmission = kpiSubmission;
  }

  public GrantQuantitativeKpiDataVO build(GrantQuantitativeKpiData grantQuantitativeKpiData,
      WorkflowPermissionService workflowPermissionService,
      User user, AppConfig submissionWindow) {

    PropertyDescriptor[] propertyDescriptors = BeanUtils
        .getPropertyDescriptors(grantQuantitativeKpiData.getClass());
    GrantQuantitativeKpiDataVO vo = new GrantQuantitativeKpiDataVO();
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grantQuantitativeKpiData);
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
