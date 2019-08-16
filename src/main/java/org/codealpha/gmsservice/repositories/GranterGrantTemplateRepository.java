package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GranterGrantTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterGrantTemplateRepository extends CrudRepository<GranterGrantTemplate, Long> {

    public List<GranterGrantTemplate> findByGranterId(Long granterId);
}
