package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GranterClosureTemplate;
import org.codealpha.gmsservice.entities.GranterReportTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterClosureTemplateRepository extends CrudRepository<GranterClosureTemplate, Long> {
    public GranterClosureTemplate findByGranterIdAndDefaultTemplate(Long granterId, Boolean flag);

    public List<GranterClosureTemplate> findByGranterId(Long granterId);

    public List<GranterClosureTemplate> findByGranterIdAndPublished(Long granterId, Boolean publishedStatus);

    public List<GranterClosureTemplate> findByGranterIdAndPublishedAndPrivateToClosure(Long granterId,
            Boolean publishedStatus, boolean _private);

    public List<GranterClosureTemplate> findByGranterIdAndPublishedAndPrivateToClosure(Long granterId, boolean published,
            boolean _private);
}
