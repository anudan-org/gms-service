package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterGrantSummaryDisbursed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterGrantSummaryDisbursedRepository extends CrudRepository<GranterGrantSummaryDisbursed,Long> {

    @Query(value="SELECT row_number() OVER () as id,a.grantor_org_id AS granter_id,a.start_date,a.amount as grant_amount, b.internal_status, d.value AS disbursement_data FROM grants a JOIN workflow_statuses b ON b.id = a.grant_status_id JOIN reports c ON c.grant_id = a.id JOIN workflow_statuses f ON f.id = c.status_id JOIN report_string_attributes d ON d.report_id = c.id JOIN report_specific_section_attributes e ON e.id = d.section_attribute_id WHERE a.grantor_org_id =?1 and e.field_type = 'disbursement' AND f.internal_status='CLOSED' AND b.internal_status = ?2 order by a.start_date asc",nativeQuery = true)
    public List<GranterGrantSummaryDisbursed> getGrantDisbursedSummaryForGranter(Long granterId, String status);
}
