package tone.analyzer.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tone.analyzer.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Created by mozammal on 4/11/17. */
@Component
public class MessageProducer {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  @Autowired private SimpMessagingTemplate template;

  public void sendMessageTo(ChatMessage chatMessage) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    builder.append(dateFormatter.format(new Date()));
    builder.append("] ");
    builder.append(chatMessage.getMessage());

    this.template.convertAndSend(
        "/topic/message" + "-" + chatMessage.getRecipient(), builder.toString());
    /* this.template.convertAndSendToUser(name, "/queue/position-updates", builder.toString());*/
  }
}
