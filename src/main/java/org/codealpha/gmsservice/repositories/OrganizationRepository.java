package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Developer code-alpha.org
 **/
@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long> {

  public Organization findByCode(String code);

  public Organization findByOrganizationTypeEquals(String type);

  @Query("select o from Organization o where o.organizationType = 'GRANTEE'")
  public List<Organization> getGranteeOrgs();

  @Query("select o from Organization o where o.organizationType = 'GRANTER'")
  public List<Organization> getGranterOrgs();

  public Organization findByNameAndOrganizationType(String name, String type);

  @Query("select distinct g.organization from Grant g where g.grantorOrganization.id = ?1 order by g.organization.name")
  public List<Organization> getAssociatedGranteesForTenant(Long granterId);

  Organization findByName(String grantee);
}
