package tone.analyzer.domain.entity;

import java.io.Serializable;
import org.springframework.data.mongodb.core.mapping.Document;

/** Created by user on 1/8/2018. */
public class BuddyDetails implements Serializable{

  private String id;

  private String name;

  public BuddyDetails() {}

  public BuddyDetails(String id, String name) {
    this.id = id;
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BuddyDetails that = (BuddyDetails) o;

    if (name != null ? !name.equals(that.name) : that.name!= null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result;
    return result;
  }
}
