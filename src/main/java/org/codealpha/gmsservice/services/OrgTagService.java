package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.OrgTag;
import org.codealpha.gmsservice.repositories.OrgTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgTagService {

    @Autowired
    private OrgTagRepository orgTagRepository;

    public OrgTag createTag(OrgTag tag){
        return orgTagRepository.save(tag);
    }

    public List<OrgTag> getOrgTags(Long orgId){
        return orgTagRepository.getOrgTags(orgId);
    }
}
