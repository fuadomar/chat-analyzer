package tone.analyzer.domain.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/** Created by mozammal on 4/20/17. */
@Document
public class Role {

  private String id;

  private String role;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return "Role{" + "id='" + id + '\'' + ", role='" + role + '\'' + '}';
  }
}
