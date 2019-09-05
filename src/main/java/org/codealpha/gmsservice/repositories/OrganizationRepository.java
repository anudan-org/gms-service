package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.Organization;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long> {

  public Organization findByCode(String code);

  public Organization findByOrganizationTypeEquals(String type);

  @Query(value = "select * from organizations where organization_type='GRANTEE'",nativeQuery = true)
  public List<Organization> getGranteeOrgs();

  @Query(value = "select * from organizations where organization_type='GRANTER'",nativeQuery = true)
  public List<Organization> getGranterOrgs();

  public Organization findByNameAndOrganizationType(String name, String type);


}
