package tone.analyzer.event;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tone.analyzer.domain.repository.ParticipantRepository;

/**
 * Listener to track user presence. Sends notifications to the login destination when a connected
 * event is received and notifications to the logout destination when a disconnect event is received
 *
 * @author Sergi Almar
 */
@Component
public class PresenceEventListener {

  private static final Logger log = LoggerFactory.getLogger(PresenceEventListener.class);

  private ParticipantRepository participantRepository;

  private SimpMessagingTemplate messagingTemplate;

  private String loginDestination;

  private String logoutDestination;

  private Map<String, String> sessionToUserIdMap;

  public PresenceEventListener(
      SimpMessagingTemplate messagingTemplate, ParticipantRepository participantRepository) {
    this.messagingTemplate = messagingTemplate;
    this.participantRepository = participantRepository;
    this.sessionToUserIdMap = new ConcurrentHashMap<>();
  }

  @EventListener
  private void handleSessionConnected(SessionConnectEvent event) {
    SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
    String userName = headers.getUser().getName();
    LoginEvent loginEvent = new LoginEvent(userName);
    messagingTemplate.convertAndSend(loginDestination, loginEvent);
    log.info("logged in user session id: {}", headers.getSessionId());
    sessionToUserIdMap.put(headers.getSessionId(), userName);
    // We store the session as we need to be idempotent in the disconnect event processing
    participantRepository.add(userName, loginEvent);
  }

  @EventListener
  private void handleSessionDisconnect(SessionDisconnectEvent event) {
    log.info("logged out user session id: {}", event.getSessionId());
    Optional.ofNullable(
            participantRepository.getParticipant(sessionToUserIdMap.get(event.getSessionId())))
        .ifPresent(
            login -> {
              messagingTemplate.convertAndSend(
                  logoutDestination, new LogoutEvent(login.getUserName()));
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
