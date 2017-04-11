package tone.analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.model.ChatMessage;
import tone.analyzer.websocket.MessageProducer;

/**
 * Created by mozammal on 4/11/17.
 */
@Component
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public void sendMessageTo(ChatMessage chatMessage) {
        messageProducer.sendMessageTo(chatMessage);
    }
}
