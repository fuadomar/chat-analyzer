package chat.analyzer.domain.DTO;

import java.io.Serializable;
import java.util.Set;

/** Created by Dell on 1/29/2018. */
public class AwaitingChatMessageNotificationDetailsDTO implements Serializable {

  private String receiver;

  private Set<UserOnlinePresenceDTO> sender;

  public AwaitingChatMessageNotificationDetailsDTO() {}

  public AwaitingChatMessageNotificationDetailsDTO(
      String receiver, Set<UserOnlinePresenceDTO> sender) {
    this.receiver = receiver;
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Set<UserOnlinePresenceDTO> getSender() {
    return sender;
  }

  public void setSender(Set<UserOnlinePresenceDTO> sender) {
    this.sender = sender;
  }

  @Override
  public String toString() {
    return "AwaitingChatMessageNotificationDetailsDTO{"
        + "receiver='"
        + receiver
        + '\''
        + ", sender="
        + sender
        + '}';
  }
}
