package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Release;
import org.codealpha.gmsservice.repositories.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReleaseService {

    @Autowired
    private ReleaseRepository releaseRepository;

    public void deleteAllEntries(){
        releaseRepository.deleteAll();
    }

    public Release saveRelease(Release release){
        return releaseRepository.save(release);
    }

    public Release getCurrentRelease(){
        return releaseRepository.findAll().iterator().next();
    }
}
