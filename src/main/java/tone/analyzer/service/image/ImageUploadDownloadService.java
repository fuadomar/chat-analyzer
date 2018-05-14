package tone.analyzer.service.image;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tone.analyzer.service.amazon.AmazonFileUploaderClient;

/** Created by Dell on 1/25/2018. */
@Service
public class ImageUploadDownloadService {

  @Autowired private AmazonFileUploaderClient amazonFileUploaderClient;

  public void findImageAsByteArray(
      HttpServletRequest request, HttpServletResponse response, String image, boolean isBase64Image)
      throws IOException {

    InputStream s3ObjectInputStream = null;
    OutputStream outputStream = null;
    OutputStream out = null;
    S3Object s3Object = null;
    try {

      s3Object = amazonFileUploaderClient.downloadFileFromS3bucket(image);
      s3ObjectInputStream = s3Object.getObjectContent();
      String contentType = s3Object.getObjectMetadata().getContentType();
      byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
      response.setContentLength((int) bytes.length);

      s3Object = amazonFileUploaderClient.downloadFileFromS3bucket(image);
      s3ObjectInputStream = s3Object.getObjectContent();
      String mimeType = request.getServletContext().getMimeType(image);
      response.setContentType(mimeType);
      outputStream = response.getOutputStream();
      IOUtils.copy(s3ObjectInputStream, outputStream);
    } catch (Exception ex) {
    } finally {

      if (outputStream != null) {
        outputStream.close();
      }
      if (s3ObjectInputStream != null) {
        s3ObjectInputStream.close();
      }
    }
  }
}
