package tone.analyzer.domain.entity;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/** Created by user on 4/11/2017. */
@Document
public class Message implements Serializable{

  @Id private String id;

  private String sender;

  private String content;

  private Date createdTime;

  private String conversationId;

  private String receiver;

  public Message() {}

  public Message(
      String conversationId, String sender, String receiver, String content, Date createdTime) {
    this.content = content;
    this.conversationId = conversationId;
    this.sender = sender;
    this.receiver = receiver;
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

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }
}
