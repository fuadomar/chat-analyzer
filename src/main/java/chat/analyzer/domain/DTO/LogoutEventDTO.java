package chat.analyzer.domain.DTO;

import java.io.Serializable;

public class LogoutEventDTO implements Serializable {

  private String userName;

  private String id;

  public LogoutEventDTO(String username, String id) {
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
