package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GrantSection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrantSectionRepository extends CrudRepository<GrantSection, Long> {

  @Query(value = "select * from grant_sections", nativeQuery = true)
  public List<GrantSection> getAllDefaultSections();
}
