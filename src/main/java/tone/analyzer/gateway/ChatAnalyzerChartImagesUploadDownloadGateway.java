package tone.analyzer.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import tone.analyzer.dao.ImageRepository;
import tone.analyzer.domain.entity.ChatAnalyzerImageToneDetails;
import tone.analyzer.domain.entity.DocumentMetaData;
import tone.analyzer.domain.model.Document;
import tone.analyzer.domain.repository.ChatAnalyzerImageToneDetailsRepository;
import tone.analyzer.service.fileuploader.FileUploadService;
import tone.analyzer.service.image.ImageUploadDownloadService;
import tone.analyzer.utility.CommonUtility;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class ChatAnalyzerChartImagesUploadDownloadGateway {

  private static final Logger LOG =
      LoggerFactory.getLogger(ChatAnalyzerChartImagesUploadDownloadGateway.class);

  @Autowired private CommonUtility commonUtility;

  @Autowired private ImageRepository imageRepository;

  @Autowired private ChatAnalyzerImageToneDetailsRepository chatAnalyzerImageToneDetailsRepository;

  @Autowired private FileUploadService fileUploadService;

  @Autowired private ImageUploadDownloadService profileImageService;

  public String upload(MultipartFile file) throws IOException {

    return fileUploadService.upload(file);
  }

  public void findImageAsByteArray(
      HttpServletRequest request, HttpServletResponse response, String image, boolean isBase64Image)
      throws IOException {

    profileImageService.findImageAsByteArray(request, response, image, isBase64Image);
  }

  public String uploadBase64Image(String image, HttpServletRequest request, String loggedInUser) {
    try {

      String imageName = commonUtility.generateImageNameFromUser(loggedInUser, "png");

      LOG.info("image base64:  {}", image);
      String delimiter = "data:image/png;base64,";
      int imageLength = image.length();
      String base64Image = image.substring(delimiter.length(), imageLength - 2);

      byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

      Document document = new Document(imageName, imageBytes);
      DocumentMetaData documentMetaData = new DocumentMetaData(imageName, new Date());
      imageRepository.add(document, true);
      chatAnalyzerImageToneDetailsRepository.save(
          new ChatAnalyzerImageToneDetails(loggedInUser, documentMetaData));
      return commonUtility.findBaseUrl(request) + "/tone_analyzer/images/" + document.getName();
    } catch (Exception ex) {
      LOG.info("Exception inside method uploadBase64Image: {}", ex);
      return "error = " + ex.getCause();
    }
  }
}
