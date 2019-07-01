package org.codealpha.gmsservice.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.codealpha.gmsservice.entities.DocKpiDataDocument;
import org.codealpha.gmsservice.services.DocKpiDataDocumentService;
import org.codealpha.gmsservice.services.GrantDocumentDataService;
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

@RestController
@RequestMapping("/submission/{submissionId}/kpi/{kpiId}")
public class KpiSubmissionController {

  private static Logger logger = LoggerFactory.getLogger(KpiSubmissionController.class);

  @Autowired
  private ResourceLoader resourceLoader;
  @Autowired
  private GrantDocumentDataService grantDocumentDataService;
  @Autowired
  private DocKpiDataDocumentService docKpiDataDocumentService;

  @Value("${spring.upload-file-location}")
  private String uploadLocation;

  @GetMapping("/file/{fileId}")
  public void getFile(@PathVariable("submissionId") Long submissionId,
      @PathVariable("kpiId") Long kpiId, @PathVariable("fileId") Long fileId,
      HttpServletResponse servletResponse) {

    //GrantDocumentKpiData documentKpiData = grantDocumentDataService.findByKpiIdAndSubmissionId(kpiId,submissionId);
    DocKpiDataDocument doc = docKpiDataDocumentService.getById(fileId);

    Resource file = resourceLoader.getResource("file:" + uploadLocation + doc.getFileName());
    switch (doc.getFileType()) {
      case "png":
        servletResponse.setContentType(MediaType.IMAGE_PNG_VALUE);
        break;
      case "jpg":
        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        break;
      case "jpeg":
        servletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);
        break;
      case "pdf":
        servletResponse.setContentType(MediaType.APPLICATION_PDF_VALUE);
        break;
      case "doc":
        servletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        break;
    }

    try {
      StreamUtils.copy(file.getInputStream(), servletResponse.getOutputStream());

    } catch (IOException ex) {
      logger.error(ex.getMessage(), ex);
    }
  }


}
