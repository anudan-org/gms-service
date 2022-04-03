package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.codealpha.gmsservice.entities.Granter;
import org.codealpha.gmsservice.entities.Organization;
import org.codealpha.gmsservice.entities.Release;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.exceptions.ResourceNotFoundException;
import org.codealpha.gmsservice.services.OrganizationService;
import org.codealpha.gmsservice.services.ReleaseService;
import org.codealpha.gmsservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/public")
public class PublicController {

    public static final String FILE = "file:";
    private static Logger logger = LoggerFactory.getLogger(PublicController.class);

    @Autowired
    private ResourceLoader resourceLoader;
    @Value("${spring.upload-file-location}")
    private String uploadLocation;
    @Value("${spring.preview-file-location}")
    private String previewLocation;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private UserService userService;

    @GetMapping("/images/{tenant}/logo")
    @ApiOperation(value = "Get tenant logo image for <img> tag 'src' property")
    public void getLogoImage(HttpServletResponse servletResponse, @ApiParam(name = "tenant", value = "Tenant code") @PathVariable("tenant") String tenant) {

        Resource[] logoResources = new Resource[]{};
        try {
            logoResources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(FILE + uploadLocation + "/" + tenant + "/logo/logo.*");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Optional<Resource> first = Arrays.stream(logoResources).filter(a -> a.getFilename().lastIndexOf("svg") > 0 || a.getFilename().lastIndexOf("png") > 0).findFirst();
        Resource image = first.isPresent()?first.get():null;

        if(image!=null && image.getFilename()!=null) {
            if (image.getFilename().lastIndexOf(".png") > 0) {
                servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
            } else if (image.getFilename().lastIndexOf(".svg") > 0) {
                servletResponse.setContentType("image/svg+xml");
            } else if (image.getFilename().lastIndexOf(".jpg") > 0 || image.getFilename().lastIndexOf(".jpeg") > 0) {
                servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
            }
            servletResponse.setHeader("org-name", organizationService.findOrganizationByTenantCode(tenant).getName());
            try {
                StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @GetMapping("/images/{tenant}/{granteeOrgId}/logo")
    @ApiOperation(value = "Get grantee logo image for <img> tag 'src' property")
    public void getGranteeLogoImage(HttpServletResponse servletResponse, @PathVariable("granteeOrgId") Long granteeOrgId, @PathVariable("tenant") String tenant) {

        Resource image = resourceLoader.getResource(FILE + uploadLocation + "/GRANTEES/" + granteeOrgId + "/logo/logo.png");

        if (!image.exists()) {
            image = resourceLoader.getResource("classpath:static/images/orglogo.png");
        }
        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        servletResponse.setHeader("org-name", organizationService.findOrganizationByTenantCode(tenant).getName());
        try {
            StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @GetMapping("/images/profile/{userId}")
    public void getUserProfile(HttpServletResponse servletResponse,
                               @PathVariable("userId") Long userId) {

        User user = userService.getUserById(userId);

        Resource image = null;
        if (user.getUserProfile() == null) {
            image = resourceLoader.getResource("classpath:static/images/profile-avatar.png");
            servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        } else {
            image = resourceLoader.getResource(FILE + user.getUserProfile());
            if (image.exists()) {
                String extension = user.getUserProfile().substring(user.getUserProfile().lastIndexOf(".") + 1);
                if (user.getUserProfile() != null && extension.equalsIgnoreCase("png")) {
                    servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
                } else if (user.getUserProfile() != null && (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))) {
                    servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
                }
            } else {
                image = resourceLoader.getResource("classpath:static/images/profile-avatar.png");
                servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
            }
        }

        try {
            StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @GetMapping("/tenant/{domain}")
    public String getTenantName(@PathVariable("domain") String domain) {
        Organization org = organizationService.findOrganizationByTenantCode(domain.toUpperCase());
        if (org != null) {
            return org.getName();
        } else {
            throw new ResourceNotFoundException("Invalid request to access Anudan");
        }
    }

    @GetMapping("/tenant/{domain}/navbar")
    public String getTenantNavbarColor(@PathVariable("domain") String domain) {
        Organization org = organizationService.findOrganizationByTenantCode(domain.toUpperCase());
        if (org.getOrganizationType().equalsIgnoreCase("GRANTER")) {
            return ((Granter)org).getNavbarColor();
        }else if (org.getOrganizationType().equalsIgnoreCase("PLATFORM")) {
            return "#ffffff;";
        } else {
            throw new ResourceNotFoundException("Invalid request to access Anudan");
        }
    }

    @GetMapping("/release")
    public Release getAppVersion() {
        return releaseService.getCurrentRelease();
    }

    @GetMapping("/doc/{url}")
    public void getDocumentForPreview(HttpServletResponse servletResponse, @PathVariable("url") String url) {
        Resource image = resourceLoader.getResource(FILE + previewLocation + "/" + url);

        try {
            StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
