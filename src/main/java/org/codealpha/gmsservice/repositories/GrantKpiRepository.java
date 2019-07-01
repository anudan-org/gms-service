package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.springframework.data.repository.CrudRepository;

public interface GrantKpiRepository extends CrudRepository<GrantKpi, Long> {


  public GrantKpi findByTitleAndKpiTypeAndGrant(String title, KpiType type, Grant grant);

}
