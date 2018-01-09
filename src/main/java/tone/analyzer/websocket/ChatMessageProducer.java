package tone.analyzer.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.domain.repository.ParticipantRepository;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatMessageProducer {

  @Value("${app.user.message.topic}")
  private String messageTopic;

  @Autowired private SimpMessagingTemplate template;

  @Autowired private ParticipantRepository participantRepository;

  public void sendMessageToRecipient(ChatMessage chatMessage) {


    this.template.convertAndSend(messageTopic + "-" + chatMessage.getRecipient(), chatMessage);
  }

  public void sendMessageForLiveUser(LoginEvent loginevent) {

    participantRepository.add(loginevent.getUserName(), loginevent);
  }
}
