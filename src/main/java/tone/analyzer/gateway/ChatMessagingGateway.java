package tone.analyzer.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import tone.analyzer.dao.UserAccountDao;
import tone.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;
import tone.analyzer.domain.entity.ChatMessage;
import tone.analyzer.domain.entity.UserAccount;
import tone.analyzer.domain.repository.ChatMessageRepository;
import tone.analyzer.domain.model.LoginEvent;
import tone.analyzer.redis.service.RedisNotificationStorageService;
import tone.analyzer.service.chat.ChatService;
import tone.analyzer.utility.CommonUtility;

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

  public void notifyReceiverAboutMessage(tone.analyzer.domain.model.ChatMessage chatMessage) {

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

  public void sendMessageTo(tone.analyzer.domain.model.ChatMessage chatChatMessage) {
    chatService.sendMessageTo(chatChatMessage);
  }

  public List<ChatMessage> fetchAllMessagesBetweenTwoBuddy(String receiver, Principal principal) {

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
    List<ChatMessage> messagesBySenderAndReceiver =
        chatMessageRepository.findMessagesBySenderAndReceiver(sender, recipient.getName(), sort);
    return messagesBySenderAndReceiver;
  }
}
