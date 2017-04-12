package tone.analyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tone.analyzer.ToneAnalyzerApplication;
import tone.analyzer.domain.repository.LoginEvent;
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
