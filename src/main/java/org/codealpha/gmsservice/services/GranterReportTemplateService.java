package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GranterReportTemplate;
import org.codealpha.gmsservice.entities.GranterReportSection;
import org.codealpha.gmsservice.repositories.GranterReportSectionRepository;
import org.codealpha.gmsservice.repositories.GranterReportTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GranterReportTemplateService {
    @Autowired
    private GranterReportTemplateRepository granterReportTemplateRepository;
    @Autowired
    private GranterReportSectionRepository granterReportSectionRepository;

    public GranterReportTemplate saveReportTemplate(GranterReportTemplate template) {
        return granterReportTemplateRepository.save(template);
    }

    public void markAllAsNotDefault() {
        granterReportTemplateRepository.findAll().forEach(t -> {
            t.setDefaultTemplate(false);
            granterReportTemplateRepository.save(t);
        });
    }

    public GranterReportTemplate findByTemplateId(Long templateId) {
        if (granterReportTemplateRepository.findById(templateId).isPresent()) {
            GranterReportTemplate template = granterReportTemplateRepository.findById(templateId).orElse(null);
            if (template == null) {
                return null;
            }
            List<GranterReportSection> sections = granterReportSectionRepository.findByReportTemplate(template);
            if (sections != null) {
                sections.sort((a, b) -> Comparator.nullsLast(Integer::compareTo)
                        .compare(a.getSectionOrder(), b.getSectionOrder()));
                sections.forEach(section -> {
                    if (section.getAttributes() != null) {
                        section.getAttributes().size();
                    }
                });
            }

            GranterReportTemplate responseTemplate = new GranterReportTemplate();
            responseTemplate.setId(template.getId());
            responseTemplate.setName(template.getName());
            responseTemplate.setDescription(template.getDescription());
            responseTemplate.setPublished(template.isPublished());
            responseTemplate.setPrivateToReport(template.isPrivateToReport());
            responseTemplate.setGranterId(template.getGranterId());
            responseTemplate.setDefaultTemplate(template.getDefaultTemplate());
            responseTemplate.setSections(sections);
            return responseTemplate;
        }
        return null;
    }

    public List<GranterReportTemplate> findByGranterIdAndPublishedStatusAndPrivateStatus(Long granterId,
            boolean published, boolean isPrivate) {
        return granterReportTemplateRepository.findByGranterIdAndPublishedAndPrivateToReport(granterId, published,
                isPrivate);
    }
}
