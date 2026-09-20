package org.codealpha.gmsservice.repositories;

import java.util.Date;

public interface ClosureSummaryRow {
    Long getClosureId();
    java.util.Date getUpdatedAt();
    Long getOwnerId();
    String getOwnerName();
    Long getStatusId();
    String getStatusName();
    String getStatusInternalStatus();
    Long getReasonId();
    String getReasonText();
    Boolean getDeleted();

    Long getGrantId();
    String getGrantReferenceNo();
    String getGrantName();
    Double getGrantAmount();
    Date getGrantStartDate();
    Date getGrantEndDate();
    Long getGrantTypeId();
    Boolean getClosureInProgress();
    Long getAmendGrantId();
    Long getOrigGrantId();
    Double getApprovedDisbursementsTotal();
    Integer getApprovedReportsForGrant();
    Integer getProjectDocumentsCount();
    Double getPlannedFundOthers();
    Double getActualFundOthers();

    Long getGrantStatusId();
    String getGrantStatusName();
    String getGrantStatusInternalStatus();

    Long getOrganizationId();
    String getOrganizationName();
    String getOrganizationCode();

    Long getGrantorOrgId();
    String getGrantorOrgName();
    String getGrantorOrgCode();
}
