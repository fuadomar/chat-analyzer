package tone.analyzer.service.fileuploader;

import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tone.analyzer.dao.DocumentFileSystemRepository;
import tone.analyzer.domain.entity.DocumentMetaData;
import tone.analyzer.domain.model.Document;
import tone.analyzer.domain.repository.AccountRepository;

/** Created by Dell on 1/17/2018. */
@Service
public class FileUploadService {

  @Value("${file.repository}")
  private String fileStorageLocation;

  @Autowired DocumentFileSystemRepository documentFileSystemRepository;

  @Autowired private AccountRepository accountRepository;

  public void upload(MultipartFile file) throws IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    String loggedInUser = auth.getName();
    String loggedInUserSignature = auth.getName() + System.currentTimeMillis();
    String sha256hex =
        Hashing.sha256().hashString(loggedInUserSignature, StandardCharsets.UTF_8).toString();
    String fileName = sha256hex + file.getOriginalFilename();
    byte[] content = file.getBytes();
    Date currentDate = new Date();
    Document document = new Document(fileName, content);
    DocumentMetaData documentMetaData =
        new DocumentMetaData(fileName, fileStorageLocation, currentDate);
    documentFileSystemRepository.add(document);

    // documentRepository.save(documentMetaData);
  }
}
