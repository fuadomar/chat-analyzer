package tone.analyzer.domain.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import tone.analyzer.event.LoginEvent;

/**
 * Created by Dell on 1/29/2018.
 */
public class AwaitingMessagesNotificationDetailsDTO implements Serializable {

    private String receiver;

    private Set<LoginEvent> sender;

    public AwaitingMessagesNotificationDetailsDTO() {
    }

    public AwaitingMessagesNotificationDetailsDTO(String receiver, Set<LoginEvent> sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Set<LoginEvent> getSender() {
        return sender;
    }

    public void setSender(Set<LoginEvent> sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "AwaitingMessagesNotificationDetailsDTO{" +
                "receiver='" + receiver + '\'' +
                ", sender=" + sender +
                '}';
    }
}
