package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.constants.WorkflowObject;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.models.GranterDTO;
import org.codealpha.gmsservice.models.RfpDTO;
import org.codealpha.gmsservice.repositories.GranterRepository;
import org.codealpha.gmsservice.repositories.RfpRepository;
import org.codealpha.gmsservice.services.*;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Developer code-alpha.org
 **/
@RestController
@RequestMapping(value = "/granter", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GranterController {

	private static final Logger logger = LoggerFactory.getLogger(GranterController.class);
	public static final String SECURE_SITES_TXT = "/opt/gms/secure-sites.txt";
	public static final String ADMIN = "Admin";
	public static final String SYSTEM = "System";
	public static final String DEFAULT = "Default ";
	public static final String GRANT_WORKFLOW = " Grant workflow";
	public static final String ANUDAN_DEFAULT_REPORT_TEMPLATE = "Anudan Default Report Template";
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
	@Autowired
	private ModelMapper modelMapper;

	@Value("${spring.upload-file-location}")
	private String uploadLocation;

	@PostMapping(value = "/")
	@ApiIgnore
	public void create(@RequestBody GranterDTO granter) {

		granter.setCreatedAt(DateTime.now().toDate());
		granter.setCreatedBy(ADMIN);
		granterRepository.save(modelMapper.map(granter,Granter.class));

	}

	@PostMapping("/{granterId}/rfps/")
	@ApiIgnore
	public Rfp createRfp(@PathVariable(name = "granterId") Long organizationId, @RequestBody RfpDTO rfp) {

		rfp.setCreatedAt(LocalDateTime.now());
		rfp.setCreatedBy(ADMIN);
		return rfpRepository.save(modelMapper.map(rfp,Rfp.class));

	}

	@PostMapping(value = "/user/{userId}/onboard/{grantName}/slug/{tenantSlug}/granterUser/{granterUserEmail}/{refOrgCode}", consumes = {
			"multipart/form-data" })
	@ApiOperation(value = "Onboard new granter with basic details", notes = "Currently harcoded users and roles for the newly created Granter is implemented. This feature will be enhanced in the future")
	public Organization onBoardGranter(
			@ApiParam(name = "granterName", value = "Name of new granter being onboarded") @PathVariable("grantName") String granterName,
			@ApiParam(name = "slug", value = "Name of granter slug. This will be used to create the Tenant Code as well us the subdomain") @PathVariable("tenantSlug") String slug,
			@ApiParam(name = "image", value = "Uploaded image file to be used as granter's logo") @RequestParam(value = "file") MultipartFile image,
			@ApiParam(name = "userId", value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,
			@ApiParam(name = "userEmail", value = "Email Id of primary admin of Granter organization to whom email invite will be sent") @PathVariable("granterUserEmail") String userEmail,
			@PathVariable("refOrgCode") String refOrgCode) {

		Organization org = new Granter();
		org.setCode(slug.toUpperCase());
		org.setCreatedAt(DateTime.now().toDate());
		org.setCreatedBy(userService.getUserById(userId).getEmailId());
		org.setName(granterName);
		org.setOrganizationType("GRANTER");
		org = granterService.createGranter((Granter) org, image);

		File file = new File(SECURE_SITES_TXT);
		try{

			Files.write(file.toPath(),System.lineSeparator().concat(slug.toLowerCase()).concat(".").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String filePath = uploadLocation + slug.toUpperCase() + "/logo/";
		File dir = new File(filePath);
		dir.mkdirs();
		File fileToCreate = new File(dir, "logo.png");
		try(FileOutputStream fos = new FileOutputStream(fileToCreate)) {

			image.getOriginalFilename();
			fos.write(image.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

		User granterUser = new User();
		granterUser.setActive(false);
		granterUser.setCreatedAt(DateTime.now().toDate());
		granterUser.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUser.setFirstName("");
		granterUser.setLastName("");
		granterUser.setOrganization(org);
		granterUser.setEmailId(userEmail);
		granterUser = userService.save(granterUser);

		Role role = new Role();
		role.setCreatedAt(DateTime.now().toDate());
		role.setCreatedBy(userService.getUserById(userId).getEmailId());
		role.setName(ADMIN);
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
				userService.getUserById(userId), org, granterUser, Arrays.asList(userRole));


		buildWorkflowsBasedOnTempOrg(org,refOrgCode);
		buildDefaultTemplates(org, organizationService.findOrganizationByTenantCode(refOrgCode));

		return org;
	}

	private void buildDefaultTemplates(Organization org, Organization referenceOrg) {

		buildDefaultGrantTemplate(org, referenceOrg);
		buildDefaultReportTemplate(org, referenceOrg);
	}

	private void buildDefaultGrantTemplate(Organization org, Organization referenceOrg) {
		Optional<GranterGrantTemplate> granterGrantTemplate = granterGrantTemplateService
				.findByGranterIdAndPublishedStatusAndPrivateStatus(referenceOrg.getId(), true, false).stream()
				.filter(GranterGrantTemplate::isDefaultTemplate).findFirst();

		List<GranterGrantSection> defaultSections = granterGrantTemplate.isPresent()?granterGrantTemplate.get().getSections():new ArrayList<>();
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

			if (section.getAttributes() != null && !section.getAttributes().isEmpty()) {
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
					grantService.saveGrantTemaplteSectionAttribute(templateSectionAttribute);
				}
			}
		}
	}

	private void buildDefaultReportTemplate(Organization org, Organization referenceOrg) {
		Optional<GranterReportTemplate> granterReportTemplate = granterReportTemplateService
				.findByGranterIdAndPublishedStatusAndPrivateStatus(referenceOrg.getId(), true, false).stream()
				.filter(GranterReportTemplate::getDefaultTemplate).findFirst();
		List<GranterReportSection> defaultSections = granterReportTemplate.isPresent()?granterReportTemplate.get().getSections():new ArrayList<>();
		GranterReportTemplate defaultTemplate = new GranterReportTemplate();
		defaultTemplate.setDefaultTemplate(true);
		defaultTemplate.setDescription(ANUDAN_DEFAULT_REPORT_TEMPLATE);
		defaultTemplate.setGranterId(org.getId());
		defaultTemplate.setName(ANUDAN_DEFAULT_REPORT_TEMPLATE);
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

			if (section.getAttributes() != null && !section.getAttributes().isEmpty()) {
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
					grantService
							.saveReportTemplateSectionAttribute(templateSectionAttribute);
				}
			}
		}
	}

	private void buildWorkflowsBasedOnTempOrg(Organization org,String refOrgCode) {

		Organization tempOrg = organizationService.findOrganizationByTenantCode(refOrgCode);
		Workflow tempGrantWorkflow = workflowService.findDefaultByGranterAndObject(tempOrg.getId(), "GRANT");
		Workflow tempReportWorkflow = workflowService.findDefaultByGranterAndObject(tempOrg.getId(), "REPORT");
		Workflow tempDisbursementWorkflow = workflowService.findDefaultByGranterAndObject(tempOrg.getId(), "DISBURSEMENT");
		List<WorkflowStatus> tempGrantStatuses = workflowStatusService
				.getTenantWorkflowStatuses(WorkflowObject.GRANT.name(), tempOrg.getId());
		List<WorkflowStatus> tempReportStatuses = workflowStatusService
				.getTenantWorkflowStatuses(WorkflowObject.REPORT.name(), tempOrg.getId());
		List<WorkflowStatus> tempDisbursementStatuses = workflowStatusService
				.getTenantWorkflowStatuses(WorkflowObject.DISBURSEMENT.name(), tempOrg.getId());
		List<WorkflowStatusTransition> tempGrantTransitions = workflowStatusTransitionService
				.getStatusTransitionsForWorkflow(tempGrantWorkflow);
		List<WorkflowStatusTransition> tempReportTransitions = workflowStatusTransitionService
				.getStatusTransitionsForWorkflow(tempReportWorkflow);
		List<WorkflowStatusTransition> tempDisbursementTransitions = workflowStatusTransitionService
				.getStatusTransitionsForWorkflow(tempDisbursementWorkflow);

		generateGrantWorkflow(org, tempGrantStatuses, tempGrantTransitions);
		generateReportWorkflow(org, tempReportStatuses, tempReportTransitions);
		generateDisbursementWorkflow(org, tempDisbursementStatuses, tempDisbursementTransitions);

	}

	private void generateGrantWorkflow(Organization org, List<WorkflowStatus> tempGrantStatuses,
			List<WorkflowStatusTransition> tempGrantTransitions) {
		Workflow grantWorkflow = new Workflow();
		grantWorkflow.setCreatedAt(DateTime.now().toDate());
		grantWorkflow.setCreatedBy(SYSTEM);
		grantWorkflow.setDescription(DEFAULT + org.getName() + GRANT_WORKFLOW);
		grantWorkflow.setGranter(org);
		grantWorkflow.setName(DEFAULT + org.getName() + GRANT_WORKFLOW);
		grantWorkflow.setObject(WorkflowObject.GRANT);
		grantWorkflow = workflowService.saveWorkflow(grantWorkflow);

		List<WorkflowStatus> grantStatuses = new ArrayList<>();
		for (WorkflowStatus tempStatus : tempGrantStatuses) {
			WorkflowStatus status = new WorkflowStatus();
			status.setCreatedAt(DateTime.now().toDate());
			status.setCreatedBy(SYSTEM);
			status.setDisplayName(tempStatus.getDisplayName());
			status.setInitial(tempStatus.isInitial());
			status.setInternalStatus(tempStatus.getInternalStatus());
			status.setName(tempStatus.getName());
			status.setTerminal(tempStatus.getTerminal());
			status.setWorkflow(grantWorkflow);
			status = workflowStatusService.saveWorkflowStatus(status);
			grantStatuses.add(status);
		}

		for (WorkflowStatusTransition tempTransition : tempGrantTransitions) {

			WorkflowStatusTransition transition = new WorkflowStatusTransition();
			transition.setAction(tempTransition.getAction());
			transition.setCreatedAt(DateTime.now().toDate());
			transition.setCreatedBy(SYSTEM);
			WorkflowStatus fromStatus = tempGrantStatuses.stream()
					.filter(st -> st.getId().longValue() == tempTransition.getFromState().getId()).findFirst().orElse(null);
			WorkflowStatus toStatus = tempGrantStatuses.stream()
					.filter(st -> st.getId().longValue() == tempTransition.getToState().getId()).findFirst().orElse(null);
			if(fromStatus!=null) {
				transition.setFromState(grantStatuses.stream()
						.filter(s -> s.getName().equalsIgnoreCase(fromStatus.getName())).findFirst().orElse(null));
			}
			if(toStatus!=null) {
				transition.setToState(grantStatuses.stream().filter(s -> s.getName().equalsIgnoreCase(toStatus.getName()))
						.findFirst().orElse(null));
			}
			transition.setNoteRequired(true);
			transition.setSeqOrder(tempTransition.getSeqOrder());
			transition.setWorkflow(grantWorkflow);
			workflowStatusTransitionService.saveStatusTransition(transition);
		}
	}

	private void generateReportWorkflow(Organization org, List<WorkflowStatus> tempReportStatuses,
			List<WorkflowStatusTransition> tempReportTransitions) {
		Workflow reportWorkflow = new Workflow();
		reportWorkflow.setCreatedAt(DateTime.now().toDate());
		reportWorkflow.setCreatedBy(SYSTEM);
		reportWorkflow.setDescription(DEFAULT + org.getName() + " Report workflow");
		reportWorkflow.setGranter(org);
		reportWorkflow.setName(DEFAULT + org.getName() + " Report workflow");
		reportWorkflow.setObject(WorkflowObject.REPORT);
		reportWorkflow = workflowService.saveWorkflow(reportWorkflow);

		List<WorkflowStatus> reportStatuses = new ArrayList<>();
		for (WorkflowStatus tempStatus : tempReportStatuses) {
			WorkflowStatus status = new WorkflowStatus();
			status.setCreatedAt(DateTime.now().toDate());
			status.setCreatedBy(SYSTEM);
			status.setDisplayName(tempStatus.getDisplayName());
			status.setInitial(tempStatus.isInitial());
			status.setInternalStatus(tempStatus.getInternalStatus());
			status.setName(tempStatus.getName());
			status.setTerminal(tempStatus.getTerminal());
			status.setWorkflow(reportWorkflow);
			status = workflowStatusService.saveWorkflowStatus(status);
			reportStatuses.add(status);
		}

		for (WorkflowStatusTransition tempTransition : tempReportTransitions) {

			WorkflowStatusTransition transition = new WorkflowStatusTransition();
			transition.setAction(tempTransition.getAction());
			transition.setCreatedAt(DateTime.now().toDate());
			transition.setCreatedBy(SYSTEM);
			WorkflowStatus fromStatus = tempReportStatuses.stream()
					.filter(st -> st.getId().longValue() == tempTransition.getFromState().getId()).findFirst().orElse(null);
			WorkflowStatus toStatus = tempReportStatuses.stream()
					.filter(st -> st.getId().longValue() == tempTransition.getToState().getId()).findFirst().orElse(null);
			if(fromStatus!=null) {
				transition.setFromState(reportStatuses.stream()
						.filter(s -> s.getName().equalsIgnoreCase(fromStatus.getName())).findFirst().orElse(null));

			}
			if(toStatus!=null) {
				transition.setToState(reportStatuses.stream().filter(s -> s.getName().equalsIgnoreCase(toStatus.getName()))
						.findFirst().orElse(null));
			}
			transition.setNoteRequired(true);
			transition.setSeqOrder(tempTransition.getSeqOrder());
			transition.setWorkflow(reportWorkflow);
			workflowStatusTransitionService.saveStatusTransition(transition);
		}
	}

	private void generateDisbursementWorkflow(Organization org, List<WorkflowStatus> tempDisbursementStatuses,
			List<WorkflowStatusTransition> tempDisbursementTransitions) {
		Workflow disbursementWorkflow = new Workflow();
		disbursementWorkflow.setCreatedAt(DateTime.now().toDate());
		disbursementWorkflow.setCreatedBy(SYSTEM);
		disbursementWorkflow.setDescription(DEFAULT + org.getName() + " Disbursement workflow");
		disbursementWorkflow.setGranter(org);
		disbursementWorkflow.setName(DEFAULT + org.getName() + " Disbursement workflow");
		disbursementWorkflow.setObject(WorkflowObject.DISBURSEMENT);
		disbursementWorkflow = workflowService.saveWorkflow(disbursementWorkflow);

		List<WorkflowStatus> disbursementStatuses = new ArrayList<>();
		for (WorkflowStatus tempStatus : tempDisbursementStatuses) {
			WorkflowStatus status = new WorkflowStatus();
			status.setCreatedAt(DateTime.now().toDate());
			status.setCreatedBy(SYSTEM);
			status.setDisplayName(tempStatus.getDisplayName());
			status.setInitial(tempStatus.isInitial());
			status.setInternalStatus(tempStatus.getInternalStatus());
			status.setName(tempStatus.getName());
			status.setTerminal(tempStatus.getTerminal());
			status.setWorkflow(disbursementWorkflow);
			status = workflowStatusService.saveWorkflowStatus(status);
			disbursementStatuses.add(status);
		}

		for (WorkflowStatusTransition tempTransition : tempDisbursementTransitions) {

			WorkflowStatusTransition transition = new WorkflowStatusTransition();
			transition.setAction(tempTransition.getAction());
			transition.setCreatedAt(DateTime.now().toDate());
			transition.setCreatedBy(SYSTEM);
			WorkflowStatus fromStatus = tempDisbursementStatuses.stream()
					.filter(st -> st.getId().longValue() == tempTransition.getFromState().getId()).findFirst().orElse(null);
			WorkflowStatus toStatus = tempDisbursementStatuses.stream()
					.filter(st -> st.getId().longValue() == tempTransition.getToState().getId()).findFirst().orElse(null);
			if(fromStatus!=null) {
				transition.setFromState(disbursementStatuses.stream()
						.filter(s -> s.getName().equalsIgnoreCase(fromStatus.getName())).findFirst().orElse(null));
			}
			if(toStatus!=null){
				transition.setToState(disbursementStatuses.stream()
						.filter(s -> s.getName().equalsIgnoreCase(toStatus.getName())).findFirst().orElse(null));
			}

			transition.setNoteRequired(true);
			transition.setSeqOrder(tempTransition.getSeqOrder());
			transition.setWorkflow(disbursementWorkflow);
			workflowStatusTransitionService.saveStatusTransition(transition);
		}
	}

}
