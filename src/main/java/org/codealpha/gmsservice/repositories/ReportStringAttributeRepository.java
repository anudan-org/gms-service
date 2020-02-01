package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.entities.ReportSpecificSection;
import org.codealpha.gmsservice.entities.ReportSpecificSectionAttribute;
import org.codealpha.gmsservice.entities.ReportStringAttribute;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportStringAttributeRepository extends CrudRepository<ReportStringAttribute,Long> {

    public List<ReportStringAttribute> findByReport(Report report);
    public ReportStringAttribute findBySectionAttributeAndSection(ReportSpecificSectionAttribute sectionAttribute, ReportSpecificSection section);

    List<ReportStringAttribute> findBySectionAttribute(ReportSpecificSectionAttribute attrib);
}
