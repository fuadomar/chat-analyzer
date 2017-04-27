package tone.analyzer.event;

public class LogoutEvent {

  private String userName;

  public LogoutEvent(String username) {
    this.userName = username;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
