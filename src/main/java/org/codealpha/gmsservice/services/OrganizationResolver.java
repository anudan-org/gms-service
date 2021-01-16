package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Developer code-alpha.org
 **/
@Service
public class OrganizationResolver {

	@Autowired
	private GranterRepository granterRepository;

	public Organization getOrganizationByHostedDomain(String domain){
		return granterRepository.findByHostUrl(domain);
	}

}
