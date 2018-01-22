package tone.analyzer.domain.model;

import java.io.Serializable;

/** Created by mozammal on 4/11/17. */
public class ChatMessage implements Serializable{

  public ChatMessage() {}

  public ChatMessage(String topic, String recipient, String message) {
    this.recipient = recipient;
    this.message = message;
    this.topic = topic;
  }

  public ChatMessage(String recipient, String message) {
    this.recipient = recipient;
    this.message = message;
  }

  private String recipient;

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  private String sender;

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  private String topic;

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  @Override
  public String toString() {
    return "ChatMessage{"
        + "recipient='"
        + recipient
        + '\''
        + ", sender='"
        + sender
        + '\''
        + ", message='"
        + message
        + '\''
        + ", topic='"
        + topic
        + '\''
        + '}';
  }
}
