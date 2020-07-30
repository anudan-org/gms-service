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

		Workflow workflow = new Workflow();
		workflow.setCreatedAt(DateTime.now().toDate());
		workflow.setCreatedBy(userService.getUserById(userId).getEmailId());
		workflow.setDescription("Default " + org.getName() + " Grant workflow");
		workflow.setGranter(granterService.getGranterById(org.getId()));
		workflow.setName("Default " + org.getName() + " Grant workflow");
		workflow.setObject(WorkflowObject.GRANT);
		workflow = workflowService.saveWorkflow(workflow);

		/*
		 * organizationService.buildInviteUrlAndSendMail(userService, ,
		 * commonEmailSevice, releaseService, adminUser, org, user, userRoles); return
		 * org;
		 */

		return org;
	}

}
