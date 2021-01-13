package org.codealpha.gmsservice.controllers;

import java.time.LocalDateTime;
import java.util.Date;
import org.codealpha.gmsservice.entities.Grantee;
import org.codealpha.gmsservice.repositories.GranteeRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Developer code-alpha.org
 **/
@RestController
@RequestMapping(value = "/grantee", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiIgnore
public class GranteeController {

	@Autowired
	private GranteeRepository repository;

	@PostMapping(value = "/")
	public void create(@RequestBody Grantee grantee) {

		grantee.setCreatedAt(DateTime.now().toDate());
		grantee.setCreatedBy("Admin");
		repository.save(grantee);

		System.out.println(repository.findAll());
	}

}
