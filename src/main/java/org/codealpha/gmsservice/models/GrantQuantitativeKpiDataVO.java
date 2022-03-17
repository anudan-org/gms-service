package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.AppConfig;
import org.codealpha.gmsservice.entities.BaseEntity;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class GrantQuantitativeKpiDataVO extends BaseEntity {

  private static Logger logger = LoggerFactory.getLogger(GrantQuantitativeKpiDataVO.class);

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
          logger.error(e.getMessage(),e);
        } catch (InvocationTargetException e) {
          logger.error(e.getMessage(),e);
        }
      }
    }
    return vo;
  }
}
