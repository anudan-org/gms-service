package org.codealpha.gmsservice.repositories;

import java.util.List;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer code-alpha.org
 **/
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  public List<User> findByEmailId(String email);
  public User findByEmailIdAndOrganization(String email, Organization org);
 @Query(value = "select * from users where email_id=?1 and organization_id=?2 order by id desc limit 1",nativeQuery = true)
  public User findByEmailAndOrg(String email, Long orgId);

  @Query(value = "select distinct users.* from workflow_status_transitions A inner join roles r on A.role_id = r.id inner join user_roles u on r.id = u.role_id inner join users users on users.id=u.user_id where A.from_state_id=?1",nativeQuery = true)
  public List<User> usersToNotifyOnWorkflowSateChangeTo(Long toStateId);

  public List<User> findByOrganization(Organization org);

  public List<User> findByOrganizationAndActive(Organization org, boolean active);
}
