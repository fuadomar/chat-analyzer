package chat.analyzer.websocket;

import chat.analyzer.domain.model.ChatMessage;
import chat.analyzer.domain.model.LoginEvent;
import chat.analyzer.domain.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatMessageProducer {

  @Value("${app.user.message.topic}")
  private String messageTopic;

  @Autowired private SimpMessagingTemplate template;

  @Autowired private ParticipantRepository participantRepository;

  public void sendMessageToRecipient(ChatMessage chatMessage) {

    this.template.convertAndSendToUser(chatMessage.getRecipient(), messageTopic, chatMessage);
  }

  public void sendMessageForLiveUser(LoginEvent loginevent) {

    participantRepository.add(loginevent.getUserName(), loginevent);
  }
}
