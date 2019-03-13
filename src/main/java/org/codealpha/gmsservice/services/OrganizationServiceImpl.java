package org.codealpha.gmsservice.services;

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
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private OrganizationRepository repository;

	@Override
	public Organization get(Long organizationId) {
		Optional<Organization> optionalOrganization = repository.findById(organizationId);

		if (optionalOrganization.isPresent()) {
			return optionalOrganization.get();
		}
		//TODO - Replace with specific exception
		throw new ResourceNotFoundException("Organization with id [" + organizationId + "] not found.");
	}
}
