package org.codealpha.gmsservice.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.codealpha.gmsservice.GmsServiceApplication;
import org.codealpha.gmsservice.entities.Organization;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GmsServiceApplication.class)
class OrganizationRepositoryTest {

	@Autowired
	private OrganizationRepository repository;

	@Test
	public void repositoryShouldCreateANewInstance() {

		Organization organization = repository
				.save(new Organization());

		Optional<Organization> foundOrganization = repository
				.findById(organization.getId());

		assertTrue(foundOrganization.isPresent());
		assertNotNull(foundOrganization.get());
		assertEquals(foundOrganization.get().getId(), organization.getId());
	}

	@Test
	public void repositoryShouldUpdateAnExisitngInstance() {

		Organization organization = repository
				.save(new Organization());

		Optional<Organization> foundOrganization = repository
				.findById(organization.getId());

		assertTrue(foundOrganization.isPresent());
		LocalDateTime now = LocalDateTime.now();

		foundOrganization.get().setUpdatedAt(now);
		repository.save(foundOrganization.get());

		foundOrganization = repository
				.findById(organization.getId());
		assertEquals(foundOrganization.get().getUpdatedAt(), now);

	}
}