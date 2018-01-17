package tone.analyzer.dao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.Document;

/**
 * Created by Dell on 1/17/2018.
 */
@Component
public class DocumentFileSystemRepositoryImp implements DocumentFileSystemRepository {

  private static final Logger LOG = Logger.getLogger(DocumentFileSystemRepositoryImp.class);

  @Value("${file.repository}")
  private String fileStorageLocation;

  @PostConstruct
  public void init() {
    createDirectory(fileStorageLocation);
  }

  @Override
  public void add(Document document) throws IOException {

    if (org.springframework.util.StringUtils.isEmpty(document.getName())) {
      LOG.info("file name cant be null");
      throw new IOException();
    }

    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileStorageLocation, document.getName())));
    stream.write(document.getContent());
    stream.close();
  }

  private void createDirectory(String path) {
    File file = new File(path);
    file.mkdirs();

  }
}