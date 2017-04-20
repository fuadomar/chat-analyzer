package tone.analyzer.domain.entity;

import org.springframework.data.annotation.Id;

/** Created by mozammal on 4/18/17. */
public class User {

  @Id private String id;

  private String name;

  private String password;

  public User() {}

  public User(String email, String password) {
    this.name = email;
    this.password = password;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
