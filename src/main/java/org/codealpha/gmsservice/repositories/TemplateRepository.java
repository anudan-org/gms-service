package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.GrantKpi;
import org.codealpha.gmsservice.entities.Template;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<Template, Long> {

  List<Template> findByKpi(GrantKpi kpi);

}
