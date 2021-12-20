package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.HygieneCheck;
import org.codealpha.gmsservice.repositories.HygieneCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HygieneCheckService {

    @Autowired
    private HygieneCheckRepository repository;

    public List<HygieneCheck> getChecks(){
        return repository.getHygieneChecks();
    }
}
