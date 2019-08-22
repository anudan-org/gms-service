package org.codealpha.gmsservice.services;

import java.util.List;
import java.util.Optional;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;

	public Organization get(Long organizationId) {
		Optional<Organization> optionalOrganization = repository.findById(organizationId);

		if (optionalOrganization.isPresent()) {
			return optionalOrganization.get();
		}
		//TODO - Replace with specific exception
		throw new ResourceNotFoundException("Organization with id [" + organizationId + "] not found.");
	}

	public Organization findOrganizationByTenantCode(String code){
		return repository.findByCode(code);
	}

	public Organization getPlatformOrg(){
		return repository.findByOrganizationTypeEquals("PLATFORM");
	}


	public Organization save(Organization organization){
		return repository.save(organization);
	}


	public List<Organization> getGranteeOrgs(){
		return repository.getGranteeOrgs();
	}

	public List<Organization> getGranterOrgs(){
		return repository.getGranterOrgs();
	}


	public Organization findByNameAndOrganizationType(String name, String type){
		return repository.findByNameAndOrganizationType(name, type);
	}
}
