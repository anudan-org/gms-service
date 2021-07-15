package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterGrantSummaryDisbursed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterGrantSummaryDisbursedRepository extends CrudRepository<GranterGrantSummaryDisbursed,Long> {

    @Query(value="SELECT d.id,a.id as grant_id,a.grantor_org_id AS granter_id,a.start_date,a.amount as grant_amount, b.internal_status, d.value AS disbursement_data FROM grants a JOIN workflow_statuses b ON b.id = a.grant_status_id JOIN reports c ON c.grant_id = a.id JOIN workflow_statuses f ON f.id = c.status_id JOIN report_string_attributes d ON d.report_id = c.id JOIN report_specific_section_attributes e ON e.id = d.section_attribute_id WHERE a.grantor_org_id =?1 and a.deleted=false and e.field_type = 'disbursement' AND f.internal_status='CLOSED' AND b.internal_status = ?2 order by a.start_date asc",nativeQuery = true)
    public List<GranterGrantSummaryDisbursed> getGrantDisbursedSummaryForGranter(Long granterId, String status);

    @Query(value="select a.id,a.id as grant_id, a.grantor_org_id AS granter_id,a.start_date, a.amount as grant_amount,b.internal_status,'' as disbursement_data FROM grants a INNER JOIN workflow_statuses b ON b.id = a.grant_status_id where a.grantor_org_id =?1 and a.deleted=false and b.internal_status = 'ACTIVE' order by a.start_date asc",nativeQuery = true)
    public List<GranterGrantSummaryDisbursed> getActiveGrantCommittedSummaryForGranter(Long granterId);

    @Query(value="select a.id,a.id as grant_id, a.grantor_org_id AS granter_id,a.start_date, a.amount as grant_amount,b.internal_status,'' as disbursement_data FROM grants a INNER JOIN workflow_statuses b ON b.id = a.grant_status_id where a.grantor_org_id =?1 and a.deleted=false and b.internal_status = 'CLOSED' order by a.start_date asc",nativeQuery = true)
    public List<GranterGrantSummaryDisbursed> getClosedGrantCommittedSummaryForGranter(Long granterId);

    @Query(value="select a.id,a.id as grant_id, \n" +
            "a.grantor_org_id AS granter_id,\n" +
            "a.start_date, a.amount as grant_amount,\n" +
            "b.internal_status,'' as disbursement_data \n" +
            "FROM grants a \n" +
            "INNER JOIN workflow_statuses b ON b.id = a.grant_status_id \n" +
            "inner join grant_assignments c on c.state_id=a.grant_status_id and c.grant_id=a.id \n" +
            "where c.assignments =?1 and a.deleted=false and b.internal_status = 'ACTIVE' order by a.start_date asc",nativeQuery = true)
    public List<GranterGrantSummaryDisbursed> getActiveGrantCommittedSummaryForUser(Long userId);

    @Query(value="select a.id,a.id as grant_id, a.grantor_org_id AS granter_id,a.start_date, a.amount as grant_amount,b.internal_status,'' as disbursement_data \n" +
            "FROM grants a \n" +
            "INNER JOIN workflow_statuses b ON b.id = a.grant_status_id \n" +
            "inner join grant_assignments c on c.state_id=a.grant_status_id and c.grant_id=a.id\n" +
            "where c.assignments =?1 and a.deleted=false and b.internal_status = 'CLOSED' order by a.start_date asc",nativeQuery = true)
    public List<GranterGrantSummaryDisbursed> getClosedGrantCommittedSummaryForUser(Long userId);

}
