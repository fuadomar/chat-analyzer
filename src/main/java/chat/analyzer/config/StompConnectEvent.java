package chat.analyzer.config;

import chat.analyzer.domain.model.ChatMessage;
import chat.analyzer.domain.model.LoginEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import chat.analyzer.websocket.ChatMessageProducer;

/** Created by mozammal on 4/12/17. */
@Component
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {

  private final Log LOG = LogFactory.getLog(StompConnectEvent.class);

  @Autowired private ChatMessageProducer messageProducer;

  public void onApplicationEvent(SessionConnectEvent event) {
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

    String user = headers.getUser().getName();
    LOG.debug("Connect event [sessionId: " + headers.getSessionId() + "; user: " + user + " ]");
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setRecipient(user);
    LoginEvent loginEvent = new LoginEvent(user);
    messageProducer.sendMessageForLiveUser(loginEvent);
  }
}
