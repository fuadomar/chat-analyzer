package tone.analyzer.controller;

import java.text.ParseException;

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
import org.springframework.web.bind.annotation.*;
import tone.analyzer.dao.UserAccountDao;
import tone.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;
import tone.analyzer.domain.entity.ChatMessage;
import tone.analyzer.domain.model.LoginEvent;
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

  @MessageExceptionHandler
  @MessageMapping("/dispose.all.queued.message.notification")
  public String DisposeAwaitingMessageNotificationForLoggedInUser(
      SimpMessageHeaderAccessor headerAccessor) {

    String sender = headerAccessor.getUser().getName();
    redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(sender, null);
    return "Ok";
  }

  @MessageExceptionHandler
  @MessageMapping("/dispose.ack.message.notification")
  public String DisposeAwaitingMessageNotificationForLoggedInUserBuUser(
      @Payload LoginEvent loginEvent, SimpMessageHeaderAccessor headerAccessor) {

    String msgSendToUser = headerAccessor.getUser().getName();
    redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(msgSendToUser, loginEvent);
    return "Ok";
  }
}
