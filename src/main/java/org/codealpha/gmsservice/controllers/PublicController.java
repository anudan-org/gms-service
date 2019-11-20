package org.codealpha.gmsservice.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/public")
public class PublicController {

    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ResourceLoader resourceLoader;
    @Value("${spring.upload-file-location}")
    private String uploadLocation;

    @GetMapping("/images/{tenant}/logo")
    @ApiOperation(value = "Get tenant logo image for <img> tag 'src' property")
    public void getLogoImage(HttpServletResponse servletResponse, @ApiParam(name="tenant",value="Tenant code")@PathVariable("tenant") String tenant) {

        Resource image = resourceLoader.getResource("file:" + uploadLocation + "/" + tenant + "/logo/logo.png");
        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        try {
            StreamUtils.copy(image.getInputStream(), servletResponse.getOutputStream());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
