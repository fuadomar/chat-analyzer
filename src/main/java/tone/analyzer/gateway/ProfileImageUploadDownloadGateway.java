package tone.analyzer.gateway;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tone.analyzer.service.fileuploader.FileUploadService;
import tone.analyzer.service.image.ImageUploadDownloadService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Created by Dell on 1/17/2018. */
@Component
public class ProfileImageUploadDownloadGateway {

  @Autowired private FileUploadService fileUploadService;

  @Autowired private ImageUploadDownloadService imageUploadDownloadService;

  public String upload(MultipartFile file) throws IOException {

    return fileUploadService.upload(file);
  }

  public void findImageAsByteArray(
      HttpServletRequest request, HttpServletResponse response, String image, boolean isBase64Image)
      throws IOException {

    imageUploadDownloadService.findImageAsByteArray(request, response, image, isBase64Image);
  }
}
