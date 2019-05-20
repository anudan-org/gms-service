package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.springframework.data.repository.CrudRepository;

public interface GrantQualitativeDataRepository extends CrudRepository<GrantQualitativeKpiData,Long> {

}
