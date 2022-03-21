package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantType;
import org.codealpha.gmsservice.repositories.GrantTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantTypeService {

    @Autowired
    private GrantTypeRepository grantTypeRepository;

    public List<GrantType> findGrantTypesForTenant(Long granterId){
        List<GrantType> grantTypes = grantTypeRepository.findGrantTypesForTenant(granterId);
        return grantTypes;
    }

    public GrantType save(GrantType gt) {

        return grantTypeRepository.save(gt);
    }

    public GrantType findById(Long id){
        return grantTypeRepository.findById(id).orElse(null);
    }
}
