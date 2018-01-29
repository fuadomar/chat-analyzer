package tone.analyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tone.analyzer.gateway.ProfileImageGateway;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;

/** Created by user on 1/28/2018. */
@RestController
public class ProfileImageController {

  @Autowired private ProfileImageGateway profileImageGateway;

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/profiles/images/{image}", method = RequestMethod.GET)
  public void retrieveImageAsByteArray(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("image") String image,
      Principal principal)
      throws IOException {

    profileImageGateway.retrieveImageAsByteArray(request, response, image, false);
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

      return profileImageGateway.upload(file);
    }
    return null;
  }
}
