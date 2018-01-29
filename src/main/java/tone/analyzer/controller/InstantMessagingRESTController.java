package tone.analyzer.controller;

import java.util.Arrays;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.dao.UserAccountDao;
import tone.analyzer.domain.DTO.AwaitingMessagesNotificationDetailsDTO;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.gateway.InstantMessagingGateway;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.redis.service.RedisNotificationStorageService;
import tone.analyzer.utility.ToneAnalyzerUtility;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

/** Created by mozammal on 4/11/17. */
@RestController
public class InstantMessagingRESTController {

  private static final Logger LOG = LoggerFactory.getLogger(InstantMessagingRESTController.class);

  @Autowired private InstantMessagingGateway chatGateway;

  @Autowired private AccountRepository accountRepository;

  @Autowired private MessageRepository messageRepository;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired private RedisNotificationStorageService redisNotificationStorageService;

  @Autowired private SimpMessagingTemplate template;

  @RequestMapping(
    value = "/fetch/messages",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<Message> fetchAllMessagesBetweenTwoBuddy(
      @RequestParam("receiver") String receiver, Principal principal) {

    String sender;

    if (principal instanceof OAuth2Authentication)
      sender =
          new ToneAnalyzerUtility()
              .findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
    else sender = principal.getName();

    Account recipent = accountRepository.findOne(receiver.trim());
    if (recipent == null) return null;

    Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
    List<Message> messagesBySenderAndReceiver =
        messageRepository.findMessagesBySenderAndReceiver(sender, recipent.getName(), sort);
    return messagesBySenderAndReceiver;
  }

  @MessageExceptionHandler
  @MessageMapping("/send.message")
  public String sendChatMessageFromSenderToReceiver(
      @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

    String sender = headerAccessor.getUser().getName();
    chatMessage.setSender(sender);
    chatGateway.sendMessageTo(chatMessage);

    AwaitingMessagesNotificationDetailsDTO awaitingMessagesNotificationDetailsDTO =
        new AwaitingMessagesNotificationDetailsDTO(
            chatMessage.getRecipient(), new HashSet<>(Arrays.asList(chatMessage.getSender())));
    redisNotificationStorageService.cacheUserAwaitingMessagesNotification(
        chatMessage.getRecipient(), awaitingMessagesNotificationDetailsDTO);

    return "Ok";
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @SubscribeMapping("/chat.participants")
  public Collection<LoginEvent> retrieveLoggedInUserBuddyListWithOnlineStatus(
      SimpMessageHeaderAccessor headerAccessor) {

    LOG.info("retrieveParticipants method fired");

    String userName = headerAccessor.getUser().getName();
    return userAccountDao.retrieveBuddyList(userName, true);
  }

  @MessageExceptionHandler
  @MessageMapping("/unseen.messages")
  public AwaitingMessagesNotificationDetailsDTO sendAwaitingMessagesNotificationsToLoggedInUser(
      SimpMessageHeaderAccessor headerAccessor) {

    String sender = headerAccessor.getUser().getName();

    AwaitingMessagesNotificationDetailsDTO cachedUserAwaitingMessagesNotifications =
        redisNotificationStorageService.findCachedUserAwaitingMessagesNotifications(sender);
    return cachedUserAwaitingMessagesNotifications;
  }
}
