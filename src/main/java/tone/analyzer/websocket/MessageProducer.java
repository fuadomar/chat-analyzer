package tone.analyzer.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.domain.repository.ParticipantRepository;

import java.text.SimpleDateFormat;
import java.util.Collection;

/** Created by mozammal on 4/11/17. */
@Component
public class MessageProducer {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  @Autowired private SimpMessagingTemplate template;

  @Autowired private ParticipantRepository participantRepository;

  public void sendMessageToRecipient(ChatMessage chatMessage) {

    this.template.convertAndSend(
        "/topic/message" + "-" + chatMessage.getRecipient(), chatMessage.getMessage());
  }

  public void sendMessageForLiveUser(LoginEvent loginevent) {

    participantRepository.add(loginevent.getUserName(), loginevent);
    /*Collection<LoginEvent> liveUsers = participantRepository.getActiveSessions().values();
    this.template.convertAndSend(
        "/queue/position-updates", liveUsers);*/
  }
}
