package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.Disbursement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisbursementRepository extends CrudRepository<Disbursement,Long> {


    @Query(value = "select distinct A.* from disbursements A \n" +
            "inner join grants Z on Z.id=A.grant_id \n" +
            "inner join disbursement_assignments B on B.disbursement_id=A.id \n" +
            "inner join workflow_statuses C on C.id=A.status_id \n" +
            "where ( \n" +
            "\t(B.anchor=true and B.owner = ?1) or \n" +
            "\t(B.owner=?1 and B.state_id=A.status_id) or \n" +
            "\t(C.internal_status='DRAFT' and (select count(*) from disbursement_history where id=A.id)>0 and ?1 = any (array(select owner from disbursement_assignments where disbursement_id=A.id))) or \n" +
            "\t(C.internal_status='REVIEW' and ?1 = any( array(select owner from report_assignments where disbursement_id=A.id))) or \n" +
            "\t(C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) \n" +
            "\tand Z.grantor_org_id=?2 and Z.deleted=false order by A.requested_on desc",nativeQuery = true)
    List<Disbursement> getDisbursementsForUser(Long userId,Long orgId);
}
