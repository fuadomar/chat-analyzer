package tone.analyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.entity.DocumentMetaData;
import tone.analyzer.domain.entity.ChatAnalyzerImageToneDetails;
import tone.analyzer.domain.repository.ChatAnalyzerImageToneDetailsRepository;
import tone.analyzer.gateway.ChatAnalyzerChartImagesUploadDownloadGateway;
import tone.analyzer.utility.CommonUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/** Created by user on 1/28/2018. */
@RestController
public class ChatAnalyzerChartImagesUploadsDownloadRESTController {

  private static final Logger LOG =
      LoggerFactory.getLogger(ChatAnalyzerChartImagesUploadsDownloadRESTController.class);

  @Value("${tone.analyzer.image.repository")
  private String toneAnalyzerImageStorageLocation;

  @Autowired private ChatAnalyzerImageToneDetailsRepository chatAnalyzerImageToneDetailsRepository;

  @Autowired
  private ChatAnalyzerChartImagesUploadDownloadGateway chatAnalyzerChartImagesUploadDownloadGateway;

  @RequestMapping(value = "/tone_analyzer/images/{image}", method = RequestMethod.GET)
  public void retrieveImageAsByteArray(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("image") String image,
      Principal principal)
      throws IOException {

    ChatAnalyzerImageToneDetails chatAnalyzerImageToneDetails =
        chatAnalyzerImageToneDetailsRepository.findByDocumentMetaDataName(image);
    if (chatAnalyzerImageToneDetails == null
        || chatAnalyzerImageToneDetails.getDocumentMetaData() == null) {
      return;
    }
    DocumentMetaData documentMetaData = chatAnalyzerImageToneDetails.getDocumentMetaData();
    if (documentMetaData != null && !StringUtils.isEmpty(documentMetaData.getName())) {
      chatAnalyzerChartImagesUploadDownloadGateway.findImageAsByteArray(
          request, response, image, true);
    }
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/tone_analyzer/upload/images", method = RequestMethod.POST)
  public @ResponseBody String uploadBase64Image(
      @RequestParam("image") String image, HttpServletRequest request, Principal principal) {
    try {

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String loggedInUser;
      if (auth instanceof OAuth2Authentication) {
        loggedInUser = new CommonUtility().findPrincipalNameFromAuthentication(auth);
      } else {
        loggedInUser = auth.getName();
      }

      return chatAnalyzerChartImagesUploadDownloadGateway.uploadBase64Image(
          image, request, loggedInUser);

    } catch (Exception ex) {
      LOG.info("Exception inside method uploadBase64Image: {}", ex);
      return "error = " + ex.getCause();
    }
  }
}
