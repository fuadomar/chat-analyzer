package chat.analyzer.domain.model;

import java.io.Serializable;

public class LogoutEvent implements Serializable{

  private String userName;

  private String id;

  public LogoutEvent(String username, String id) {
    this.userName = username;
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
