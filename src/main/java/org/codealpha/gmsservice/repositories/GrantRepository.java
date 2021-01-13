package org.codealpha.gmsservice.repositories;

import java.util.Date;
import java.util.List;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Developer code-alpha.org
 **/
public interface GrantRepository extends CrudRepository<Grant, Long> {

    @Query(value = "select A.* from grants A inner join organizations B on A.grantor_org_id = B.id inner join workflows w on B.id = w.granter_id inner join workflow_statuses ws on w.id = ws.workflow_id inner join workflow_state_permissions wsp on ws.id = wsp.workflow_status_id where B.id =?2 and A.organization_id =?1 and wsp.role_id in (?3) and A.grant_status_id = ws.id and A.deleted=false", nativeQuery = true)
    public List<Grant> findGrantsOfGranteeForTenantOrg(Long granteeOrgId, Long grantorOrgId, List<Long> roleIds);

    @Query(value = "select A.* from grants A where A.grantor_org_id=?1 and A.deleted=false", nativeQuery = true)
    public List<Grant> findGrantsOfGranter(Long grantorOrgId);

    @Query(value = "select distinct A.* from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) order by A.updated_at desc", nativeQuery = true)
    public List<Grant> findAssignedGrantsOfGranter(Long grantorOrgId, Long userId);

    @Query(value = "select distinct A.* from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) or ((select count(*) from grant_history where id=A.id)>0) ) order by A.updated_at desc", nativeQuery = true)
    public List<Grant> findGrantsForAdmin(Long granterId, Long userId);

    @Query(value = "select A.* from grants A inner join workflow_statuses B on B.id=A.grant_status_id where A.organization_id=?1 and A.deleted=false and (B.internal_status='ACTIVE' or B.internal_status='CLOSED')", nativeQuery = true)
    public List<Grant> findAllGrantsOfGrantee(Long granteeOrgId);

    public Grant findByNameAndGrantorOrganization(String name, Granter granter);

    @Query(value = "select A.* from grants A inner join workflow_statuses B on B.id=A.grant_status_id where B.internal_status='ACTIVE' and A.grantor_org_id=?1 and A.deleted=false", nativeQuery = true)
    List<Grant> findActiveGrants(Long organizationId);

    @Query(value = "select count(distinct A.*) from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2 and C.internal_status not in('ACTIVE','CLOSED')) or (B.assignments=?2 and B.state_id=A.grant_status_id and C.internal_status not in('ACTIVE','CLOSED')) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))))", nativeQuery = true)
    Long countOfInprogressGrantsForGrantor(Long grantorOrgId, Long userId);

    @Query(value = "select count(*) from grants A inner join workflow_statuses B on B.id=A.grant_status_id where A.organization_id=?1 and B.internal_status='ACTIVE' and A.deleted=false", nativeQuery = true)
    Long countOfActiveGrantsForGrantee(Long granteeOrgId);

    @Query(value = "select count(distinct A.*) from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and C.internal_status='ACTIVE' and A.deleted=false", nativeQuery = true)
    Long countOfActiveGrantsForGrantor(Long userOrgId);

    @Query(value = "select count(*) from grants A inner join workflow_statuses B on B.id=A.grant_status_id where A.organization_id=?1 and B.internal_status='CLOSED' and A.deleted=false", nativeQuery = true)
    Long countOfClosedGrantsForGrantee(Long granteeOrgId);

    @Query(value = "select count(distinct A.*) from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and C.internal_status='CLOSED' and A.deleted=false", nativeQuery = true)
    Long countOfClosedGrantsForGrantor(Long userOrgId);

    @Query(value = "select count(*) from grants where start_date=?1 and grant_status_id=?3 and id !=?2 and deleted=false", nativeQuery = true)
    public Long getCountOfOtherGrantsWithStartDateAndStatus(Date startDate, Long grantId, Long statusId);

    @Query(value = "select * from grants g inner join workflow_statuses w on w.id=g.grant_status_id\n"
            + "inner join grant_assignments ga on ga.grant_id=g.id and ga.state_id=w.id\n"
            + "where ga.assignments=?1 and w.internal_status=?2 and g.deleted=false", nativeQuery = true)
    List<Grant> findGrantsOwnedByUserByStatus(Long userId, String status);

    @Query(value = "select A.* from grants A inner join workflow_statuses B on B.id=A.grant_status_id where ( (B.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id) >0   ) or B.internal_status!='DRAFT') and A.id=?1", nativeQuery = true)
    List<Grant> findGrantsThatMovedAtleastOnce(Long grantId);

    @Query(value = "select distinct C.* from grant_assignments A inner join users B on B.id=A.assignments inner join grants C on C.id=A.grant_id inner join workflow_statuses D on D.id=C.grant_status_id inner join organizations E on E.id=B.organization_id where B.deleted=true and D.internal_status!='CLOSED' and E.organization_type!='GRANTEE'",nativeQuery = true)
    List<Grant> getGrantsWithDisabledUsers();
}
