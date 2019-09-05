package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.TemplateLibrary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TemplateLibraryRepository extends CrudRepository<TemplateLibrary, Long> {

    public List<TemplateLibrary> findByGranterId(Long granterId);
}
