package org.codealpha.gmsservice.services;

import io.swagger.annotations.OAuth2Definition;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    @Autowired private ReportRepository reportRepository;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private WorkflowStatusRepository workflowStatusRepository;
    @Autowired private ReportAssignmentRepository reportAssignmentRepository;
    @Autowired private GrantAssignmentRepository grantAssignmentRepository;


    public Report saveReport(Report report){
        return reportRepository.save(report);
    }

    public List<Report> getAllReports(){
        return (List<Report>) reportRepository.findAll();
    }

    public List<ReportAssignment> saveAssignments(Report report, String tenantCode,Long userId) {
        ReportAssignment assignment = null;

        Organization granterOrg = organizationRepository.findByCode(tenantCode);
        List<WorkflowStatus> statuses = workflowStatusRepository.getAllTenantStatuses("REPORT", report.getGrant().getGrantorOrganization().getId());

        GrantAssignments anchorAssignment = grantAssignmentRepository.findByGrantIdAndAnchor(report.getGrant().getId(),true);
        List<ReportAssignment> assignments = new ArrayList<>();
        for (WorkflowStatus status : statuses) {
            if (!status.getTerminal()) {
                assignment = new ReportAssignment();
                if (status.isInitial()) {
                    assignment.setAnchor(true);
                    assignment.setAssignment(anchorAssignment.getAssignments());
                } else {
                    assignment.setAnchor(false);
                }
                assignment.setReportId(report.getId());
                assignment.setStateId(status.getId());
                assignment = _saveAssignmentForReport(assignment);
                assignments.add(assignment);
            }
        }
        return assignments;
    }

    private ReportAssignment _saveAssignmentForReport(ReportAssignment assignment) {
        return reportAssignmentRepository.save(assignment);
    }

    public List<Report> getAllAssignedReportsForUser(Long userId, Long granterOrgId){
        return reportRepository.findAllAssignedReportsForUser(userId,granterOrgId);
    }

}
