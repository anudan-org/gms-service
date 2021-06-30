package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.GrantType;
import org.codealpha.gmsservice.repositories.GrantTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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
        return grantTypeRepository.findById(id).get();
    }
}
