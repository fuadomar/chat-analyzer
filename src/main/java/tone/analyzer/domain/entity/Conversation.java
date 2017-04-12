package tone.analyzer.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Created by user on 4/11/2017. */
@Document
public class Conversation {

  @Id private String id;

  private String sender;

  private String recipient;

  public Conversation() {}

  public Conversation(String initiator, String recipient) {
    this.sender = initiator;
    this.recipient = recipient;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }
}
