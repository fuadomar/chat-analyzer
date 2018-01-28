package tone.analyzer.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tone.analyzer.gateway.ProfileImageGateway;

/** Created by Dell on 1/16/2018. */
@RestController
public class ProfileController {

  @Autowired private ProfileImageGateway profileGateway;

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(
    value = "/upload/profile/images",
    method = RequestMethod.POST,
    consumes = {"multipart/form-data"}
  )
  public String upload(@RequestPart(value = "file", required = true) MultipartFile file)
      throws IOException {

    if (!file.isEmpty()) {

      return profileGateway.upload(file);
    }
    return null;
  }
}
