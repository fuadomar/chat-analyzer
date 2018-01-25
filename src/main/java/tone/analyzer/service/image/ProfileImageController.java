package tone.analyzer.service.image;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.UserAccountRepository;
import tone.analyzer.utility.ToneAnalyzerUtility;

/** Created by Dell on 1/25/2018. */
@RestController
public class ProfileImageController {

  @Autowired private ServletContext servletContext;

  @Autowired private ToneAnalyzerUtility toneAnalyzerUtility;

  @Autowired private UserAccountRepository userAccountRepository;

  @RequestMapping(value = "profiles/images/{image}", method = RequestMethod.GET)
  public void getImageAsByteArray(@PathVariable("image")  String image, HttpServletResponse response, Principal principal)
      throws IOException {

    String loggedInUserName =
        toneAnalyzerUtility.findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
    Account loggedInUser = userAccountRepository.findByName(loggedInUserName);

    String fileLocation =
        loggedInUser.getDocumentMetaData() != null
            ? loggedInUser.getDocumentMetaData().getFileLocation()
                + loggedInUser.getDocumentMetaData().getName()
            : null;

    final InputStream in = servletContext.getResourceAsStream(fileLocation);
    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
    IOUtils.copy(in, response.getOutputStream());
  }
}
