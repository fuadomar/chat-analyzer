package tone.analyzer.gateway;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.service.chat.ChatService;
import tone.analyzer.service.fileuploader.FileUploadService;

/** Created by Dell on 1/17/2018. */
@Component
public class ProfileGateway {

  @Autowired
  FileUploadService fileUploadService;

  public String upload(MultipartFile file) throws IOException {

    return fileUploadService.upload(file);
  }
}
