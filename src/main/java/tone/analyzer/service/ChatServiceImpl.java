package tone.analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.entity.Conversation;
import tone.analyzer.domain.repository.ConversationRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.websocket.MessageProducer;

/**
 * Created by mozammal on 4/11/17.
 */
@Component
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void sendMessageTo(ChatMessage chatMessage) {
        messageProducer.sendMessageTo(chatMessage);
        Conversation conversation = conversationRepository.findConversationByInitiatorAndRecipient(chatMessage.getSender(), chatMessage.getRecipient());
    }
}
