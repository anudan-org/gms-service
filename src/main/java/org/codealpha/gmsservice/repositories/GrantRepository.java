package org.codealpha.gmsservice.repositories;

import java.util.Date;
import java.util.List;

import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.services.OrganizationService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.ColumnResult;
import javax.persistence.EntityResult;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

/**
 * @author Developer code-alpha.org
 **/
public interface GrantRepository extends CrudRepository<Grant, Long> {

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join organizations B on A.grantor_org_id = B.id inner join workflows w on B.id = w.granter_id inner join workflow_statuses ws on w.id = ws.workflow_id inner join workflow_state_permissions wsp on ws.id = wsp.workflow_status_id where B.id =?2 and A.organization_id =?1 and wsp.role_id in (?3) and A.grant_status_id = ws.id and A.deleted=false", nativeQuery = true)
    public List<Grant> findGrantsOfGranteeForTenantOrg(Long granteeOrgId, Long grantorOrgId, List<Long> roleIds);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A where A.grantor_org_id=?1 and A.deleted=false", nativeQuery = true)
    public List<Grant> findGrantsOfGranter(Long grantorOrgId);

    @Query(value = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) order by A.updated_at desc", nativeQuery = true)
    public List<Grant> findAssignedGrantsOfGranter(Long grantorOrgId, Long userId);

