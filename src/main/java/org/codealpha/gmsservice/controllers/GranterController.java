package org.codealpha.gmsservice.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Api(value = "Granters",description = "Granter API endpoints")
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
	public Rfp createRfp(@PathVariable(name = "granterId") Long organizationId,
			@RequestBody Rfp rfp) {

		//rfp.setGranter(granterRepository.findById(organizationId).get());
		rfp.setCreatedAt(LocalDateTime.now());
		rfp.setCreatedBy("Admin");
		return rfpRepository.save(rfp);

	}

	@PostMapping("/user/{userId}/onboard/{grantName}/slug/{tenantSlug}/granterUser/{granterUserEmail}")
	@ApiOperation(value = "Onboard new granter with basic details",notes = "Currently harcoded users and roles for the newly created Granter is implemented. This feature will be enhanced in the future")
	public Organization onBoardGranter(@ApiParam(name = "granterName",value = "Name of new granter being onboarded") @PathVariable("grantName") String granterName,@ApiParam(name="slug",value = "Name of granter slug. This will be used to create the Tenant Code as well us the subdomain") @PathVariable("tenantSlug") String slug,@ApiParam(name = "image",value = "Uploaded image file to be used as granter's logo") @RequestParam(value = "file") MultipartFile image,@ApiParam(name="userId",value = "Unique identifier of logged in user") @PathVariable("userId") Long userId,@ApiParam(name = "userEmail",value = "Email Id of primary admin of Granter organization to whom email invite will be sent") @PathVariable("granterUserEmail") String userEmail){


		Organization org = new Granter();
		/*org.setCode(slug.toUpperCase());
		org.setCreatedAt(DateTime.now().toDate());
		org.setCreatedBy(userService.getUserById(userId).getEmailId());
		org.setName(granterName);
		org.setOrganizationType("GRANTER");
		org = granterService.createGranter((Granter)org,image);

        try {
            String filePath = uploadLocation + slug.toUpperCase() + "/logo/";
            File dir = new File(filePath);
            dir.mkdirs();

            File fileToCreate = new File(dir, image.getOriginalFilename());
            image.transferTo(fileToCreate);

        } catch (IOException e) {
            e.printStackTrace();
        }

        User granterUser = new User();
		granterUser.setActive(true);
		granterUser.setCreatedAt(DateTime.now().toDate());
		granterUser.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUser.setFirstName("");
		granterUser.setLastName("");
		granterUser.setOrganization(org);
		granterUser.setEmailId(userEmail);
		granterUser.setPassword("password");
		granterUser = userService.save(granterUser);

		User granterUserPE = new User();
		granterUserPE.setActive(true);
		granterUserPE.setCreatedAt(DateTime.now().toDate());
		granterUserPE.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUserPE.setFirstName("Vineet Prasani");
		granterUserPE.setLastName("PE");
		granterUserPE.setOrganization(org);
		granterUserPE.setEmailId("vineet.prasani@gmail.com");
		granterUserPE.setPassword("password");
		granterUserPE = userService.save(granterUserPE);

		User granterUserFE = new User();
		granterUserFE.setActive(true);
		granterUserFE.setCreatedAt(DateTime.now().toDate());
		granterUserFE.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUserFE.setFirstName("Vineet Prasani");
		granterUserFE.setLastName("FE");
		granterUserFE.setOrganization(org);
		granterUserFE.setEmailId("tbd@gmail.com");
		granterUserFE.setPassword("password");
		granterUserFE = userService.save(granterUserFE);

		User granterUserRPL = new User();
		granterUserRPL.setActive(true);
		granterUserRPL.setCreatedAt(DateTime.now().toDate());
		granterUserRPL.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUserRPL.setFirstName("Vineet Prasani");
		granterUserRPL.setLastName("RPL");
		granterUserRPL.setOrganization(org);
		granterUserRPL.setEmailId("vineet_prasani@email.com");
		granterUserRPL.setPassword("password");
		granterUserRPL = userService.save(granterUserRPL);

		User granterUserCPL = new User();
		granterUserCPL.setActive(true);
		granterUserCPL.setCreatedAt(DateTime.now().toDate());
		granterUserCPL.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUserCPL.setFirstName("Vineet Prasani");
		granterUserCPL.setLastName("CPL");
		granterUserCPL.setOrganization(org);
		granterUserCPL.setEmailId("vineet@socialalpha.org");
		granterUserCPL.setPassword("password");
		granterUserCPL = userService.save(granterUserCPL);

		User granterUserBM = new User();
		granterUserBM.setActive(true);
		granterUserBM.setCreatedAt(DateTime.now().toDate());
		granterUserBM.setCreatedBy(userService.getUserById(userId).getEmailId());
		granterUserBM.setFirstName("Ranjit Victor");
		granterUserBM.setLastName("Board");
		granterUserBM.setOrganization(org);
		granterUserBM.setEmailId("ranjit.victor@enstratify.com");
		granterUserBM.setPassword("password");
		granterUserBM = userService.save(granterUserBM);



		Role role = new Role();
		role.setCreatedAt(DateTime.now().toDate());
		role.setCreatedBy(userService.getUserById(userId).getEmailId());
		role.setName("Admin");
		role.setOrganization(org);
		role = roleService.saveRole(role);
		List<RolesPermission> rolesPermissions = new ArrayList<>();
		rolesPermissions.add(new RolesPermission(role,"Create Grant"));
		rolesPermissions.add(new RolesPermission(role,"Delete Grant"));
		rolesPermissions.add(new RolesPermission(role,"Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissions);

		Role rolePE = new Role();
		rolePE.setCreatedAt(DateTime.now().toDate());
		rolePE.setCreatedBy(userService.getUserById(userId).getEmailId());
		rolePE.setName("Programme Executive");
		rolePE.setOrganization(org);
		rolePE = roleService.saveRole(rolePE);
		List<RolesPermission> rolesPermissionsPE = new ArrayList<>();
		rolesPermissionsPE.add(new RolesPermission(rolePE,"Create Grant"));
		rolesPermissionsPE.add(new RolesPermission(rolePE,"Delete Grant"));
		rolesPermissionsPE.add(new RolesPermission(rolePE,"Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissionsPE);

		Role roleFE = new Role();
		roleFE.setCreatedAt(DateTime.now().toDate());
		roleFE.setCreatedBy(userService.getUserById(userId).getEmailId());
		roleFE.setName("Finance Executive");
		roleFE.setOrganization(org);
		roleFE = roleService.saveRole(roleFE);
		List<RolesPermission> rolesPermissionsFE = new ArrayList<>();
		rolesPermissionsFE.add(new RolesPermission(roleFE,"Create Grant"));
		rolesPermissionsFE.add(new RolesPermission(roleFE,"Delete Grant"));
		rolesPermissionsFE.add(new RolesPermission(roleFE,"Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissionsFE);

		Role roleRPL = new Role();
		roleRPL.setCreatedAt(DateTime.now().toDate());
		roleRPL.setCreatedBy(userService.getUserById(userId).getEmailId());
		roleRPL.setName("Regional Program Lead");
		roleRPL.setOrganization(org);
		roleRPL = roleService.saveRole(roleRPL);
		List<RolesPermission> rolesPermissionsRPL = new ArrayList<>();
		rolesPermissionsRPL.add(new RolesPermission(roleRPL,"Create Grant"));
		rolesPermissionsRPL.add(new RolesPermission(roleRPL,"Delete Grant"));
		rolesPermissionsRPL.add(new RolesPermission(roleRPL,"Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissionsRPL);

		Role roleCPL = new Role();
		roleCPL.setCreatedAt(DateTime.now().toDate());
		roleCPL.setCreatedBy(userService.getUserById(userId).getEmailId());
		roleCPL.setName("Central Program Lead");
		roleCPL.setOrganization(org);
		roleCPL = roleService.saveRole(roleCPL);
		List<RolesPermission> rolesPermissionsCPL = new ArrayList<>();
		rolesPermissionsCPL.add(new RolesPermission(roleCPL,"Create Grant"));
		rolesPermissionsCPL.add(new RolesPermission(roleCPL,"Delete Grant"));
		rolesPermissionsCPL.add(new RolesPermission(roleCPL,"Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissionsCPL);

		Role roleBM = new Role();
		roleBM.setCreatedAt(DateTime.now().toDate());
		roleBM.setCreatedBy(userService.getUserById(userId).getEmailId());
		roleBM.setName("Board Member");
		roleBM.setOrganization(org);
		roleBM = roleService.saveRole(roleBM);
		List<RolesPermission> rolesPermissionsBM = new ArrayList<>();
		rolesPermissionsBM.add(new RolesPermission(roleBM,"Create Grant"));
		rolesPermissionsBM.add(new RolesPermission(roleBM,"Delete Grant"));
		rolesPermissionsBM.add(new RolesPermission(roleBM,"Manage Workflows"));
		rolesPermissionService.saveRolePermissions(rolesPermissionsBM);


		UserRole userRole = new UserRole();
		userRole.setRole(role);
		userRole.setUser(granterUser);
		userRole = userRoleService.saveUserRole(userRole);

		userRole = new UserRole();
		userRole.setRole(rolePE);
		userRole.setUser(granterUserPE);
		userRole = userRoleService.saveUserRole(userRole);

		userRole = new UserRole();
		userRole.setRole(roleFE);
		userRole.setUser(granterUserFE);
		userRole = userRoleService.saveUserRole(userRole);

		userRole = new UserRole();
		userRole.setRole(roleRPL);
		userRole.setUser(granterUserRPL);
		userRole = userRoleService.saveUserRole(userRole);

		userRole = new UserRole();
		userRole.setRole(roleCPL);
		userRole.setUser(granterUserCPL);
		userRole = userRoleService.saveUserRole(userRole);

		userRole = new UserRole();
		userRole.setRole(roleBM);
		userRole.setUser(granterUserBM);
		userRole = userRoleService.saveUserRole(userRole);


		GranterGrantTemplate defaulTemplate = new GranterGrantTemplate();
		defaulTemplate.setPublished(true);
		defaulTemplate.setName("Anudan Template");
		defaulTemplate.setGranterId(org.getId());
		defaulTemplate.setDescription("Default Anudan template.");
		defaulTemplate =grantService.saveGrantTemplate(defaulTemplate);
		int order=1;
		for(GrantSection defaultSection: grantSectionService.getAllDefaultSections()){
			GranterGrantSection section = new GranterGrantSection();
			section.setDeletable(true);
			section.setGranter(granterService.getGranterById(org.getId()));
			section.setGrantTemplate(defaulTemplate);
			section.setSectionName(defaultSection.getSectionName());
			section.setSectionOrder(order++);
			section = grantService.saveGrantTemaplteSection(section);

			int attributeOrder = 1;
			for(GrantSectionAttribute defaultAttribute : grantSectionAttributeService.getAllDefaultSectionAttributesForSection(defaultSection.getId())){
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

		Workflow workflow = new Workflow();
		workflow.setCreatedAt(DateTime.now().toDate());
		workflow.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflow.setDescription("Default " + org.getName() + " Grant workflow");
		workflow.setGranter(granterService.getGranterById(org.getId()));
		workflow.setName("Default " + org.getName() + " Grant workflow");
		workflow.setObject(WorkflowObject.GRANT);
		workflow = workflowService.saveWorkflow(workflow);

		WorkflowStatus workflowStatusDraft = new WorkflowStatus();
		workflowStatusDraft.setCreatedAt(DateTime.now().toDate());
		workflowStatusDraft.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusDraft.setDisplayName("DRAFT");
		workflowStatusDraft.setInitial(true);
		workflowStatusDraft.setInternalStatus("DRAFT");
		workflowStatusDraft.setName("Draft");
		workflowStatusDraft.setTerminal(false);
		workflowStatusDraft.setVerb("Draft");
		workflowStatusDraft.setWorkflow(workflow);
		workflowStatusDraft = workflowStatusService.saveWorkflowStatus(workflowStatusDraft);

		WorkflowStatus workflowStatusSubmitted1 = new WorkflowStatus();
		workflowStatusSubmitted1.setCreatedAt(DateTime.now().toDate());
		workflowStatusSubmitted1.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusSubmitted1.setDisplayName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted1.setInitial(false);
		workflowStatusSubmitted1.setInternalStatus("DRAFT");
		workflowStatusSubmitted1.setName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted1.setTerminal(false);
		workflowStatusSubmitted1.setVerb("Submitted (Pending Review)");
		workflowStatusSubmitted1.setWorkflow(workflow);
		workflowStatusSubmitted1 = workflowStatusService.saveWorkflowStatus(workflowStatusSubmitted1);

		WorkflowStatus workflowStatusSubmitted2 = new WorkflowStatus();
		workflowStatusSubmitted2.setCreatedAt(DateTime.now().toDate());
		workflowStatusSubmitted2.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusSubmitted2.setDisplayName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted2.setInitial(false);
		workflowStatusSubmitted2.setInternalStatus("DRAFT");
		workflowStatusSubmitted2.setName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted2.setTerminal(false);
		workflowStatusSubmitted2.setVerb("Submitted (Pending Review)");
		workflowStatusSubmitted2.setWorkflow(workflow);
		workflowStatusSubmitted2 = workflowStatusService.saveWorkflowStatus(workflowStatusSubmitted2);

		WorkflowStatus workflowStatusSubmitted3 = new WorkflowStatus();
		workflowStatusSubmitted3.setCreatedAt(DateTime.now().toDate());
		workflowStatusSubmitted3.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusSubmitted3.setDisplayName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted3.setInitial(false);
		workflowStatusSubmitted3.setInternalStatus("DRAFT");
		workflowStatusSubmitted3.setName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted3.setTerminal(false);
		workflowStatusSubmitted3.setVerb("Submitted (Pending Review)");
		workflowStatusSubmitted3.setWorkflow(workflow);
		workflowStatusSubmitted3 = workflowStatusService.saveWorkflowStatus(workflowStatusSubmitted3);

		WorkflowStatus workflowStatusSubmitted4 = new WorkflowStatus();
		workflowStatusSubmitted4.setCreatedAt(DateTime.now().toDate());
		workflowStatusSubmitted4.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusSubmitted4.setDisplayName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted4.setInitial(false);
		workflowStatusSubmitted4.setInternalStatus("DRAFT");
		workflowStatusSubmitted4.setName("SUBMITTED (Pending Review)");
		workflowStatusSubmitted4.setTerminal(false);
		workflowStatusSubmitted4.setVerb("Submitted (Pending Review)");
		workflowStatusSubmitted4.setWorkflow(workflow);
		workflowStatusSubmitted4 = workflowStatusService.saveWorkflowStatus(workflowStatusSubmitted4);

		WorkflowStatus workflowStatusApproved = new WorkflowStatus();
		workflowStatusApproved.setCreatedAt(DateTime.now().toDate());
		workflowStatusApproved.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusApproved.setDisplayName("APPROVED");
		workflowStatusApproved.setInitial(false);
		workflowStatusApproved.setInternalStatus("ACTIVE");
		workflowStatusApproved.setName("APPROVED");
		workflowStatusApproved.setTerminal(false);
		workflowStatusApproved.setVerb("Approved");
		workflowStatusApproved.setWorkflow(workflow);
		workflowStatusApproved = workflowStatusService.saveWorkflowStatus(workflowStatusApproved);

		WorkflowStatus workflowStatusClosed = new WorkflowStatus();
		workflowStatusClosed.setCreatedAt(DateTime.now().toDate());
		workflowStatusClosed.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusClosed.setDisplayName("CLOSED");
		workflowStatusClosed.setInitial(false);
		workflowStatusClosed.setInternalStatus("CLOSED");
		workflowStatusClosed.setName("APPROVED");
		workflowStatusClosed.setTerminal(true);
		workflowStatusClosed.setVerb("Closed");
		workflowStatusClosed.setWorkflow(workflow);
		workflowStatusClosed = workflowStatusService.saveWorkflowStatus(workflowStatusClosed);

		WorkflowStatusTransition workflowStatusTransition =  new WorkflowStatusTransition();

		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Submit");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusDraft);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(rolePE);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted1);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);



		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Return");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted1);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleFE);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusDraft);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);

		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Approve");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted1);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleFE);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted2);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);

		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Return");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted2);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleRPL);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted1);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);


		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Approve");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted2);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleRPL);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted3);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);

		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Return");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted3);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleCPL);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted2);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);


		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Approve");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted3);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleCPL);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted4);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);

		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Return");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted4);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleBM);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusSubmitted3);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);


		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Approve");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusSubmitted4);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleBM);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusApproved);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);


		workflowStatusTransition =  new WorkflowStatusTransition();
		workflowStatusTransition.setAction("Close");
		workflowStatusTransition.setCreatedAt(DateTime.now().toDate());
		workflowStatusTransition.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflowStatusTransition.setFromState(workflowStatusApproved);
		workflowStatusTransition.setNoteRequired(true);
		workflowStatusTransition.setRole(roleRPL);
		workflowStatusTransition.setWorkflow(workflow);
		workflowStatusTransition.setToState(workflowStatusClosed);
		workflowStatusTransition = workflowStatusTransitionService.saveStatusTransition(workflowStatusTransition);

		WorkflowStatePermission permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusDraft);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusDraft);
		permission.setRole(rolePE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusDraft);
		permission.setRole(roleFE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusDraft);
		permission.setRole(roleRPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusDraft);
		permission.setRole(roleCPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusDraft);
		permission.setRole(roleBM);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);

		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted1);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted1);
		permission.setRole(rolePE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted1);
		permission.setRole(roleFE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);


		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted2);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted2);
		permission.setRole(rolePE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted2);
		permission.setRole(roleFE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted2);
		permission.setRole(roleRPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);

		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted3);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted3);
		permission.setRole(rolePE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted3);
		permission.setRole(roleFE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted3);
		permission.setRole(roleRPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted3);
		permission.setRole(roleCPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);

		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("MANAGE");
		permission.setWorkflowStatus(workflowStatusSubmitted4);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted4);
		permission.setRole(rolePE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted4);
		permission.setRole(roleFE);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted4);
		permission.setRole(roleRPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted4);
		permission.setRole(roleCPL);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusSubmitted4);
		permission.setRole(roleBM);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);

		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusApproved);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);

		permission = new WorkflowStatePermission();
		permission.setCreatedAt(DateTime.now().toDate());
		permission.setCreatedBy(userService.getUserById(userId).getEmailId());
		permission.setPermission("VIEW");
		permission.setWorkflowStatus(workflowStatusClosed);
		permission.setRole(role);
		permission = workflowStatePermissionService.saveWorkflowStatePermission(permission);
*/

		return org;
	}

}
