package tone.analyzer.domain.model;

import java.io.Serializable;
import java.util.Map;

/** Created by Dell on 1/15/2018. */
public class UserEmailInvitationNotification implements Serializable {

  private String subject;

  private String token;

  private Map<String, Object> model;

  public UserEmailInvitationNotification() {}

  public UserEmailInvitationNotification(String subject, String token) {
    this.subject = subject;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
