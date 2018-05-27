package chat.analyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import chat.analyzer.domain.repository.ChatAnalyzerImageToneDetailsRepository;
import chat.analyzer.gateway.ChatAnalyzerChartImagesUploadDownloadGateway;
import chat.analyzer.utility.CommonUtility;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/** Created by user on 1/28/2018. */
@RestController
public class ChatAnalyzerToneChartImageUploaderController {

  private static final Logger LOG =
      LoggerFactory.getLogger(ChatAnalyzerToneChartImageUploaderController.class);

  @Value("${tone.analyzer.image.repository")
  private String toneAnalyzerImageStorageLocation;

  @Autowired private ChatAnalyzerImageToneDetailsRepository chatAnalyzerImageToneDetailsRepository;

  @Autowired
  private ChatAnalyzerChartImagesUploadDownloadGateway chatAnalyzerChartImagesUploadDownloadGateway;

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
