package chat.analyzer.service.fileuploader;

import chat.analyzer.domain.repository.UserAccountRepository;
import chat.analyzer.service.image.ImageUploadDownloadService;
import com.google.common.hash.Hashing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;
import chat.analyzer.dao.ImageRepository;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.entity.DocumentMetaData;
import chat.analyzer.domain.model.Document;
import chat.analyzer.utility.CommonUtility;

/** Created by Dell on 1/17/2018. */
@Service
public class FileUploadService {

  @Autowired ImageRepository documentFileSystemRepository;

  @Autowired private UserAccountRepository userUserAccountRepository;

  public String upload(MultipartFile file) throws IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    String loggedInUser;
    if (auth instanceof OAuth2Authentication) {
      loggedInUser = new CommonUtility().findPrincipalNameFromAuthentication(auth);
    } else {
      loggedInUser = auth.getName();
    }

    String loggedInUserSignature = loggedInUser + System.currentTimeMillis();
    String sha256hex =
        Hashing.sha256().hashString(loggedInUserSignature, StandardCharsets.UTF_8).toString();
    String fileName = sha256hex + file.getOriginalFilename();
    String thumbNail = sha256hex + "thumbnail" + file.getOriginalFilename();

    byte[] content = file.getBytes();

    Document document = new Document().createDocument(fileName, content).withThumNail(thumbNail);

    DocumentMetaData documentMetaData = new DocumentMetaData(fileName, new Date());
    documentMetaData.setThumbNail(thumbNail);
    documentFileSystemRepository.add(document);

    UserAccount loggedInUserUserAccount = userUserAccountRepository.findByName(loggedInUser);
    loggedInUserUserAccount.setDocumentMetaData(documentMetaData);
    userUserAccountRepository.save(loggedInUserUserAccount);

    BASE64Encoder encoder = new BASE64Encoder();
    return encoder.encode(document.getContent());
  }
}