    @Query(value = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and ?2 = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and ?2 = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) or ((select count(*) from grant_history where id=A.id)>0) ) order by A.updated_at desc", nativeQuery = true)
    public List<Grant> findGrantsForAdmin(Long granterId, Long userId);

    @Query(value="select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (B.anchor=true and B.assignments=?2) or (B.assignments=?2 and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 ) or (C.internal_status='REVIEW') ) order by A.updated_at desc",nativeQuery = true)
    public List<Grant> findInProgressGrantsForAdmin(Long granterId,Long userId);

    @Query(value="select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (C.internal_status='ACTIVE') ) order by A.updated_at desc\n",nativeQuery = true)
    public List<Grant> findActiveGrantsForAdmin(Long granterId);

    @Query(value="select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=?1 and A.deleted=false and ( (C.internal_status='CLOSED') ) order by A.updated_at desc",nativeQuery = true)
    public List<Grant> findClosedGrantsForAdmin(Long granterId);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join workflow_statuses B on B.id=A.grant_status_id where A.organization_id=?1 and A.deleted=false and (B.internal_status='ACTIVE' or B.internal_status='CLOSED')", nativeQuery = true)
    public List<Grant> findAllGrantsOfGrantee(Long granteeOrgId);

    @Query(value = "select a.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants where a.name=?1 and grantor_org_id=?2",nativeQuery = true)
    public Grant findByNameAndGrantorOrganization(String name, Long granterId);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join workflow_statuses B on B.id=A.grant_status_id where B.internal_status='ACTIVE' and A.grantor_org_id=?1 and A.deleted=false", nativeQuery = true)
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

    @Query(value = "select g.*,(select assignments from grant_assignments where grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants g inner join workflow_statuses w on w.id=g.grant_status_id\n"
            + "inner join grant_assignments ga on ga.grant_id=g.id and ga.state_id=w.id\n"
            + "where ga.assignments=?1 and w.internal_status=?2 and g.deleted=false", nativeQuery = true)
    List<Grant> findGrantsOwnedByUserByStatus(Long userId, String status);

    @Query(value = "select A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join workflow_statuses B on B.id=A.grant_status_id where ( (B.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id) >0   ) or B.internal_status!='DRAFT') and A.id=?1", nativeQuery = true)
    List<Grant> findGrantsThatMovedAtleastOnce(Long grantId);

    @Query(value = "select distinct C.*,(select assignments from grant_assignments where grant_id=C.id and state_id=C.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grant_assignments A inner join users B on B.id=A.assignments inner join grants C on C.id=A.grant_id inner join workflow_statuses D on D.id=C.grant_status_id inner join organizations E on E.id=B.organization_id where B.deleted=true and D.internal_status!='CLOSED' and E.organization_type!='GRANTEE'",nativeQuery = true)
    List<Grant> getGrantsWithDisabledUsers();

    @Query(value = "select a.*,(select assignments from grant_assignments where grant_id=a.id and state_id=a.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants a where grantor_org_id=?1",nativeQuery = true)
    List<Grant> getAllGrantsForGranter(Long granterId);

    @Query(value = "select g.*,(select assignments from grant_assignments where grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants g inner join workflow_statuses w on w.id=g.grant_status_id where g.grantor_org_id=?1 and w.internal_status=?2 and g.deleted=false",nativeQuery = true)
    List<Grant> findGrantsByStatus(Long granterId,String status);

    @Query(value = "select g.*,(select assignments \n" +
            "\t\t\tfrom grant_assignments \n" +
            "\t\t\twhere grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count \n" +
            "\t\t\tfrom grants g inner join workflow_statuses w on w.id=g.grant_status_id \n" +
            "\t\t\tinner join grant_assignments ga on ga.state_id=g.grant_status_id and ga.grant_id=g.id\n" +
            "\t\t\twhere ga.assignments=?1 and w.internal_status=?2 and g.deleted=false",nativeQuery = true)
    List<Grant> findGrantsByStatusForUser(Long userId,String status);

    @Query(value = "select g.*,(select assignments from grant_assignments where grant_id=g.id and state_id=g.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants g where g.id=?1",nativeQuery = true)
    Grant getById(Long id);

    @Query(value = "select * from grants where orig_grant_id=?1",nativeQuery = true)
    Grant getByOrigGrantId(Long grantId);

    @Query(value="select count(*) from grants a\n" +
            "inner join grant_assignments b on b.state_id=a.grant_status_id and b.grant_id=a.id\n" +
            "inner join workflow_statuses c on c.id=grant_status_id\n" +
            "where b.assignments=?1 and \n" +
            "(c.internal_status!='ACTIVE' and c.internal_status!='CLOSED') and\n" +
            "a.deleted=false",nativeQuery = true)
    Long getActionDueGrantsForUser(Long userId);

    @Query(value = "select count(distinct(a.id)) from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id where b.assignments=?1 and c.internal_status='DRAFT' and a.deleted=false",nativeQuery = true)
    Long getUpComingDraftGrants(Long userId);

    @Query(value = "select count(*) from (select distinct a.id,c.internal_status\n" +
            "                        from grants a \n" +
            "                        inner join grant_assignments b on b.grant_id=a.id\n" +
            "                        inner join workflow_statuses c on c.id=a.grant_status_id \n" +
            "                        where (\n" +
            "\t\t\t\t\t\t(b.assignments=?1 and c.internal_status='DRAFT' and b.state_id=a.grant_status_id) or\n" +
            "                        (b.assignments=?1 and c.internal_status!='DRAFT') or\n" +
            "                        (c.internal_status='DRAFT' and (select count(*) from grant_history where id=a.id)>0) \n" +
            "                        )\n" +
            "                        and a.deleted=false) X where X.internal_status not in ('ACTIVE','CLOSED')",nativeQuery = true)
    Long getGrantsInWorkflow(Long userId);

    @Query(value = "select sum(amount) from (select distinct b.assignments,a.amount,c.internal_status  \n" +
            "                        from grants a \n" +
            "                        inner join grant_assignments b on b.grant_id=a.id \n" +
            "                        inner join workflow_statuses c on c.id=a.grant_status_id \n" +
            "                        where b.assignments=?1 and (\n" +
            "                        (b.assignments=?1 and c.internal_status='DRAFT' and b.state_id=a.grant_status_id) or\n" +
            "                        (b.assignments=?1 and c.internal_status!='DRAFT') or\n" +
            "                        (c.internal_status='DRAFT' and (select count(*) from grant_history where id=a.id)>0) )\n" +
            "                        and a.deleted=false) X where X.internal_status not in ('CLOSED','ACTIVE') group by X.assignments",nativeQuery = true)
    Long getUpcomingGrantsDisbursementAmount(Long userId);

    @Query(value = "select count(distinct(a.id)) from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id where b.assignments=?1 and (c.internal_status=?2) and a.deleted=false",nativeQuery = true)
    Long getGrantsTotalForUserByStatus(Long userId,String status);

    @Query(value = "select sum(a.amount) from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id where b.assignments=?1 and (c.internal_status=?2) and a.deleted=false group by b.assignments",nativeQuery = true)
    Long getCommittedAmountByUserAndStatus(Long userId, String status);

    @Query(value = "select sum(f.actual_amount) from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id inner join disbursements d on d.grant_id=a.id inner join workflow_statuses e on e.id=d.status_id inner join actual_disbursements f on f.disbursement_id=d.id where b.assignments=?1 and (c.internal_status=?2) and a.deleted=false and e.internal_status='CLOSED' group by b.assignments",nativeQuery = true)
    Long getDisbursedAmountByUserAndStatus(Long userId, String status);

    @Query(value = "select count(distinct(a.organization_id)) from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id where b.assignments=?1 and (c.internal_status=?2) and a.deleted=false",nativeQuery = true)
    Long getGranteeOrgsCountByUserAndStatus(Long userId, String status);

    @Query(value = "select count(distinct(a.id)) from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id inner join reports d on d.grant_id=a.id inner join workflow_statuses e on e.id=d.status_id where b.assignments=?1 and (c.internal_status=?2) and e.internal_status !='CLOSED' and a.deleted=false",nativeQuery = true)
    Long getGrantsWithNoApprovedReportsByUserAndStatus(Long userId, String status);

    @Query(value = "select distinct a.id from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id where b.assignments=?1 and (c.internal_status=?2) and a.id not in ( select distinct a.id from grants a inner join grant_assignments b on b.grant_id=a.id and b.state_id=a.grant_status_id inner join workflow_statuses c on c.id=a.grant_status_id inner join grant_string_attributes d on d.grant_id=a.id inner join grant_specific_section_attributes e on e.id=d.section_attribute_id where b.assignments=?1 and (c.internal_status=?2) and e.field_type='kpi' and a.deleted=false) and a.deleted=false",nativeQuery = true)
    Long getGrantsWithNoAKPIsByUserAndStatus(Long userId, String status);

    @Query(value = "select exists (select distinct(a.id) from grants a\n" +
            "inner join grant_assignments b on b.grant_id=a.id\n" +
            "where b.assignments=?1 and a.deleted=false\n" +
            "union\n" +
            "select distinct(c.id) from grants a\n" +
            "inner join reports c on c.grant_id=a.id\n" +
            "inner join report_assignments b on b.report_id=c.id\n" +
            "where b.assignment=?1 and a.deleted=false and c.deleted=false\n" +
            "union\n" +
            "select distinct(c.id) from grants a\n" +
            "inner join disbursements c on c.grant_id=a.id\n" +
            "inner join disbursement_assignments b on b.disbursement_id=c.id\n" +
            "where b.owner=?1 and a.deleted=false)\n",nativeQuery = true)
    boolean isUserPartOfActiveWorkflow(Long userid);
}
