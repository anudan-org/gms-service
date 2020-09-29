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
}
