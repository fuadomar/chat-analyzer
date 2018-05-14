package tone.analyzer.domain.entity;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/** Created by mozammal on 4/20/17. */
@Document
public class Role implements Serializable {

  private String id;

  private String name;

  public Role() {}

  public Role(String name) {
    this.name = name;
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

  @Override
  public String toString() {
    return "Role{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
  }
}
