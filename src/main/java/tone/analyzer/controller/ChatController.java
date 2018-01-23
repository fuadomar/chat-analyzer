package tone.analyzer.controller;

import io.indico.api.utils.IndicoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.dao.UserAccountDao;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.BuddyDetails;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.gateway.ChatGateway;
import tone.analyzer.domain.model.ChatMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/** Created by mozammal on 4/11/17. */
@RestController
public class ChatController {

  private static final Logger log = LoggerFactory.getLogger(ChatController.class);

  @Autowired private ChatGateway chatGateway;

  @Autowired private ParticipantRepository participantRepository;

  @Autowired private AccountRepository accountRepository;

  @Autowired private MessageRepository messageRepository;

  @Autowired private UserAccountDao userAccountDao;

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(
    value = "/fetch/messages",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Message> analyzeStatedPlacesTone(
      @RequestParam("sender") String sender, @RequestParam("receiver") String receiver)
      throws IOException, IndicoException, URISyntaxException {

    Account recipent = accountRepository.findOne(receiver.trim());
    if (recipent == null) return null;
    Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
    List<Message> messagesBySenderAndReceiver =
        messageRepository.findMessagesBySenderAndReceiver(sender, recipent.getName(), sort);

    return messagesBySenderAndReceiver;
  }

  @MessageExceptionHandler
  @MessageMapping("/send.message")
  public String sendChatMessageToDestination(
      @Payload ChatMessage chatMessage, Principal principal) {

    chatGateway.sendMessageTo(chatMessage, principal);
    return "Ok";
  }

  @SubscribeMapping("/chat.participants")
  public Collection<LoginEvent> retrieveBuddyList(SimpMessageHeaderAccessor headerAccessor) {
    log.info("retrieveParticipants method fired");

    String userName = headerAccessor.getUser().getName();
    return userAccountDao.retrieveBuddyList(userName, true);
  }
}
