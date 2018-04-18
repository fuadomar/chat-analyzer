package tone.analyzer.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import java.util.SimpleTimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import tone.analyzer.domain.repository.ParticipantRepository;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.gateway.InstantMessagingGateway;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.redis.service.RedisNotificationStorageService;
import tone.analyzer.utility.ToneAnalyzerUtility;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

/**
 * Created by mozammal on 4/11/17.
 */
@RestController
public class InstantMessagingRESTController {

    private static final Logger LOG = LoggerFactory.getLogger(InstantMessagingRESTController.class);

    @Value("${app.user.unseen.message.topic}")
    private String unseenMessageTopic;

    @Autowired
    private InstantMessagingGateway chatGateway;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserAccountDao userAccountDao;

    @Autowired
    private RedisNotificationStorageService redisNotificationStorageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(
            value = "/fetch/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Message> fetchAllMessagesBetweenTwoBuddy(
            @RequestParam("receiver") String receiver, Principal principal) {

        String sender;

        if (principal instanceof OAuth2Authentication) {
            sender =
                    new ToneAnalyzerUtility()
                            .findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
        } else {
            sender = principal.getName();
        }

        Account recipent = accountRepository.findOne(receiver.trim());
        if (recipent == null) {
            return null;
        }

        Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
        List<Message> messagesBySenderAndReceiver =
                messageRepository.findMessagesBySenderAndReceiver(sender, recipent.getName(), sort);
        return messagesBySenderAndReceiver;
    }

    @MessageExceptionHandler
    @MessageMapping("/send.message")
    public String sendChatMessageFromSenderToReceiver(
            @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
            throws ParseException {

        String sender = headerAccessor.getUser().getName();
        chatMessage.setSender(sender);
        chatGateway.sendMessageTo(chatMessage);

        LoginEvent loginEvent = new LoginEvent(chatMessage.getRecipient(), false);
        Date date = new Date();
        LOG.info("date {}", date.toString());
        loginEvent.setTime(date);

        Account friendAccount = accountRepository.findByName(chatMessage.getSender());
        loginEvent.setId(friendAccount.getId());
        loginEvent.setUserName(friendAccount.getName());
        loginEvent.setProfileImage(
                friendAccount.getDocumentMetaData() != null
                        ? friendAccount.getDocumentMetaData().getThumbNail()
                        : "");

        AwaitingMessagesNotificationDetailsDTO awaitingMessagesNotificationDetailsDTO =
                new AwaitingMessagesNotificationDetailsDTO(
                        chatMessage.getRecipient(), new HashSet<>(Arrays.asList(loginEvent)));
        awaitingMessagesNotificationDetailsDTO =
                redisNotificationStorageService.cacheUserAwaitingMessagesNotification(
                        chatMessage.getRecipient(), awaitingMessagesNotificationDetailsDTO);

        simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(), unseenMessageTopic, awaitingMessagesNotificationDetailsDTO);

        return "Ok";
    }

    @SubscribeMapping("/chat.participants")
    public Collection<LoginEvent> retrieveLoggedInUserBuddyListWithOnlineStatus(
            SimpMessageHeaderAccessor headerAccessor) {

        LOG.info("retrieveParticipants method fired");

        String userName = headerAccessor.getUser().getName();
        return userAccountDao.retrieveBuddyList(userName, true);
    }

    @SubscribeMapping("/unseen.messages")
    public AwaitingMessagesNotificationDetailsDTO sendAwaitingMessagesNotificationsToLoggedInUser(
            SimpMessageHeaderAccessor headerAccessor) {

        String sender = headerAccessor.getUser().getName();

        AwaitingMessagesNotificationDetailsDTO cachedUserAwaitingMessagesNotifications =
                redisNotificationStorageService.findCachedUserAwaitingMessagesNotifications(sender);
        LOG.info("unseen messages method fired {}", cachedUserAwaitingMessagesNotifications);
        return cachedUserAwaitingMessagesNotifications;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(
            value = "/dispose_all_message_notification",
            method = RequestMethod.GET
    )
    public String DisposeAwaitingMessageNotificationForLoggedInUser(Principal principal) {

        String sender;
        if (principal instanceof OAuth2Authentication) {
            sender =
                    new ToneAnalyzerUtility()
                            .findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
        } else {
            sender = principal.getName();
        }

        redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(sender, null);
        return "Ok";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(
            value = "/dispose_message_notification_by_user",
            method = RequestMethod.POST
    )
    public String DisposeAwaitingMessageNotificationForLoggedInUserBuUser(
            @RequestBody LoginEvent loginEvent, Principal principal) {

        String sender;

        if (principal instanceof OAuth2Authentication) {
            sender =
                    new ToneAnalyzerUtility()
                            .findPrincipalNameFromAuthentication((OAuth2Authentication) principal);
        } else {
            sender = principal.getName();
        }

        redisNotificationStorageService.deleteAwaitingMessageNotificationByUser(sender, loginEvent);
        return "Ok";
    }

}
