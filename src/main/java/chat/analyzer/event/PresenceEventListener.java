package chat.analyzer.event;

import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.DTO.UserOnlinePresenceDTO;
import chat.analyzer.domain.repository.ParticipantRepository;
import chat.analyzer.domain.repository.UserAccountRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.DTO.LogoutEventDTO;

/**
 * Listener to track user presence. Sends notifications to the login destination when a connected
 * event is received and notifications to the logout destination when a disconnect event is received
 *
 * @author Sergi Almar
 *     <p>slighty modified by mozammal
 */
@Component
public class PresenceEventListener {

  private static final Logger LOG = LoggerFactory.getLogger(PresenceEventListener.class);

  private ParticipantRepository participantRepository;

  private SimpMessagingTemplate messagingTemplate;

  private String loginDestination;

  private String logoutDestination;

  @Value("${app.user.login}")
  private String loginTopic;

  private Map<String, String> sessionToUserIdMap;

  @Autowired private UserAccountRepository userAccountRepository;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired SimpUserRegistry simpUserRegistry;

  @Autowired
  public PresenceEventListener(
      SimpMessagingTemplate messagingTemplate, ParticipantRepository participantRepository) {
    this.messagingTemplate = messagingTemplate;
    this.participantRepository = participantRepository;
    this.sessionToUserIdMap = new ConcurrentHashMap<>();
  }

  public ParticipantRepository getParticipantRepository() {

    return participantRepository;
  }

  @EventListener
  private void handleSessionConnected(SessionConnectEvent event) {
    SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
    String userName = headers.getUser().getName();

    UserAccount userAccount = userAccountRepository.findByName(userName);

    UserOnlinePresenceDTO userOnlinePresenceDTO = new UserOnlinePresenceDTO(userName);
    userOnlinePresenceDTO.setId(userAccount.getId());

    List<UserOnlinePresenceDTO> onlineBuddyList =
        userAccountDao.findFullBuddyListOrOnlineBuddy(userName, false);
    if (onlineBuddyList == null) return;

    onlineBuddyList.forEach(
        userOnlinePresenceDTO1 -> {
          messagingTemplate.convertAndSendToUser(
              userOnlinePresenceDTO1.getUserName(), loginTopic, userOnlinePresenceDTO);
          LOG.info("currently users are online: {}", userOnlinePresenceDTO1.getUserName());
        });

    String sessionId = headers.getSessionId();
    LOG.info("logged in user session id: {}", sessionId);
    sessionToUserIdMap.put(sessionId, userName);
    participantRepository.add(userName, userOnlinePresenceDTO);
  }

  @EventListener
  private void handleSessionDisconnect(SessionDisconnectEvent event) {
    LOG.info("logged out user session id: {}", event.getSessionId());
    Optional.ofNullable(
            participantRepository.getParticipant(sessionToUserIdMap.get(event.getSessionId())))
        .ifPresent(
            login -> {
              List<UserOnlinePresenceDTO> onlineBuddyList =
                  userAccountDao.findFullBuddyListOrOnlineBuddy(login.getUserName(), false);
              for (UserOnlinePresenceDTO userOnlinePresenceDTO1 : onlineBuddyList) {
                messagingTemplate.convertAndSendToUser(
                    userOnlinePresenceDTO1.getUserName(),
                    logoutDestination,
                    new LogoutEventDTO(login.getUserName(), login.getId()));
              }
              participantRepository.removeParticipant(sessionToUserIdMap.get(event.getSessionId()));
            });
  }

  public void setLoginDestination(String loginDestination) {
    this.loginDestination = loginDestination;
  }

  public void setLogoutDestination(String logoutDestination) {
    this.logoutDestination = logoutDestination;
  }
}
