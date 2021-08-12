package org.codealpha.gmsservice.controllers;

import javax.validation.constraints.NotNull;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.repositories.OrganizationRepository;
import org.codealpha.gmsservice.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Developer code-alpha.org
 **/
@RestController
@RequestMapping(value = "/organizations", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiIgnore
public class OrganizationController {

	@Autowired
	private OrganizationService service;

	@Autowired
	private OrganizationRepository repository;
	@org.springframework.beans.factory.annotation.Value("${spring.upload-file-location}")
	private String uploadLocation;


	@GetMapping("/{organizationId}")
	public Organization get(@NotNull @PathVariable("organizationId") Long organizationId) {
		return service.get(organizationId);
	}

	@GetMapping("/")
	public void getAll() {
		System.out.println(repository.findAll());
	}


	@PostMapping("/")
	public Organization saveOrganization(@RequestBody Organization org){
		org = service.save(org);

		return org;
	}

	@PostMapping(value="/logo",consumes = {"multipart/form-data" })
	public void saveOrganizationLogo(@RequestParam("file") MultipartFile image,
											 @RequestHeader("X-TENANT-CODE") String tenantCode){
		String filePath = uploadLocation + tenantCode + "/logo/";
		File dir = new File(filePath);
		dir.mkdirs();

		File fileToCreate = new File(dir, "logo.png");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileToCreate);
			fos.write(image.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
