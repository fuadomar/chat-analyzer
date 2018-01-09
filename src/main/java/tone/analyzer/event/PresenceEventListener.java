package tone.analyzer.event;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.BuddyDetails;
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

    private static final Logger log = LoggerFactory.getLogger(PresenceEventListener.class);

    private ParticipantRepository participantRepository;

    private SimpMessagingTemplate messagingTemplate;

    private String loginDestination;

    private String logoutDestination;

    private Map<String, String> sessionToUserIdMap;

    @Autowired
    private AccountRepository accountRepository;

    public PresenceEventListener(
            SimpMessagingTemplate messagingTemplate, ParticipantRepository participantRepository) {
        this.messagingTemplate = messagingTemplate;
        this.participantRepository = participantRepository;
        this.sessionToUserIdMap = new ConcurrentHashMap<>();
    }

    public List<LoginEvent> retrieveBuddyList(String userName) {
        Account userAccount = accountRepository.findByName(userName);
        List<LoginEvent> buddyListObjects = new ArrayList<>();
        Set<BuddyDetails> buddyList = userAccount.getBuddyList();
        List<LoginEvent> onlineBuddyList = new ArrayList<>();

        if (buddyList == null)
            return buddyListObjects;

        for (BuddyDetails buddy : buddyList) {
            LoginEvent loginEvent = new LoginEvent(buddy.getName(), false);
            loginEvent.setId(buddy.getId());
            buddyListObjects.add(loginEvent);
        }
        List<LoginEvent> activeUser = new ArrayList<>(participantRepository.getActiveSessions().values());

        for (LoginEvent loginEvent : buddyListObjects)
            if (activeUser.contains(loginEvent)) {
                loginEvent.setOnline(true);
                onlineBuddyList.add(loginEvent);
            }
        return onlineBuddyList;
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String userName = headers.getUser().getName();

        Account account = accountRepository.findByName(userName);

        LoginEvent loginEvent = new LoginEvent(userName);
        loginEvent.setId(account.getId());
        if (participantRepository.getParticipant(userName) == null) {
           /* this.template.convertAndSend(messageTopic + "-" + chatMessage.getRecipient(), chatMessage);*/
            List<LoginEvent> onlineBuddyList = retrieveBuddyList(userName);
            for (LoginEvent loginEvent1 : onlineBuddyList)
                messagingTemplate.convertAndSend(loginDestination + "-" + loginEvent1.getUserName(), loginEvent);
        }
        String sessionId = headers.getSessionId();
        log.info("logged in user session id: {}", sessionId);
        sessionToUserIdMap.put(sessionId, userName);
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
                            List<LoginEvent> onlineBuddyList = retrieveBuddyList(login.getUserName());
                            for (LoginEvent loginEvent1 : onlineBuddyList)
                                messagingTemplate.convertAndSend(logoutDestination + "-" + loginEvent1.getUserName(), new LogoutEvent(login.getUserName(), login.getId()));
                           /* messagingTemplate.convertAndSend(
                                    logoutDestination, new LogoutEvent(login.getUserName(), login.getId()));*/
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
