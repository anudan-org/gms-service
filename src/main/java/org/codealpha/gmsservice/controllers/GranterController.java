package org.codealpha.gmsservice.controllers;

import java.time.LocalDateTime;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Rfp;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.codealpha.gmsservice.repositories.RfpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping(value = "/granter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GranterController {

	@Autowired
	private GranterRepository granterRepository;

	@Autowired
	private RfpRepository rfpRepository;

	@PostMapping(value = "/")
	public void create(@RequestBody Granter granter) {

		System.out.println("Create Granter");
		granter.setCreatedAt(LocalDateTime.now());
		granter.setCreatedBy("Admin");
		granter = granterRepository.save(granter);

		System.out.println(granterRepository.findById(granter.getId()));
	}

	@PostMapping("/{granterId}/rfps/")
	public Rfp createRfp(@PathVariable(name = "granterId") Long organizationId,
			@RequestBody Rfp rfp) {

		rfp.setGranter(granterRepository.findById(organizationId).get());
		rfp.setCreatedAt(LocalDateTime.now());
		rfp.setCreatedBy("Admin");
		return rfpRepository.save(rfp);

	}

}
