package tone.analyzer.domain.entity;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/** Created by mozammal on 4/26/17. */
@Document
public class FlaggedMessage implements Serializable{

  @Id private String id;

  private String sender;

  private String content;

  List<String> likelyTone;

  List<Double> likelyToneScore;

  public FlaggedMessage() {}

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<String> getLikelyTone() {
    return likelyTone;
  }

  public void setLikelyTone(List<String> likelyTone) {
    this.likelyTone = likelyTone;
  }

  public List<Double> getLikelyToneScore() {
    return likelyToneScore;
  }

  public void setLikelyToneScore(List<Double> likelyToneScore) {
    this.likelyToneScore = likelyToneScore;
  }
}
