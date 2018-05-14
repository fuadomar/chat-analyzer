package tone.analyzer.domain.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PasswordResetToken {

  private static final int EXPIRATION = 60 * 24;

  @Id private String id;

  private String token;

  private UserAccount user;

  private Date expiryDate;

  public PasswordResetToken() {}

  public PasswordResetToken(String token, UserAccount user) {
    this.token = token;
    this.user = user;
  }

  public static int getEXPIRATION() {
    return EXPIRATION;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserAccount getUser() {
    return user;
  }

  public void setUser(UserAccount user) {
    this.user = user;
  }

  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }
}
