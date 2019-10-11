package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantSectionAttribute;
import org.codealpha.gmsservice.repositories.GrantSectionAttributeRepository;
import org.codealpha.gmsservice.repositories.GranterGrantSectionAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantSectionAttributeService {
    @Autowired
    private GrantSectionAttributeRepository grantSectionAttributeRepository;

    public List<GrantSectionAttribute> getAllDefaultSectionAttributes(){
        return grantSectionAttributeRepository.getAllDefaultSectionAttributes();
    }

    public List<GrantSectionAttribute> getAllDefaultSectionAttributesForSection(Long sectionId){
        return grantSectionAttributeRepository.getAllDefaultSectionAttributesForSection(sectionId);
    }

    public GrantSectionAttribute saveGrantSectionAttribute(GrantSectionAttribute sectionAttribute){
        return grantSectionAttributeRepository.save(sectionAttribute);
    }
}
