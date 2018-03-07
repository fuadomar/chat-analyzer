package tone.analyzer.service.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Dell on 1/25/2018.
 */
@RestController
public class ProfileImageProviderService {

  @Value("${profile.thumb.image.repository}")
  private String profileImageStorageLocation;

  @Value("${tone.analyzer.image.repository}")
  private String toneAnalyzerImageStorageLocation;

  public void getImageAsByteArray(
      HttpServletRequest request,
      HttpServletResponse response,
      String image, boolean isBase64Image)
      throws IOException {

    String fullFilePath;
    if (!isBase64Image) {
      fullFilePath = profileImageStorageLocation + "/" + image;
    } else {
      fullFilePath = toneAnalyzerImageStorageLocation + "/" + image;
    }
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
