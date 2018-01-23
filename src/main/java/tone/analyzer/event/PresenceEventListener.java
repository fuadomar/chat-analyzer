package tone.analyzer.event;

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
import tone.analyzer.dao.UserAccountDao;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.domain.repository.ParticipantRepository;

/**
 * Listener to track user presence. Sends notifications to the login destination when a connected
 * event is received and notifications to the logout destination when a disconnect event is received
 *
 * @author Sergi Almar
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

  @Autowired private AccountRepository accountRepository;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired SimpUserRegistry simpUserRegistry;

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

    Account account = accountRepository.findByName(userName);

    LoginEvent loginEvent = new LoginEvent(userName);
    loginEvent.setId(account.getId());

    List<LoginEvent> onlineBuddyList = userAccountDao.retrieveBuddyList(userName, false);
    for (LoginEvent loginEvent1 : onlineBuddyList) {
      messagingTemplate.convertAndSendToUser(loginEvent1.getUserName(), loginTopic, loginEvent);
      LOG.info("currently users are online: {}", loginEvent1.getUserName());
    }

    String sessionId = headers.getSessionId();
    LOG.info("logged in user session id: {}", sessionId);
    sessionToUserIdMap.put(sessionId, userName);
    participantRepository.add(userName, loginEvent);
  }

  @EventListener
  private void handleSessionDisconnect(SessionDisconnectEvent event) {
    LOG.info("logged out user session id: {}", event.getSessionId());
    Optional.ofNullable(
            participantRepository.getParticipant(sessionToUserIdMap.get(event.getSessionId())))
        .ifPresent(
            login -> {
              List<LoginEvent> onlineBuddyList =
                  userAccountDao.retrieveBuddyList(login.getUserName(), false);
              for (LoginEvent loginEvent1 : onlineBuddyList) {
                messagingTemplate.convertAndSendToUser(
                    loginEvent1.getUserName(),
                    logoutDestination,
                    new LogoutEvent(login.getUserName(), login.getId()));
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
