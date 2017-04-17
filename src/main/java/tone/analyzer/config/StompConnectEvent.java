package tone.analyzer.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.event.LoginEvent;
import tone.analyzer.websocket.MessageProducer;

/** Created by mozammal on 4/12/17. */
@Component
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {

  private final Log logger = LogFactory.getLog(StompConnectEvent.class);

  @Autowired private MessageProducer messageProducer;

  public void onApplicationEvent(SessionConnectEvent event) {
    StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

    String user = sha.getNativeHeader("user").get(0);
    logger.info("Connect event [sessionId: " + sha.getSessionId() + "; user: " + user + " ]");
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setRecipient(user);
    LoginEvent loginEvent = new LoginEvent(user);
    messageProducer.sendMessageForLiveUser(loginEvent);
  }
}
