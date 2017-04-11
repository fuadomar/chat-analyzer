package tone.analyzer.domain;

import java.util.Date;

/** Created by mozammal on 4/11/17. */
public class Message {

  private String message;

  private String author;

  private long time = new Date().getTime();

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public long getTime() {
    return this.time;
  }

  public void setTime(long time) {
    this.time = time;
  }
}
