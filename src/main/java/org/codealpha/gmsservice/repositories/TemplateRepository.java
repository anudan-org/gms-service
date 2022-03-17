package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.Template;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TemplateRepository extends CrudRepository<Template, Long> {

  List<Template> findByKpi(GrantKpi kpi);

}
