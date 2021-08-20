package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * @author Developer code-alpha.org
 **/
public interface GrantCardRepository extends CrudRepository<GrantCard, Long> {

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join organizations B on A.grantor_org_id = B.id inner join workflows w on B.id = w.granter_id inner join workflow_statuses ws on w.id = ws.workflow_id inner join workflow_state_permissions wsp on ws.id = wsp.workflow_status_id where B.id =?2 and A.organization_id =?1 and wsp.role_id in (?3) and A.grant_status_id = ws.id and A.deleted=false", nativeQuery = true)
    public List<GrantCard> findGrantsOfGranteeForTenantOrg(Long granteeOrgId, Long grantorOrgId, List<Long> roleIds);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A where A.grantor_org_id=?1 and A.deleted=false", nativeQuery = true)
    public List<GrantCard> findGrantsOfGranter(Long grantorOrgId);

    @Query(value = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) order by A.updated_at desc", nativeQuery = true)
    public List<GrantCard> findAssignedGrantsOfGranter(Long grantorOrgId, Long userId);

    @Query(value = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) or ((select count(*) from grant_history where id=A.id)>0) ) order by A.updated_at desc", nativeQuery = true)
    public List<GrantCard> findGrantsForAdmin(Long granterId, Long userId);

    @Query(value="select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 ) or (C.internal_status='REVIEW') ) order by A.updated_at desc",nativeQuery = true)
    public List<GrantCard> findInProgressGrantsForAdmin(Long granterId,Long userId);

    @Query(value="select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (C.internal_status='ACTIVE') ) order by A.updated_at desc\n",nativeQuery = true)
    public List<GrantCard> findActiveGrantsForAdmin(Long granterId);

    @Query(value="select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (C.internal_status='CLOSED') ) order by A.updated_at desc",nativeQuery = true)
    public List<GrantCard> findClosedGrantsForAdmin(Long granterId);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,approved_reports_for_grant(A.id) approved_reports_for_grant, disbursed_amount_for_grant(A.id) approved_disbursements_total, project_documents_for_grant(A.id) project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join workflow_statuses B on B.id=A.grant_status_id where A.organization_id=?1 and A.deleted=false and (B.internal_status='ACTIVE' or B.internal_status='CLOSED')", nativeQuery = true)
    public List<GrantCard> findAllGrantsOfGrantee(Long granteeOrgId);

    @Query(value = "select a.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants where a.name=?1 and grantor_org_id=?2",nativeQuery = true)
    public GrantCard findByNameAndGrantorOrganization(String name, Long granterId);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join workflow_statuses B on B.id=A.grant_status_id where B.internal_status='ACTIVE' and A.grantor_org_id=?1 and A.deleted=false", nativeQuery = true)
    List<GrantCard> findActiveGrants(Long organizationId);

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

    @Query(value = "select g.*,(select assignments from grant_assignments where grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants g inner join workflow_statuses w on w.id=g.grant_status_id\n"
            + "inner join grant_assignments ga on ga.grant_id=g.id and ga.state_id=w.id\n"
            + "where ga.assignments=?1 and w.internal_status=?2 and g.deleted=false", nativeQuery = true)
    List<GrantCard> findGrantsOwnedByUserByStatus(Long userId, String status);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants A inner join workflow_statuses B on B.id=A.grant_status_id where ( (B.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id) >0   ) or B.internal_status!='DRAFT') and A.id=?1", nativeQuery = true)
    List<GrantCard> findGrantsThatMovedAtleastOnce(Long grantId);

    @Query(value = "select distinct C.*,(select assignments from grant_assignments where grant_id=C.id and state_id=C.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grant_assignments A inner join users B on B.id=A.assignments inner join grants C on C.id=A.grant_id inner join workflow_statuses D on D.id=C.grant_status_id inner join organizations E on E.id=B.organization_id where B.deleted=true and D.internal_status!='CLOSED' and E.organization_type!='GRANTEE'",nativeQuery = true)
    List<GrantCard> getGrantsWithDisabledUsers();

    @Query(value = "select a.*,(select assignments from grant_assignments where grant_id=a.id and state_id=a.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants a where grantor_org_id=?1",nativeQuery = true)
    List<GrantCard> getAllGrantsForGranter(Long granterId);

    @Query(value = "select g.*,(select assignments from grant_assignments where grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants g inner join workflow_statuses w on w.id=g.grant_status_id where g.grantor_org_id=?1 and w.internal_status=?2 and g.deleted=false",nativeQuery = true)
    List<GrantCard> findGrantsByStatus(Long granterId,String status);

    @Query(value = "select g.*,(select assignments from grant_assignments where grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count,get_owner_grant(A.id) owner_id,get_owner_grant_name(A.id) owner_name from grants g where g.id=?1",nativeQuery = true)
    GrantCard getById(Long id);

    @Query(value="select distinct a.*,(select assignments from grant_assignments where grant_id=a.id and state_id=a.grant_status_id) current_assignment,approved_reports_for_grant(a.id) approved_reports_for_grant, disbursed_amount_for_grant(a.id) approved_disbursements_total, project_documents_for_grant(a.id) project_documents_count,get_owner_grant(a.id) owner_id,get_owner_grant_name(a.id) owner_name from grants a\n" +
            "inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "inner join workflow_statuses c on c.id=grant_status_id\n" +
            "where b.assignments=?1 and \n" +
            "(c.internal_status!='ACTIVE' and c.internal_status!='CLOSED') and\n" +
            "a.deleted=false",nativeQuery = true)
    List<GrantCard> getDetailedActionDueGrantsForUser(Long userId);

    @Query(value = "select distinct a.*,(select assignments from grant_assignments where grant_id=a.id and state_id=a.grant_status_id) current_assignment,approved_reports_for_grant(a.id) approved_reports_for_grant, disbursed_amount_for_grant(a.id) approved_disbursements_total, project_documents_for_grant(a.id) project_documents_count,get_owner_grant(a.id) owner_id,get_owner_grant_name(a.id) owner_name\n" +
            "from grants a \n" +
            "inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id \n" +
            "inner join workflow_statuses c on c.id=a.grant_status_id \n" +
            "where b.assignments=?1\n" +
            "and c.internal_status='DRAFT' \n" +
            "and a.deleted=false",nativeQuery = true)
    List<GrantCard> getDetailedUpComingDraftGrants(Long userId);
}
