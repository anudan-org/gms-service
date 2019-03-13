package org.codealpha.gmsservice.controllers;

import javax.validation.constraints.NotNull;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping(value = "/organizations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrganizationController {

	@Autowired
	private OrganizationService service;

	@GetMapping("/{organizationId}")
	public Organization get(@NotNull @PathVariable("organizationId") Long organizationId) {
		return service.get(organizationId);
	}

}
