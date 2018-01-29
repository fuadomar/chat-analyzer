package tone.analyzer.domain.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/** Created by Dell on 1/29/2018. */
public class AwaitingMessagesNotificationDetailsDTO implements Serializable {

  private String receiver;

  private Set<String> sender;

  public AwaitingMessagesNotificationDetailsDTO() {}

  public AwaitingMessagesNotificationDetailsDTO(String receiver, Set<String> sender) {
    this.receiver = receiver;
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Set<String> getSender() {
    return sender;
  }

  public void setSender(Set<String> sender) {
    this.sender = sender;
  }
}
