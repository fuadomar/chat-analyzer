package tone.analyzer.controller;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import tone.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;
import tone.analyzer.domain.entity.UserAccount;
import tone.analyzer.domain.entity.ChatMessage;
import tone.analyzer.domain.repository.ChatMessageRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.gateway.ChatMessagingGateway;
import tone.analyzer.redis.service.RedisNotificationStorageService;
import tone.analyzer.utility.CommonUtility;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

/** Created by mozammal on 4/11/17. */
@RestController
public class ChatMessagingRESTController {

  private static final Logger LOG = LoggerFactory.getLogger(ChatMessagingRESTController.class);

  @Value("${app.user.unseen.message.topic}")
  private String unseenMessageTopic;

  @Autowired private ChatMessagingGateway chatMessagingGateway;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired private RedisNotificationStorageService redisNotificationStorageService;

  @Autowired private SimpMessagingTemplate simpMessagingTemplate;

  /* @Autowired private UserAccountDao userAccountRepository;*/

  @Autowired private CommonUtility commonUtility;

  @RequestMapping(
    value = "/fetch/messages",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<ChatMessage> fetchAllMessagesBetweenTwoBuddy(
      @RequestParam("receiver") String receiver, Principal principal) {

    return chatMessagingGateway.fetchAllMessagesBetweenTwoBuddy(receiver, principal);
  }

  @MessageExceptionHandler
  @MessageMapping("/send.message")
  public String sendChatMessageFromSenderToReceiver(
      @Payload tone.analyzer.domain.model.ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor)
      throws ParseException {

    String sender = headerAccessor.getUser().getName();
    chatMessage.setSender(sender);
    chatMessagingGateway.sendMessageTo(chatMessage);
    chatMessagingGateway.notifyReceiverAboutMessage(chatMessage);

    /*LoginEvent loginEvent = new LoginEvent(chatMessage.getRecipient(), false);
    Date date = new Date();
    LOG.info("date {}", date.toString());
    loginEvent.setDate(date);

    UserAccount friendUserAccount = userAccountDao.findByName(chatMessage.getSender());
    loginEvent.setId(friendUserAccount.getId());
    loginEvent.setUserName(friendUserAccount.getName());
    loginEvent.setProfileImage(
        friendUserAccount.getDocumentMetaData() != null
            ? friendUserAccount.getDocumentMetaData().getThumbNail()
            : "");

    AwaitingChatMessageNotificationDetailsDTO awaitingChatMessageNotificationDetailsDTO =
        new AwaitingChatMessageNotificationDetailsDTO(
            chatMessage.getRecipient(), new HashSet<>(Arrays.asList(loginEvent)));
    awaitingChatMessageNotificationDetailsDTO =
        redisNotificationStorageService.cacheUserAwaitingMessagesNotification(
            chatMessage.getRecipient(), awaitingChatMessageNotificationDetailsDTO);

    simpMessagingTemplate.convertAndSendToUser(
        chatMessage.getRecipient(), unseenMessageTopic, awaitingChatMessageNotificationDetailsDTO);*/

    return "Ok";
  }

  @SubscribeMapping("/chat.participants")
  public Collection<LoginEvent> retrieveLoggedInUserBuddyListWithOnlineStatus(
      SimpMessageHeaderAccessor headerAccessor) {

    LOG.info("retrieveParticipants method fired");

    String userName = headerAccessor.getUser().getName();
    return userAccountDao.findBuddyList(userName, true);
  }

  @SubscribeMapping("/unseen.messages")
  public AwaitingChatMessageNotificationDetailsDTO sendAwaitingMessagesNotificationsToLoggedInUser(
      SimpMessageHeaderAccessor headerAccessor) {

    String sender = headerAccessor.getUser().getName();

    AwaitingChatMessageNotificationDetailsDTO cachedUserAwaitingMessagesNotifications =
        redisNotificationStorageService.findCachedUserAwaitingMessagesNotifications(sender);
    LOG.info("unseen messages method fired {}", cachedUserAwaitingMessagesNotifications);
    return cachedUserAwaitingMessagesNotifications;
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/dispose_all_message_notification", method = RequestMethod.GET)
  public String DisposeAwaitingMessageNotificationForLoggedInUser(Principal principal) {

    String sender;
    if (principal instanceof OAuth2Authentication) {
      sender =
          new CommonUtility().findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
    } else {
      sender = principal.getName();
    }

    redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(sender, null);
    return "Ok";
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/dispose_message_notification_by_user", method = RequestMethod.POST)
  public String DisposeAwaitingMessageNotificationForLoggedInUserBuUser(
      @RequestBody LoginEvent loginEvent, Principal principal) {

    String sender;

    if (principal instanceof OAuth2Authentication) {
      sender =
          new CommonUtility().findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
    } else {
      sender = principal.getName();
    }

    redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(sender, loginEvent);
    return "Ok";
  }
}
