package chat.analyzer.gateway;

import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.model.LoginEvent;
import chat.analyzer.domain.repository.ChatMessageRepository;
import chat.analyzer.redis.service.RedisNotificationStorageService;
import chat.analyzer.service.chat.ChatService;
import chat.analyzer.utility.CommonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import chat.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatMessagingGateway {

  private static final Logger LOG = LoggerFactory.getLogger(ChatMessagingGateway.class);

  @Value("${app.user.unseen.message.topic}")
  private String unseenMessageTopic;

  @Autowired private ChatService chatService;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired private ChatMessageRepository chatMessageRepository;

  @Autowired private CommonUtility commonUtility;

  @Autowired private RedisNotificationStorageService redisNotificationStorageService;

  @Autowired private SimpMessagingTemplate simpMessagingTemplate;

  public void notifyReceiverAboutMessage(chat.analyzer.domain.model.ChatMessage chatMessage) {

    UserAccount senderAccount = userAccountDao.findByName(chatMessage.getSender());
    LoginEvent loginEvent =
        new LoginEvent()
            .withId(senderAccount.getId())
            .withUserName(senderAccount.getName())
            .withDate(new Date())
            .withUserProfileImage(
                senderAccount.getDocumentMetaData() != null
                    ? senderAccount.getDocumentMetaData().getThumbNail()
                    : "");

    AwaitingChatMessageNotificationDetailsDTO awaitingChatMessageNotificationDetailsDTO =
        new AwaitingChatMessageNotificationDetailsDTO(
            chatMessage.getRecipient(), new HashSet<>(Arrays.asList(loginEvent)));
    awaitingChatMessageNotificationDetailsDTO =
        redisNotificationStorageService.cacheUserAwaitingMessagesNotification(
            chatMessage.getRecipient(), awaitingChatMessageNotificationDetailsDTO);

    simpMessagingTemplate.convertAndSendToUser(
        chatMessage.getRecipient(), unseenMessageTopic, awaitingChatMessageNotificationDetailsDTO);
  }

  public void sendMessageTo(chat.analyzer.domain.model.ChatMessage chatChatMessage) {
    chatService.sendMessageTo(chatChatMessage);
  }

  public List<chat.analyzer.domain.entity.ChatMessage> fetchAllMessagesBetweenTwoBuddy(
      String receiver, Principal principal) {

    String sender;

    if (principal instanceof OAuth2Authentication) {
      sender = commonUtility.findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
    } else {
      sender = principal.getName();
    }

    UserAccount recipient = userAccountDao.findOne(receiver.trim());
    if (recipient == null) {
      return null;
    }

    Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
    List<chat.analyzer.domain.entity.ChatMessage> messagesBySenderAndReceiver =
        chatMessageRepository.findMessagesBySenderAndReceiver(sender, recipient.getName(), sort);
    return messagesBySenderAndReceiver;
  }
}
