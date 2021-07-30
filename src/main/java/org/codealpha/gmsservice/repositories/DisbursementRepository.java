package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Disbursement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisbursementRepository extends CrudRepository<Disbursement, Long> {

    @Query(value = "select distinct A.* from disbursements A \n" + "inner join grants Z on Z.id=A.grant_id \n"
            + "inner join disbursement_assignments B on B.disbursement_id=A.id \n"
            + "inner join workflow_statuses C on C.id=A.status_id \n" + "where ( \n"
            + "\t(B.anchor=true and B.owner = ?1) or \n" + "\t(B.owner=?1 and B.state_id=A.status_id) or \n"
            + "\t(C.internal_status='DRAFT' and (select count(*) from disbursement_history where id=A.id)>0 and ?1 = any (array(select owner from disbursement_assignments where disbursement_id=A.id))) or \n"
            + "\t(C.internal_status='REVIEW' and ?1 = any( array(select owner from disbursement_assignments where disbursement_id=A.id)))  \n"
            + "\t) \n"
            + "\tand Z.grantor_org_id=?2 and C.internal_status!='ACTIVE' and C.internal_status!='CLOSED' and Z.deleted=false and A.grantee_entry=false order by A.updated_at desc", nativeQuery = true)
    List<Disbursement> getInprogressDisbursementsForUser(Long userId, Long orgId);

    @Query(value = "select distinct A.* from disbursements A inner join grants Z on Z.id=A.grant_id inner join disbursement_assignments B on B.disbursement_id=A.id inner join workflow_statuses C on C.id=A.status_id where ( (B.anchor=true and B.owner = ?1) or (B.owner=?1 and B.state_id=A.status_id) or (C.internal_status='DRAFT' and (select count(*) from disbursement_history where id=A.id)>0 ) or (C.internal_status='REVIEW' ) ) and Z.grantor_org_id=?2 and C.internal_status!='ACTIVE' and C.internal_status!='CLOSED' and Z.deleted=false and A.grantee_entry=false order by A.updated_at desc", nativeQuery = true)
    List<Disbursement> getInprogressDisbursementsForAdminUser(Long userId, Long orgId);

    @Query(value = "select distinct A.* from disbursements A \n" + "inner join grants Z on Z.id=A.grant_id \n"
            + "inner join disbursement_assignments B on B.disbursement_id=A.id \n"
            + "inner join workflow_statuses C on C.id=A.status_id \n" + "where ( \n"
            + "\t(C.internal_status='ACTIVE' ) ) \n"
            + "\tand Z.grantor_org_id=?1 and Z.deleted=false and A.grantee_entry=false order by A.moved_on desc", nativeQuery = true)
    List<Disbursement> getActiveDisbursementsForUser(Long orgId);

    @Query(value = "select distinct A.* from disbursements A \n" + "inner join grants Z on Z.id=A.grant_id \n"
            + "inner join disbursement_assignments B on B.disbursement_id=A.id \n"
            + "inner join workflow_statuses C on C.id=A.status_id \n" + "where ( \n"
            + "\t(C.internal_status='CLOSED' ) ) \n"
            + "\tand Z.grantor_org_id=?1 and Z.deleted=false  and A.grantee_entry=false order by A.moved_on desc", nativeQuery = true)
    List<Disbursement> getClosedDisbursementsForUser(Long orgId);

    @Query(value = "select * from disbursements where grant_id=?1 and status_id in (?2)", nativeQuery = true)
    public List<Disbursement> getDisbursementByGrantAndStatuses(Long grantId, List<Long> statusIds);

    @Query(value = "select A.* from disbursements A inner join workflow_statuses B on B.id=A.status_id where ( (B.internal_status='DRAFT' and (select count(*) from disbursement_history where id=A.id) >0   ) or B.internal_status!='DRAFT') and A.id=?1", nativeQuery = true)
    List<Disbursement> findDisbursementsThatMovedAtleastOnce(Long disbursementId);

    @Query(value = "select * from disbursements where grant_id=?1", nativeQuery = true)
    List<Disbursement> getAllDisbursementsForGrant(Long grantId);

    @Query(value = "select count(distinct(a.id)) from disbursements a\n" +
            "            inner join disbursement_assignments b on b.disbursement_id=a.id and b.state_id=a.status_id\n" +
            "\t\t\tinner join grants c on c.id=a.grant_id\n" +
            "            where b.owner=?1 and c.deleted=false group by b.owner", nativeQuery = true)
    Long getPendingActionDisbursements(Long userId);

    @Query(value = "select distinct a.* from disbursements a\n" +
            "inner join disbursement_assignments b on b.disbursement_id=a.id and b.state_id=a.status_id\n" +
            "inner join grants c on c.id=a.grant_id\n" +
            "where b.owner=?1 and c.deleted=false ", nativeQuery = true)
    List<Disbursement> getDetailedPendingActionDisbursements(Long userId);

    @Query(value = "select count(distinct(a.id)) from disbursements a \n" +
            "            inner join disbursement_assignments b on b.disbursement_id=a.id and b.state_id=a.status_id \n" +
            "            inner join workflow_statuses c on c.id=a.status_id \n" +
            "            inner join grants d on d.id=a.grant_id\n" +
            "            where b.owner=?1 and c.internal_status='DRAFT' and d.deleted=false", nativeQuery = true)
    Long getUpComingDraftDisbursements(Long userId);

    @Query(value = "select distinct a.* from disbursements a \n" +
            "inner join disbursement_assignments b on b.disbursement_id=a.id and b.state_id=a.status_id \n" +
            "inner join workflow_statuses c on c.id=a.status_id \n" +
            "inner join grants d on d.id=a.grant_id\n" +
            "where b.owner=?1 and c.internal_status='DRAFT' and d.deleted=false", nativeQuery = true)
    List<Disbursement> getDetailedUpComingDraftDisbursements(Long userId);

    @Query(value = "select count(distinct(a.id)) \n" +
            "            from disbursements a \n" +
            "            inner join disbursement_assignments b on b.disbursement_id=a.id \n" +
            "            inner join workflow_statuses c on c.id=a.status_id \n" +
            "            inner join grants d on d.id=a.grant_id\n" +
            "            where b.owner=?1 and (\n" +
            "            (c.internal_status='DRAFT' and (select count(*) from disbursement_history where id=a.id)>0 and ?1 = any (array(select owner from disbursement_assignments where disbursement_id=a.id))) or\n" +
            "            (C.internal_status!='CLOSED' and (select count(*) from disbursement_history where id=a.id)>0 and ?1 = any( array(select owner from disbursement_assignments where disbursement_id=A.id)))\n" +
            "            ) and d.deleted=false", nativeQuery = true)
    Long getDisbursementsInWorkflow(Long userId);

    @Query(value = "select sum(requested_amount) from (\n" +
            "            select distinct d.id,b.owner,a.requested_amount\n" +
            "                                   from disbursements a \n" +
            "                                  inner join disbursement_assignments b on b.disbursement_id=a.id \n" +
            "                                  inner join workflow_statuses c on c.id=a.status_id \n" +
            "                                  inner join grants d on d.id=a.grant_id\n" +
            "                                  where b.owner=?1 and ( (b.owner=?1 and b.state_id=a.status_id) or\n" +
            "            (c.internal_status='DRAFT' and (select count(*) from disbursement_history where id=a.id)>0) or\n" +
            "            c.internal_status!='CLOSED' and (select count(*) from disbursement_history where id=a.id)>0) and d.deleted=false) X group by X.owner",nativeQuery = true)
    Long getUpcomingDisbursementsDisbursementAmount(Long userId);

}
