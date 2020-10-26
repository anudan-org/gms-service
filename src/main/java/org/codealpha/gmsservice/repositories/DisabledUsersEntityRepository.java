package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.DisabledUsersEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DisabledUsersEntityRepository extends CrudRepository<DisabledUsersEntity,Long> {

    @Query(value = "select distinct C.id,'Report' entity_type,concat(concat(C.name,' for Grant '),F.name) entity_name from report_assignments A \n" +
            "inner join users B on B.id=A.assignment\n" +
            "inner join reports C on C.id=A.report_id\n" +
            "inner join grants F on F.id=C.grant_id\n" +
            "inner join workflow_statuses D on D.id=C.status_id\n" +
            "inner join organizations E on E.id=B.organization_id\n" +
            "where B.deleted=true and D.internal_status!='CLOSED' and E.organization_type!='GRANTEE'",nativeQuery = true)
    List<DisabledUsersEntity> getReports();

    @Query(value = "select distinct C.id, 'Disbursement Approval Request for ' entity_type,F.name entity_name from disbursement_assignments A \n" +
            "inner join users B on B.id=A.owner\n" +
            "inner join disbursements C on C.id=A.disbursement_id\n" +
            "inner join grants F on F.id=C.grant_id\n" +
            "inner join workflow_statuses D on D.id=C.status_id\n" +
            "inner join organizations E on E.id=B.organization_id\n" +
            "where B.deleted=true and D.internal_status!='CLOSED' and E.organization_type!='GRANTEE'",nativeQuery = true)
    List<DisabledUsersEntity> getDisbursements();

    @Query(value = "select distinct C.id, 'Grant' entity_type, C.name entity_name from grant_assignments A \n" +
            "inner join users B on B.id=A.assignments\n" +
            "inner join grants C on C.id=A.grant_id\n" +
            "inner join workflow_statuses D on D.id=C.grant_status_id\n" +
            "inner join organizations E on E.id=B.organization_id\n" +
            "where B.deleted=true and D.internal_status!='CLOSED' and E.organization_type!='GRANTEE'",nativeQuery = true)
    List<DisabledUsersEntity> getGrants();
}
