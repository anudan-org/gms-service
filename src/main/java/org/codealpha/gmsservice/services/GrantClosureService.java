package org.codealpha.gmsservice.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.KpiType;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GrantClosureService {
    @Autowired
    private ClosureAssignmentRepository closureAssignmentRepository;
    @Autowired
    private GranterClosureTemplateRepository granterClosureTemplateRepository;
    @Autowired
    private ClosureSpecificSectionRepository closureSpecificSectionRepository;
    @Autowired
    private ClosureSpecificSectionAttributeRepository closureSpecificSectionAttributeRepository;
    @Autowired
    private ClosureStringAttributeRepository closureStringAttributeRepository;


    public List<GranterClosureTemplate> findTemplatesAndPublishedStatusAndPrivateStatus(Long grantId, boolean isPublished, boolean isPrivate) {
        return granterClosureTemplateRepository.findByGranterIdAndPublishedAndPrivateToClosure(grantId, isPublished,
                isPrivate);
    }

    public ClosureAssignments saveAssignmentForClosure(ClosureAssignments assignment) {
        return closureAssignmentRepository.save(assignment);
    }

    public GranterClosureTemplate findByTemplateId(Long templateId) {
        return granterClosureTemplateRepository.findById(templateId).get();
    }

    public ClosureSpecificSection saveClosureSpecificSection(ClosureSpecificSection specificSection) {
        return closureSpecificSectionRepository.save(specificSection);
    }

    public ClosureSpecificSectionAttribute saveClosureSpecificSectionAttribute(ClosureSpecificSectionAttribute sectionAttribute) {
        return closureSpecificSectionAttributeRepository.save(sectionAttribute);
    }

    public ClosureStringAttribute saveClosureStringAttribute(ClosureStringAttribute stringAttribute) {
        return closureStringAttributeRepository.save(stringAttribute);
    }

    public List<ClosureSpecificSection> getClosureSections(Grant grant) {
        return closureSpecificSectionRepository.findByGranterAndGrantId((Granter) grant.getGrantorOrganization(),
                grant.getId());
    }

    public List<ClosureSpecificSectionAttribute> getAttributesBySection(ClosureSpecificSection closureSection) {
        return closureSpecificSectionAttributeRepository.findBySection(closureSection);
    }
}
