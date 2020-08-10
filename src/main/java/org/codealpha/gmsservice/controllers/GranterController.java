package org.codealpha.gmsservice.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.codealpha.gmsservice.repositories.RfpRepository;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Developer <developer@enstratify.com>
 **/
@RestController
@RequestMapping(value = "/granter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "Granters", description = "Granter API endpoints")
public class GranterController {

	@Autowired
	private GranterRepository granterRepository;

	@Autowired
	private GrantService grantService;

	@Autowired
	private WorkflowStatusService workflowStatusService;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private WorkflowStatePermissionService workflowStatePermissionService;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowStatusTransitionService workflowStatusTransitionService;

	@Autowired
	private GrantSectionService grantSectionService;

	@Autowired
	private GrantSectionAttributeService grantSectionAttributeService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private GranterService granterService;

	@Autowired
	private RolesPermissionService rolesPermissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private RfpRepository rfpRepository;

	@Autowired
	private GranterGrantTemplateService granterGrantTemplateService;

	@Autowired
	private GranterReportTemplateService granterReportTemplateService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private AppConfigService appConfigService;
	@Autowired
	private CommonEmailSevice commonEmailSevice;
	@Autowired
	private ReleaseService releaseService;

	@Value("${spring.upload-file-location}")
	private String uploadLocation;

	@PostMapping(value = "/")
	@ApiIgnore
	public void create(@RequestBody Granter granter) {

		System.out.println("Create Granter");
		granter.setCreatedAt(DateTime.now().toDate());
		granter.setCreatedBy("Admin");
		granter = granterRepository.save(granter);

	}

	@PostMapping("/{granterId}/rfps/")
	@ApiIgnore
	public Rfp createRfp(@PathVariable(name = "granterId") Long organizationId, @RequestBody Rfp rfp) {

		// rfp.setGranter(granterRepository.findById(organizationId).get());
		rfp.setCreatedAt(LocalDateTime.now());
		rfp.setCreatedBy("Admin");
		return rfpRepository.save(rfp);

	}

	@PostMapping(value = "/user/{userId}/onboard/{grantName}/slug/{tenantSlug}/granterUser/{granterUserEmail}", consumes = {
			"multipart/form-data" })
	@ApiOperation(value = "Onboard new granter with basic details", notes = "Currently harcoded users and roles for the newly created Granter is implemented. This feature will be enhanced in the future")
	public Organization onBoardGranter(
			@ApiParam(name = "granterName", value = "Name of new granter being onboarded") @PathVariable("grantName") String granterName,
			@ApiParam(name = "slug", value = "Name of granter slug. This will be used to create the Tenant Code as well us the subdomain") @PathVariable("tenantSlug") String slug,
			@ApiParam(name = "image", value = "Uploaded image file to be used as granter's logo") @RequestParam(value = "file") MultipartFile image,
			@ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
			@ApiParam(name = "userEmail", value = "Email Id of primary admin of Granter organization to whom email invite will be sent") @PathVariable("granterUserEmail") String userEmail) {

		Organization org = new Granter();
		org.setCode(slug.toUpperCase());
		org.setCreatedAt(DateTime.now().toDate());
		org.setCreatedBy(userService.getUserById(userId).getEmailId());
		org.setName(granterName);
		org.setOrganizationType("GRANTER");
		org = granterService.createGranter((Granter) org, image);

		try {
			String filePath = uploadLocation + slug.toUpperCase() + "/logo/";
			File dir = new File(filePath);
			dir.mkdirs();

			// File fileToCreate = new File(dir, "logo.png");
			// image.transferTo(fileToCreate);

			String fileName = image.getOriginalFilename();

			File fileToCreate = new File(dir, "logo.png");
			FileOutputStream fos = new FileOutputStream(fileToCreate);
			fos.write(image.getBytes());
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		User granterUser = new User();
		granterUser.setActive(false);
		granterUser.setCreatedAt(DateTime.now().toDate());
		granterUser.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUser.setFirstName("");
		granterUser.setLastName("");
		granterUser.setOrganization(org);
		granterUser.setEmailId(userEmail);
		// granterUser.setPassword("password");
		granterUser = userService.save(granterUser);

		Role role = new Role();
		role.setCreatedAt(DateTime.now().toDate());
		role.setCreatedBy(userService.getUserById(userId).getEmailId());
		role.setName("Admin");
		role.setOrganization(org);
		role.setInternal(true);
		role = roleService.saveRole(role);
		List<RolesPermission> rolesPermissions = new ArrayList<>();
		rolesPermissions.add(new RolesPermission(role, "Create Grant"));
		rolesPermissions.add(new RolesPermission(role, "Delete Grant"));
		rolesPermissions.add(new RolesPermission(role, "Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissions);

		UserRole userRole = new UserRole();
		userRole.setRole(role);
		userRole.setUser(granterUser);
		userRole = userRoleService.saveUserRole(userRole);

		organizationService.buildInviteUrlAndSendMail(userService, appConfigService, commonEmailSevice, releaseService,
				userService.getUserById(userId), org, granterUser, Arrays.asList(new UserRole[] { userRole }));

		GranterGrantTemplate defaulTemplate = new GranterGrantTemplate();
		defaulTemplate.setPublished(true);
		defaulTemplate.setName("Anudan Template");
		defaulTemplate.setGranterId(org.getId());
		defaulTemplate.setDescription("Default Anudan template.");
		defaulTemplate = grantService.saveGrantTemplate(defaulTemplate);
		int order = 1;
		for (GrantSection defaultSection : grantSectionService.getAllDefaultSections()) {
			GranterGrantSection section = new GranterGrantSection();
			section.setDeletable(true);
			section.setGranter(granterService.getGranterById(org.getId()));
			section.setGrantTemplate(defaulTemplate);
			section.setSectionName(defaultSection.getSectionName());
			section.setSectionOrder(order++);
			section = grantService.saveGrantTemaplteSection(section);

			int attributeOrder = 1;
			for (GrantSectionAttribute defaultAttribute : grantSectionAttributeService
					.getAllDefaultSectionAttributesForSection(defaultSection.getId())) {
				GranterGrantSectionAttribute attribute = new GranterGrantSectionAttribute();
				attribute.setAttributeOrder(attributeOrder++);
				attribute.setSection(section);
				attribute.setRequired(true);
				attribute.setFieldType(defaultAttribute.getFieldType());
				attribute.setFieldName(defaultAttribute.getFieldName());
				attribute.setGranter(granterService.getGranterById(org.getId()));
				attribute.setDeletable(true);
				grantService.saveGrantTemaplteSectionAttribute(attribute);
			}
		}

		/*
		 * Workflow workflow = new Workflow();
		 * workflow.setCreatedAt(DateTime.now().toDate());
		 * workflow.setCreatedBy(userService.getUserById(userId).getEmailId());
		 * workflow.setDescription("Default " + org.getName() + " Grant workflow");
		 * workflow.setGranter(granterService.getGranterById(org.getId()));
		 * workflow.setName("Default " + org.getName() + " Grant workflow");
		 * workflow.setObject(WorkflowObject.GRANT); workflow =
		 * workflowService.saveWorkflow(workflow);
		 */

		buildWorkflowsBasedOnSustainPlus(org);
		buildDefaultTemplates(org, organizationService.findOrganizationByTenantCode("SUSTAINPLUS"));

		return org;
	}

	private void buildDefaultTemplates(Organization org, Organization referenceOrg) {

		buildDefaultGrantTemplate(org, referenceOrg);
		buildDefaultReportTemplate(org, referenceOrg);
	}

	private void buildDefaultGrantTemplate(Organization org, Organization referenceOrg) {
		List<GranterGrantSection> defaultSections = granterGrantTemplateService
				.findByGranterIdAndPublishedStatusAndPrivateStatus(referenceOrg.getId(), true, false).stream()
				.filter(t -> t.isDefaultTemplate()).findFirst().get().getSections();
		GranterGrantTemplate defaultTemplate = new GranterGrantTemplate();
		defaultTemplate.setDefaultTemplate(true);
		defaultTemplate.setDescription("Anudan Default Grant Template");
		defaultTemplate.setGranterId(org.getId());
		defaultTemplate.setName("Anudan Default Grant Template");
		defaultTemplate.setPrivateToGrant(false);
		defaultTemplate.setPublished(true);
		defaultTemplate = granterGrantTemplateService.saveGrantTemplate(defaultTemplate);

		int sectionOrder = 1;
		for (GranterGrantSection section : defaultSections) {
			GranterGrantSection templateSection = new GranterGrantSection();
			templateSection.setDeletable(true);
			templateSection.setGrantTemplate(defaultTemplate);
			templateSection.setGranter((Granter) org);
			templateSection.setSectionName(section.getSectionName());
			templateSection.setSectionOrder(sectionOrder++);
			templateSection = grantService.saveGrantTemaplteSection(templateSection);

			if (section.getAttributes() != null && section.getAttributes().size() > 0) {
				int attributeOrder = 1;
				for (GranterGrantSectionAttribute attribute : section.getAttributes()) {
					GranterGrantSectionAttribute templateSectionAttribute = new GranterGrantSectionAttribute();
					templateSectionAttribute.setAttributeOrder(attributeOrder++);
					templateSectionAttribute.setDeletable(true);
					templateSectionAttribute.setFieldName(attribute.getFieldName());
					templateSectionAttribute.setFieldType(attribute.getFieldType());
					templateSectionAttribute.setGranter((Granter) org);
					templateSectionAttribute.setRequired(false);
					templateSectionAttribute.setSection(templateSection);
					templateSectionAttribute = grantService.saveGrantTemaplteSectionAttribute(templateSectionAttribute);
				}
			}
		}
	}

	private void buildDefaultReportTemplate(Organization org, Organization referenceOrg) {
		List<GranterReportSection> defaultSections = granterReportTemplateService
				.findByGranterIdAndPublishedStatusAndPrivateStatus(referenceOrg.getId(), true, false).stream()
				.filter(t -> t.getDefaultTemplate()).findFirst().get().getSections();
		GranterReportTemplate defaultTemplate = new GranterReportTemplate();
		defaultTemplate.setDefaultTemplate(true);
		defaultTemplate.setDescription("Anudan Default Report Template");
		defaultTemplate.setGranterId(org.getId());
		defaultTemplate.setName("Anudan Default Report Template");
		defaultTemplate.setPrivateToReport(false);
		defaultTemplate.setPublished(true);
		defaultTemplate = granterReportTemplateService.saveReportTemplate(defaultTemplate);

		int sectionOrder = 1;
		for (GranterReportSection section : defaultSections) {
			GranterReportSection templateSection = new GranterReportSection();
			templateSection.setDeletable(true);
			templateSection.setReportTemplate(defaultTemplate);

			templateSection.setGranter((Granter) org);
			templateSection.setSectionName(section.getSectionName());
			templateSection.setSectionOrder(sectionOrder++);
			templateSection = grantService.saveReportTemplateSection(templateSection);

			if (section.getAttributes() != null && section.getAttributes().size() > 0) {
				int attributeOrder = 1;
				for (GranterReportSectionAttribute attribute : section.getAttributes()) {
					GranterReportSectionAttribute templateSectionAttribute = new GranterReportSectionAttribute();
					templateSectionAttribute.setAttributeOrder(attributeOrder++);
					templateSectionAttribute.setDeletable(true);
					templateSectionAttribute.setFieldName(attribute.getFieldName());
					templateSectionAttribute.setFieldType(attribute.getFieldType());
					templateSectionAttribute.setGranter((Granter) org);
					templateSectionAttribute.setRequired(false);
					templateSectionAttribute.setSection(templateSection);
					templateSectionAttribute = grantService
							.saveReportTemplateSectionAttribute(templateSectionAttribute);
				}
			}
		}
	}

	private void buildWorkflowsBasedOnSustainPlus(Organization org) {

		Organization susplusOrg = organizationService.findOrganizationByTenantCode("SUSTAINPLUS");
		Workflow susplusGrantWorkflow = workflowService.findByGranterAndObject(susplusOrg, WorkflowObject.GRANT);
		Workflow susplusReportWorkflow = workflowService.findByGranterAndObject(susplusOrg, WorkflowObject.REPORT);
		Workflow susplusDisbursementWorkflow = workflowService.findByGranterAndObject(susplusOrg,
				WorkflowObject.DISBURSEMENT);
		List<WorkflowStatus> susplusGrantStatuses = workflowStatusService
				.getTenantWorkflowStatuses(WorkflowObject.GRANT.name(), susplusOrg.getId());
		List<WorkflowStatus> susplusReportStatuses = workflowStatusService
				.getTenantWorkflowStatuses(WorkflowObject.REPORT.name(), susplusOrg.getId());
		List<WorkflowStatus> susplusDisbursementStatuses = workflowStatusService
				.getTenantWorkflowStatuses(WorkflowObject.DISBURSEMENT.name(), susplusOrg.getId());
		List<WorkflowStatusTransition> susplusGrantTransitions = workflowStatusTransitionService
				.getStatusTransitionsForWorkflow(susplusGrantWorkflow);
		List<WorkflowStatusTransition> susplusReportTransitions = workflowStatusTransitionService
				.getStatusTransitionsForWorkflow(susplusReportWorkflow);
		List<WorkflowStatusTransition> susplusDisbursementTransitions = workflowStatusTransitionService
				.getStatusTransitionsForWorkflow(susplusDisbursementWorkflow);

		generateGrantWorkflow(org, susplusGrantStatuses, susplusGrantTransitions);
		generateReportWorkflow(org, susplusReportStatuses, susplusReportTransitions);
		generateDisbursementWorkflow(org, susplusDisbursementStatuses, susplusDisbursementTransitions);

	}

	private void generateGrantWorkflow(Organization org, List<WorkflowStatus> susplusGrantStatuses,
			List<WorkflowStatusTransition> susplusGrantTransitions) {
		Workflow grantWorkflow = new Workflow();
		grantWorkflow.setCreatedAt(DateTime.now().toDate());
		grantWorkflow.setCreatedBy("System");
		grantWorkflow.setDescription("Default " + org.getName() + " Grant workflow");
		grantWorkflow.setGranter(org);
		grantWorkflow.setName("Default " + org.getName() + " Grant workflow");
		grantWorkflow.setObject(WorkflowObject.GRANT);
		grantWorkflow = workflowService.saveWorkflow(grantWorkflow);

		List<WorkflowStatus> grantStatuses = new ArrayList<>();
		for (WorkflowStatus susplusStatus : susplusGrantStatuses) {
			WorkflowStatus status = new WorkflowStatus();
			status.setCreatedAt(DateTime.now().toDate());
			status.setCreatedBy("System");
			status.setDisplayName(susplusStatus.getDisplayName());
			status.setInitial(susplusStatus.isInitial());
			status.setInternalStatus(susplusStatus.getInternalStatus());
			status.setName(susplusStatus.getName());
			status.setTerminal(susplusStatus.getTerminal());
			status.setWorkflow(grantWorkflow);
			status = workflowStatusService.saveWorkflowStatus(status);
			grantStatuses.add(status);
		}

		for (WorkflowStatusTransition susplusTransition : susplusGrantTransitions) {

			WorkflowStatusTransition transition = new WorkflowStatusTransition();
			transition.setAction(susplusTransition.getAction());
			transition.setCreatedAt(DateTime.now().toDate());
			transition.setCreatedBy("System");
			WorkflowStatus fromStatus = susplusGrantStatuses.stream()
					.filter(st -> st.getId().longValue() == susplusTransition.getFromState().getId()).findFirst().get();
			WorkflowStatus toStatus = susplusGrantStatuses.stream()
					.filter(st -> st.getId().longValue() == susplusTransition.getToState().getId()).findFirst().get();
			transition.setFromState(grantStatuses.stream()
					.filter(s -> s.getName().equalsIgnoreCase(fromStatus.getName())).findFirst().get());
			transition.setToState(grantStatuses.stream().filter(s -> s.getName().equalsIgnoreCase(toStatus.getName()))
					.findFirst().get());
			transition.setNoteRequired(true);
			transition.setSeqOrder(susplusTransition.getSeqOrder());
			transition.setWorkflow(grantWorkflow);
			transition = workflowStatusTransitionService.saveStatusTransition(transition);
		}
	}

	private void generateReportWorkflow(Organization org, List<WorkflowStatus> susplusReportStatuses,
			List<WorkflowStatusTransition> susplusReportTransitions) {
		Workflow reportWorkflow = new Workflow();
		reportWorkflow.setCreatedAt(DateTime.now().toDate());
		reportWorkflow.setCreatedBy("System");
		reportWorkflow.setDescription("Default " + org.getName() + " Report workflow");
		reportWorkflow.setGranter(org);
		reportWorkflow.setName("Default " + org.getName() + " Report workflow");
		reportWorkflow.setObject(WorkflowObject.REPORT);
		reportWorkflow = workflowService.saveWorkflow(reportWorkflow);

		List<WorkflowStatus> reportStatuses = new ArrayList<>();
		for (WorkflowStatus susplusStatus : susplusReportStatuses) {
			WorkflowStatus status = new WorkflowStatus();
			status.setCreatedAt(DateTime.now().toDate());
			status.setCreatedBy("System");
			status.setDisplayName(susplusStatus.getDisplayName());
			status.setInitial(susplusStatus.isInitial());
			status.setInternalStatus(susplusStatus.getInternalStatus());
			status.setName(susplusStatus.getName());
			status.setTerminal(susplusStatus.getTerminal());
			status.setWorkflow(reportWorkflow);
			status = workflowStatusService.saveWorkflowStatus(status);
			reportStatuses.add(status);
		}

		for (WorkflowStatusTransition susplusTransition : susplusReportTransitions) {

			WorkflowStatusTransition transition = new WorkflowStatusTransition();
			transition.setAction(susplusTransition.getAction());
			transition.setCreatedAt(DateTime.now().toDate());
			transition.setCreatedBy("System");
			WorkflowStatus fromStatus = susplusReportStatuses.stream()
					.filter(st -> st.getId().longValue() == susplusTransition.getFromState().getId()).findFirst().get();
			WorkflowStatus toStatus = susplusReportStatuses.stream()
					.filter(st -> st.getId().longValue() == susplusTransition.getToState().getId()).findFirst().get();
			transition.setFromState(reportStatuses.stream()
					.filter(s -> s.getName().equalsIgnoreCase(fromStatus.getName())).findFirst().get());
			transition.setToState(reportStatuses.stream().filter(s -> s.getName().equalsIgnoreCase(toStatus.getName()))
					.findFirst().get());
			transition.setNoteRequired(true);
			transition.setSeqOrder(susplusTransition.getSeqOrder());
			transition.setWorkflow(reportWorkflow);
			transition = workflowStatusTransitionService.saveStatusTransition(transition);
		}
	}

	private void generateDisbursementWorkflow(Organization org, List<WorkflowStatus> susplusDisbursementStatuses,
			List<WorkflowStatusTransition> susplusDisbursementTransitions) {
		Workflow disbursementWorkflow = new Workflow();
		disbursementWorkflow.setCreatedAt(DateTime.now().toDate());
		disbursementWorkflow.setCreatedBy("System");
		disbursementWorkflow.setDescription("Default " + org.getName() + " Disbursement workflow");
		disbursementWorkflow.setGranter(org);
		disbursementWorkflow.setName("Default " + org.getName() + " Disbursement workflow");
		disbursementWorkflow.setObject(WorkflowObject.DISBURSEMENT);
		disbursementWorkflow = workflowService.saveWorkflow(disbursementWorkflow);

		List<WorkflowStatus> disbursementStatuses = new ArrayList<>();
		for (WorkflowStatus susplusStatus : susplusDisbursementStatuses) {
			WorkflowStatus status = new WorkflowStatus();
			status.setCreatedAt(DateTime.now().toDate());
			status.setCreatedBy("System");
			status.setDisplayName(susplusStatus.getDisplayName());
			status.setInitial(susplusStatus.isInitial());
			status.setInternalStatus(susplusStatus.getInternalStatus());
			status.setName(susplusStatus.getName());
			status.setTerminal(susplusStatus.getTerminal());
			status.setWorkflow(disbursementWorkflow);
			status = workflowStatusService.saveWorkflowStatus(status);
			disbursementStatuses.add(status);
		}

		for (WorkflowStatusTransition susplusTransition : susplusDisbursementTransitions) {

			WorkflowStatusTransition transition = new WorkflowStatusTransition();
			transition.setAction(susplusTransition.getAction());
			transition.setCreatedAt(DateTime.now().toDate());
			transition.setCreatedBy("System");
			WorkflowStatus fromStatus = susplusDisbursementStatuses.stream()
					.filter(st -> st.getId().longValue() == susplusTransition.getFromState().getId()).findFirst().get();
			WorkflowStatus toStatus = susplusDisbursementStatuses.stream()
					.filter(st -> st.getId().longValue() == susplusTransition.getToState().getId()).findFirst().get();
			transition.setFromState(disbursementStatuses.stream()
					.filter(s -> s.getName().equalsIgnoreCase(fromStatus.getName())).findFirst().get());
			transition.setToState(disbursementStatuses.stream()
					.filter(s -> s.getName().equalsIgnoreCase(toStatus.getName())).findFirst().get());
			transition.setNoteRequired(true);
			transition.setSeqOrder(susplusTransition.getSeqOrder());
			transition.setWorkflow(disbursementWorkflow);
			transition = workflowStatusTransitionService.saveStatusTransition(transition);
		}
	}

}
