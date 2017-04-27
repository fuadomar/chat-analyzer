package tone.analyzer.event;

import java.util.Date;

/** Created by mozammal on 4/12/17. */
public class LoginEvent {

  private String userName;

  private Date time;

  public LoginEvent(String username) {
    this.userName = username;
    time = new Date();
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }
}
