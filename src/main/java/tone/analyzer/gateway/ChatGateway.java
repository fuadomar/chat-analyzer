package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.service.ChatService;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatGateway {

  @Autowired private ChatService chatService;

  public void sendMessageTo(ChatMessage chatMessage) {
    chatService.sendMessageTo(chatMessage);
  }
}
