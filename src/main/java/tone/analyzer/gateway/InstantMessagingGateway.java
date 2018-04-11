package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.service.chat.ChatService;

/**
 * Created by mozammal on 4/11/17.
 */
@Component
public class InstantMessagingGateway {

    @Autowired
    private ChatService chatService;

    public void sendMessageTo(ChatMessage chatMessage) {
        chatService.sendMessageTo(chatMessage);
    }
}
