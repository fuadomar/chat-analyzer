package chat.analyzer.domain.DTO;

import java.io.Serializable;
import java.util.Date;

/** Created by mozammal on 4/12/17. */
public class UserOnlinePresenceDTO implements Serializable {

  private String userName;

  private Date date;

  private boolean online;

  private String id;

  private String profileImage;

  public UserOnlinePresenceDTO() {}

  public UserOnlinePresenceDTO(String username) {
    this.userName = username;
    this.date = new Date();
    this.online = true;
  }

  public UserOnlinePresenceDTO(String username, boolean active) {
    this.userName = username;
    this.date = new Date();
    this.online = active;
  }

  @Override
  public int hashCode() {
    int result = userName.hashCode();
    result = 31 * result;
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserOnlinePresenceDTO)) {
      return false;
    }
    UserOnlinePresenceDTO other = (UserOnlinePresenceDTO) o;
    return other.getUserName().equals(this.userName);
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

  public UserOnlinePresenceDTO withUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public UserOnlinePresenceDTO withOnline(boolean online) {
    this.online = online;
    return this;
  }

  public UserOnlinePresenceDTO withDate(Date date) {
    this.date = date;
    return this;
  }

  public UserOnlinePresenceDTO withId(String id) {
    this.id = id;
    return this;
  }

  public UserOnlinePresenceDTO withUserProfileImage(String prodileImage) {
    this.profileImage = prodileImage;
    return this;
  }
}
