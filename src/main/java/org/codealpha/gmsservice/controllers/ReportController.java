package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.Api;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Report;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/report")
@Api(value = "Reports",description = "API end points for Reports",tags = {"Grants"})
public class ReportController {

    @Autowired private ReportService reportService;
    @Autowired private OrganizationService organizationService;

    @GetMapping("/")
    public List<Report> getAllReports(@PathVariable("userId")Long userId, @RequestHeader("X-TENANT-CODE") String tenantCode){
        Organization tenantOrg = organizationService.findOrganizationByTenantCode(tenantCode);
        return reportService.getAllAssignedReportsForUser(userId,tenantOrg.getId());
    }
}
