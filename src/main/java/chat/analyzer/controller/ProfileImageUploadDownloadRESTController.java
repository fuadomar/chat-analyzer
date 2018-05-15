package chat.analyzer.controller;

import chat.analyzer.gateway.ProfileImageUploadDownloadGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/** Created by user on 1/28/2018. */
@RestController
public class ProfileImageUploadDownloadRESTController {

  @Autowired private ProfileImageUploadDownloadGateway profileImageUploadDownloadGateway;

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/profiles/images/{image}", method = RequestMethod.GET)
  public void retrieveImageAsByteArray(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("image") String image,
      Principal principal)
      throws IOException {

    profileImageUploadDownloadGateway.findImageAsByteArray(request, response, image, false);
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(
    value = "/upload/profile/images",
    method = RequestMethod.POST,
    consumes = {"multipart/form-data"}
  )
  public String upload(@RequestPart(value = "file", required = true) MultipartFile file)
      throws IOException {

    if (!file.isEmpty()) {
      return profileImageUploadDownloadGateway.upload(file);
    }
    return null;
  }
}
