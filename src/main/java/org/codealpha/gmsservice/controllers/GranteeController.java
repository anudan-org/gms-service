package org.codealpha.gmsservice.controllers;

import java.time.LocalDateTime;
import org.codealpha.gmsservice.entities.Grantee;
import org.codealpha.gmsservice.repositories.GranteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping(value = "/grantee", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GranteeController {

	@Autowired
	private GranteeRepository repository;

	@PostMapping(value = "/")
	public void create(@RequestBody Grantee grantee) {

		grantee.setCreatedAt(LocalDateTime.now());
		grantee.setCreatedBy("Admin");
		repository.save(grantee);

		System.out.println(repository.findAll());
	}

}
