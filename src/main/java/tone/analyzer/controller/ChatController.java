package tone.analyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.gateway.ChatGateway;
import tone.analyzer.domain.ChatMessage;

import java.util.Collection;

/** Created by mozammal on 4/11/17. */
@RestController
public class ChatController {

  private static final Logger log = LoggerFactory.getLogger(ChatController.class);

  @Autowired private ChatGateway chatGateway;

  @Autowired private ParticipantRepository participantRepository;
  /* @RequestMapping(
    value = "/chat-message/{topic}"
  )*/
  @MessageMapping("/chat-message/message")
  public String sendChatMessageToDestination(ChatMessage chatMessage) {
    chatGateway.sendMessageTo(chatMessage);

    //messageProducer.sendMessageToRecipient(topic, recipient, message);
    return "Ok";
  }

  @SubscribeMapping("/chat.participants")
  public Collection<LoginEvent> retrieveParticipants() {
    log.info("retrieveParticipants method fired");
    return participantRepository.getActiveSessions().values();
  }
}
