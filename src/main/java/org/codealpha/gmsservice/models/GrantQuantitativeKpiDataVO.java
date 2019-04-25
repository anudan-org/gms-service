package org.codealpha.gmsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.BaseEntity;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.springframework.beans.BeanUtils;

public class GrantQuantitativeKpiDataVO extends BaseEntity {

  private Long id;
  private Integer goal;
  private Integer actuals;

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
