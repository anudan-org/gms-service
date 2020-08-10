package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GranterGrantTemplate;
import org.codealpha.gmsservice.repositories.GranterGrantTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GranterGrantTemplateService {

    @Autowired
    private GranterGrantTemplateRepository granterGrantTemplateRepository;

    public List<GranterGrantTemplate> findByGranterId(Long granterId) {
        return granterGrantTemplateRepository.findByGranterId(granterId);
    }

    public List<GranterGrantTemplate> findByGranterIdAndPublishedStatus(Long granterId, boolean published) {
        return granterGrantTemplateRepository.findByGranterIdAndPublished(granterId, published);
    }

    public List<GranterGrantTemplate> findByGranterIdAndPublishedStatusAndPrivateStatus(Long granterId,
            boolean published, boolean _private) {
        return granterGrantTemplateRepository.findByGranterIdAndPublishedAndPrivateToGrant(granterId, published,
                _private);
    }

    public GranterGrantTemplate findByTemplateId(Long templateId) {
        if (granterGrantTemplateRepository.findById(templateId).isPresent()) {
            return granterGrantTemplateRepository.findById(templateId).get();
        }
        return null;
    }

    public GranterGrantTemplate saveGrantTemplate(GranterGrantTemplate template) {
        return granterGrantTemplateRepository.save(template);
    }
}
