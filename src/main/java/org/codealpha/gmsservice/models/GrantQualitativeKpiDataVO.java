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
import org.codealpha.gmsservice.entities.KpiSubmission;
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

  public KpiSubmission getKpiSubmission() {
    return kpiSubmission;
  }

  public void setKpiSubmission(KpiSubmission kpiSubmission) {
    this.kpiSubmission = kpiSubmission;
  }

  public GrantQualitativeKpiDataVO build(GrantQualitativeKpiData grantQualitativeKpiData,
      WorkflowPermissionService workflowPermissionService, User user, AppConfig submissionWindow) {
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

    return vo;
  }
}
