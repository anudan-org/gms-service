package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportSpecificSection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportSpecificSectionRepository extends CrudRepository<ReportSpecificSection,Long> {
    List<ReportSpecificSection> findByReportId(Long id);

    @Query(value = "select max(section_order)+1 from report_specific_sections where granter_id=?1 and report_template_id=?2",nativeQuery = true)
    public int getNextSectionOrder(Long granterId, Long templateId);
}
