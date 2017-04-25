package tone.analyzer.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Created by mozammal on 4/25/17. */
@Document
public class Review {

  @Id private String id;

  private String user;

  private String review;

  public Review() {}

  public Review(String user, String review) {
    this.user = user;
    this.review = review;
  }

  public String getReview() {
    return review;
  }

  public void setReview(String review) {
    this.review = review;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
