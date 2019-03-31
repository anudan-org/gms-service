package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.Organization;

/**
 * @author Developer <developer@enstratify.com>
 **/
public interface OrganizationService {

	Organization get(Long organizationId);
	public Organization fingOrganizationByCode(String code);

}
