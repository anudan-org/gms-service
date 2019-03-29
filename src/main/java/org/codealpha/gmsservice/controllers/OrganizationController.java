package org.codealpha.gmsservice.controllers;

import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Rfp;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.repositories.RfpRepository;
import org.codealpha.gmsservice.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@Autowired
	private OrganizationRepository repository;



	@GetMapping("/{organizationId}")
	public Organization get(@NotNull @PathVariable("organizationId") Long organizationId) {
		return service.get(organizationId);
	}

	@GetMapping("/")
	public void getAll() {
		System.out.println(repository.findAll());
	}




}
