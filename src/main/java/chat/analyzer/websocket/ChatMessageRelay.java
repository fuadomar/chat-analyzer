package chat.analyzer.websocket;

import chat.analyzer.domain.DTO.ChatMessageDTO;
import chat.analyzer.domain.DTO.UserOnlinePresenceDTO;
import chat.analyzer.domain.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatMessageRelay {

  @Value("${app.user.message.topic}")
  private String messageTopic;

  @Autowired private SimpMessagingTemplate simpMessagingTemplate;

  @Autowired private ParticipantRepository participantRepository;

  public void sendMessageToRecipient(ChatMessageDTO chatMessageDTO) {

    this.simpMessagingTemplate.convertAndSendToUser(
        chatMessageDTO.getRecipient(), messageTopic, chatMessageDTO);
  }

  public void sendMessageForLiveUser(UserOnlinePresenceDTO loginevent) {

    participantRepository.add(loginevent.getUserName(), loginevent);
  }
}
