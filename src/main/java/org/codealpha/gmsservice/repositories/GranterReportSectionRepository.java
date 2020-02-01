package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GranterReportSection;
import org.codealpha.gmsservice.entities.GranterReportTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterReportSectionRepository extends CrudRepository<GranterReportSection,Long> {
    public List<GranterReportSection> findByReportTemplate(GranterReportTemplate template);
}
