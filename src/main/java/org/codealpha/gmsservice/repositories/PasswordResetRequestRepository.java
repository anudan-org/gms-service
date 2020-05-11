package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.PasswordResetRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetRequestRepository extends CrudRepository<PasswordResetRequest,Long> {

    @Query(value = "select * from password_reset_request where user_id=?1 and key=?2 and org_id=?3 and validated=false order by requested_on desc limit 1",nativeQuery = true)
    public PasswordResetRequest findByUnvalidatedUserIdAndKeyAndOrgId(Long userId,String key,Long orgId);
}
