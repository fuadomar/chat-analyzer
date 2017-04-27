package tone.analyzer.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/** Created by user on 4/11/2017. */
@Document
public class Message {

  @Id private String id;

  private String sender;

  private String content;

  private Date createdTime;

  private String conversationId;

  public Message() {}

  public Message(String conversationId, String sender, String content, Date createdTime) {
    this.content = content;
    this.conversationId = conversationId;
    this.sender = sender;
    this.createdTime = createdTime;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }
}
