package tone.analyzer.domain.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/** Created by user on 1/8/2018. */
public class BuddyDetails {

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
}
