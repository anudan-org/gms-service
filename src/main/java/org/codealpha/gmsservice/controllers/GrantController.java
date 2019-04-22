package org.codealpha.gmsservice.controllers;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.constants.GrantSubStatus;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.KpiSubmission;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.KpiSubmissionData;
import org.codealpha.gmsservice.services.AppConfigService;
import org.codealpha.gmsservice.services.GrantQualitativeDataService;
import org.codealpha.gmsservice.services.GrantQuantitativeDataService;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.KpiSubmissionService;
import org.codealpha.gmsservice.services.UserService;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.codealpha.gmsservice.services.WorkflowStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grant")
public class GrantController {

  @Autowired
  private GrantQuantitativeDataService quantitativeDataService;
  @Autowired
  GrantQualitativeDataService qualitativeDataService;
  @Autowired
  private WorkflowPermissionService workflowPermissionService;
  @Autowired
  private AppConfigService appConfigService;
  @Autowired
  private UserService userService;
  @Autowired
  private WorkflowStatusService workflowStatusService;
  @Autowired
  private GrantService grantService;
  @Autowired
  private KpiSubmissionService kpiSubmissionService;

  @PutMapping("/kpi")
  @Transactional
  public GrantVO saveKpiSubmissions(@RequestBody List<KpiSubmissionData> submissionData,
      @RequestHeader("USER-ID") Long userId) {
    for (KpiSubmissionData data : submissionData) {
      switch (data.getType()) {
        case "QUANTITATIVE":
          GrantQuantitativeKpiData quantitativeKpiData = quantitativeDataService
              .findById(data.getKpiDataId());
          quantitativeKpiData.setActuals(Integer.valueOf(data.getValue()));
          //quantitativeKpiData.setStatus(workflowStatusService.findById(data.getToStatusId()));
          //quantitativeKpiData.setStatusName(workflowStatusService.findById(data.getToStatusId()).getName());
          quantitativeDataService.saveData(quantitativeKpiData);
          break;
        case "QUALITATIVE":
          GrantQualitativeKpiData qualitativeKpiData = qualitativeDataService
              .findById(data.getKpiDataId());
          qualitativeKpiData.setActuals(data.getValue());
          //qualitativeKpiData.setStatus(workflowStatusService.findById(data.getToStatusId()));
          //qualitativeKpiData.setStatusName(workflowStatusService.findById(data.getToStatusId()).getName());
          qualitativeDataService.saveData(qualitativeKpiData);
          break;
      }
    }
    GrantQuantitativeKpiData quantitativeKpiData = quantitativeDataService
        .findById(submissionData.get(0).getKpiDataId());
    KpiSubmission kpiSubmission = kpiSubmissionService.findById(quantitativeKpiData.getKpiSubmission().getId());
    kpiSubmission.setSubmittedOn(new Date());
    kpiSubmission.setSubmissionStatus(workflowStatusService.findById(submissionData.get(0).getToStatusId()));
    kpiSubmission.setStatusName(workflowStatusService.findById(submissionData.get(0).getToStatusId()).getName());

    kpiSubmission = kpiSubmissionService.saveKpiSubmission(kpiSubmission);

    Grant grant = kpiSubmission.getGrantKpi().getGrant();
    grant.setSubstatus(GrantSubStatus.KPI_SUBMITTED);
    grant = grantService.saveGrant(grant);
    GrantVO grantVO = new GrantVO()
        .build(grant, workflowPermissionService, userService.getUserById(userId),
            appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
    return grantVO;
  }
}
