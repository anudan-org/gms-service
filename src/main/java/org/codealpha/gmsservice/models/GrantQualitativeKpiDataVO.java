package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.BaseEntity;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class GrantQualitativeKpiDataVO extends BaseEntity {

  private static Logger logger = LoggerFactory.getLogger(GrantQualitativeKpiDataVO.class);

  private Long id;
  private String goal;
  private String actuals;

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


  public GrantQualitativeKpiDataVO build(GrantQualitativeKpiData grantQualitativeKpiData) {
    PropertyDescriptor[] propertyDescriptors = BeanUtils
            .getPropertyDescriptors(grantQualitativeKpiData.getClass());
    GrantQualitativeKpiDataVO vo = new GrantQualitativeKpiDataVO();
    for (PropertyDescriptor descriptor : propertyDescriptors) {
      if (!descriptor.getName().equalsIgnoreCase("class")) {
        try {
          Object value = descriptor.getReadMethod().invoke(grantQualitativeKpiData);
          PropertyDescriptor voPd = BeanUtils
                  .getPropertyDescriptor(vo.getClass(), descriptor.getName());
          if(voPd!=null) {
            voPd.getWriteMethod().invoke(vo, value);
          }

        } catch (IllegalAccessException | InvocationTargetException e) {
          logger.error(e.getMessage(), e);
        }
      }
    }

    return vo;
  }
}
