package chat.analyzer.config;

import chat.analyzer.domain.DTO.ChatMessageDTO;
import chat.analyzer.domain.DTO.UserOnlinePresenceDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import chat.analyzer.websocket.ChatMessageRelay;

/** Created by mozammal on 4/12/17. */
@Component
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {

  private final Log LOG = LogFactory.getLog(StompConnectEvent.class);

  @Autowired private ChatMessageRelay messageProducer;

  public void onApplicationEvent(SessionConnectEvent event) {
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

    String user = headers.getUser().getName();
    LOG.debug("Connect event [sessionId: " + headers.getSessionId() + "; user: " + user + " ]");
    ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
    chatMessageDTO.setRecipient(user);
    UserOnlinePresenceDTO userOnlinePresenceDTO = new UserOnlinePresenceDTO(user);
    messageProducer.sendMessageForLiveUser(userOnlinePresenceDTO);
  }
}
