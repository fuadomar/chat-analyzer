package chat.analyzer.controller;

import chat.analyzer.domain.entity.ChatMessage;
import java.text.ParseException;

import chat.analyzer.domain.DTO.ChatMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;
import chat.analyzer.domain.DTO.UserOnlinePresenceDTO;
import chat.analyzer.gateway.ChatMessagingGateway;
import chat.analyzer.redis.service.RedisNotificationStorageService;

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

  @PreAuthorize("hasRole('ROLE_USER')")
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
      @Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor)
      throws ParseException {

    String sender = headerAccessor.getUser().getName();
    chatMessageDTO.setSender(sender);
    chatMessagingGateway.sendMessageTo(chatMessageDTO);
    chatMessagingGateway.notifyReceiverAboutMessage(chatMessageDTO);
    return "Ok";
  }

  @SubscribeMapping("/chat.participants")
  public Collection<UserOnlinePresenceDTO> retrieveLoggedInUserBuddyListWithOnlineStatus(
      SimpMessageHeaderAccessor headerAccessor) {

    LOG.info("retrieveParticipants method fired");

    String userName = headerAccessor.getUser().getName();
    return userAccountDao.findFullBuddyListOrOnlineBuddy(userName, true);
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
  public String disposeAwaitingMessageNotificationForLoggedInUser(
      SimpMessageHeaderAccessor headerAccessor) {

    String sender = headerAccessor.getUser().getName();
    redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(sender, null);
    return "Ok";
  }

  @MessageExceptionHandler
  @MessageMapping("/dispose.ack.message.notification")
  public String disposeAwaitingMessageNotificationForLoggedInUserBuUser(
      @Payload UserOnlinePresenceDTO userOnlinePresenceDTO,
      SimpMessageHeaderAccessor headerAccessor) {

    String msgSendToUser = headerAccessor.getUser().getName();
    redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(
        msgSendToUser, userOnlinePresenceDTO);
    return "Ok";
  }
}
