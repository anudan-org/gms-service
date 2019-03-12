package org.codealpha.gmsservice.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.codealpha.gmsservice.GmsServiceApplication;
import org.codealpha.gmsservice.entities.Grant;
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
class GrantRepositoryTest {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private GrantRepository grantRepository;

	@Test
	public void shouldCreateAGrantInAnOrganization() {

		Organization org = new Organization();
		org = organizationRepository.save(org);

		Grant grant = new Grant();
		grant.setOrganization(org);

		grantRepository.save(grant);

		Optional<Organization> savedOrg = organizationRepository.findById(org.getId());

		assertTrue(savedOrg.isPresent());
		assertNotNull(savedOrg.get());
		assertNotNull(savedOrg.get().getGrants());
		assertEquals(savedOrg.get().getGrants().size(), 1);


	}

}