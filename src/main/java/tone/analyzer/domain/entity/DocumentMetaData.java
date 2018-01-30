package tone.analyzer.domain.entity;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Dell on 1/17/2018.
 */
@Document
public class DocumentMetaData implements Serializable {

  @Id
  private String id;

  private String name;

  private Date lastModified;

  private String fileLocation;

  private String thumbNail;

  public DocumentMetaData() {
  }

  public DocumentMetaData(String name, String fileLocation, Date lastModified) {
    this.setLastModified(lastModified);
    this.setName(name);
    this.setFileLocation(fileLocation);

  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public void setFileLocation(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public String getName() {
    return name;
  }

  public String getFileLocation()  {
    return fileLocation;
  }

  public String getThumbNail() {
    return thumbNail;
  }

  public void setThumbNail(String thumbNail) {
    this.thumbNail = thumbNail;
  }
}