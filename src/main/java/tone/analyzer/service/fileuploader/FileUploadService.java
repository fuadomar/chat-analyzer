package tone.analyzer.service.fileuploader;

import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;
import tone.analyzer.dao.ImageRepository;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.DocumentMetaData;
import tone.analyzer.domain.model.Document;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.utility.ToneAnalyzerUtility;

/**
 * Created by Dell on 1/17/2018.
 */
@Service
public class FileUploadService {

  @Value("${profile.image.repository}")
  private String profileImageStorageLocation;

  @Autowired
  ImageRepository documentFileSystemRepository;

  @Autowired
  private AccountRepository userAccountRepository;

  public String upload(MultipartFile file) throws IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    String loggedInUser;
    if (auth instanceof OAuth2Authentication) {
      loggedInUser = new ToneAnalyzerUtility().findPrincipalNameFromAuthentication(auth);
    } else {
      loggedInUser = auth.getName();
    }

    String loggedInUserSignature = loggedInUser + System.currentTimeMillis();
    String sha256hex =
        Hashing.sha256().hashString(loggedInUserSignature, StandardCharsets.UTF_8).toString();
    String fileName = sha256hex + file.getOriginalFilename();
    String thumNail = sha256hex + "thumnail" + file.getOriginalFilename();

    byte[] content = file.getBytes();
    Date currentDate = new Date();
    Document document = new Document(fileName, content);
    document.setThumbNail(thumNail);

    DocumentMetaData documentMetaData =
        new DocumentMetaData(fileName, profileImageStorageLocation, currentDate);
    documentMetaData.setThumbNail(thumNail);
    documentFileSystemRepository.add(document, false);

    Account loggedInUserAccount = userAccountRepository.findByName(loggedInUser);
    loggedInUserAccount.setDocumentMetaData(documentMetaData);
    userAccountRepository.save(loggedInUserAccount);

    BASE64Encoder encoder = new BASE64Encoder();
    return encoder.encode(document.getContent());
    // documentRepository.save(documentMetaData);
  }
}
