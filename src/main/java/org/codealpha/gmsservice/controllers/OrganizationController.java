package org.codealpha.gmsservice.controllers;

import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.models.OrganizationDTO;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.services.OrganizationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Developer code-alpha.org
 **/
@RestController
@RequestMapping(value = "/organizations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiIgnore
public class OrganizationController {

	private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);
	@Autowired
	private OrganizationService service;

	@Autowired
	private OrganizationRepository repository;
	@org.springframework.beans.factory.annotation.Value("${spring.upload-file-location}")
	private String uploadLocation;

	@Autowired
	private ModelMapper modelMapper;


	@GetMapping("/{organizationId}")
	public Organization get(@NotNull @PathVariable("organizationId") Long organizationId) {
		return service.get(organizationId);
	}

	@PostMapping("/")
	public Organization saveOrganization(@RequestBody OrganizationDTO org){
		if(org.getOrganizationType().equalsIgnoreCase("GRANTEE")){
			return service.save(modelMapper.map(org, Grantee.class));
		}else{
			return service.save(modelMapper.map(org, Granter.class));
		}
	}

	@PostMapping(value="/logo",consumes = {"multipart/form-data" })
	public void saveOrganizationLogo(@RequestParam("file") MultipartFile image,
									 @RequestHeader("X-TENANT-CODE") String tenantCode, HttpServletRequest request){
		String filePath = uploadLocation + tenantCode + "/logo/";
		File dir = new File(filePath);
		dir.mkdirs();

		File fileToCreate = new File(dir, "logo.png");

		try(FileOutputStream fos = new FileOutputStream(fileToCreate)) {
			fos.write(image.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

	}

	@PostMapping(value="/{orgId}/logo",consumes = {"multipart/form-data" })
	public void saveGranteeOrganizationLogo(@RequestParam("file") MultipartFile image,@PathVariable("orgId")Long granteeOrgId,
									 @RequestHeader("X-TENANT-CODE") String tenantCode){
		String filePath = uploadLocation + "GRANTEES/"+granteeOrgId + "/logo/";
		File dir = new File(filePath);
		dir.mkdirs();

		File fileToCreate = new File(dir, "logo.png");

		try(FileOutputStream fos = new FileOutputStream(fileToCreate)) {
			fos.write(image.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

	}
}
