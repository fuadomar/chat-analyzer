package tone.analyzer.domain.model;

import java.io.Serializable;
import java.util.Map;

/** Created by Dell on 1/15/2018. */
public class NewUserInvitationNotification implements Serializable {

  private String sender;

  private String receiver;

  private String token;

  private String url;

  private String subject;

  private Map<String, Object> model;

  public NewUserInvitationNotification() {}

  public NewUserInvitationNotification(String sender, String receiver, String subject, String token, String url) {
    this.sender = sender;
    this.receiver = receiver;
    this.token = token;
    this.subject = subject;
    this.url = url;
  }

  public String getSender() {
    return sender;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "NewUserInvitationNotification{" + "sender='" + sender + '\'' + '}';
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Map<String, Object> getModel() {
    return model;
  }

  public void setModel(Map<String, Object> model) {
    this.model = model;
  }
}
