package tone.analyzer.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/** Created by user on 1/28/2018. */
@Document
public class ChatAnalyzerImageToneDetails implements Serializable {

  @Id private String id;

  private DocumentMetaData documentMetaData;

  private String chatSessionEndBy;

  public ChatAnalyzerImageToneDetails() {}

  public ChatAnalyzerImageToneDetails(String chatSessionEndBy, DocumentMetaData documentMetaData) {
    this.chatSessionEndBy = chatSessionEndBy;
    this.documentMetaData = documentMetaData;
  }

  public String getChatSessionEndBy() {
    return chatSessionEndBy;
  }

  public void setChatSessionEndBy(String chatSessionEndBy) {
    this.chatSessionEndBy = chatSessionEndBy;
  }

  public DocumentMetaData getDocumentMetaData() {
    return documentMetaData;
  }

  public void setDocumentMetaData(DocumentMetaData documentMetaData) {
    this.documentMetaData = documentMetaData;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
