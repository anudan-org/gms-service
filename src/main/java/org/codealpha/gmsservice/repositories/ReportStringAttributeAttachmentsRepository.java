package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.ReportStringAttribute;
import org.codealpha.gmsservice.entities.ReportStringAttributeAttachments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReportStringAttributeAttachmentsRepository extends CrudRepository<ReportStringAttributeAttachments,Long> {

    public List<ReportStringAttributeAttachments> findByReportStringAttribute(ReportStringAttribute reportStringAttribute);
}
