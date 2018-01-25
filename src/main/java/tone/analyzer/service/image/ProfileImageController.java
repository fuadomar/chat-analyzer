package tone.analyzer.service.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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

  @Value("${file.repository}")
  private String fileRepository;

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/profiles/images/{image}", method = RequestMethod.GET)
  public void getImageAsByteArray(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("image") String image,
      Principal principal)
      throws IOException {

    String fullFilePath = fileRepository + "/" + image;
    String mimeType = request.getServletContext().getMimeType(image);
    File profileImage = new File(fullFilePath);

    response.setContentType(mimeType);
    response.setContentLength((int) profileImage.length());

    FileInputStream fileInputStream = new FileInputStream(profileImage);
    OutputStream outputStream = response.getOutputStream();

    byte[] buf = new byte[1024];
    int count = 0;
    while ((count = fileInputStream.read(buf)) >= 0) {
      outputStream.write(buf, 0, count);
    }
    outputStream.close();
    fileInputStream.close();
  }
}
