package tone.analyzer.service.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.domain.entity.Conversation;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.domain.repository.ConversationRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.websocket.ChatMessageProducer;

import java.util.Date;

/**
 * Created by mozammal on 4/11/17.
 */
@Component
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageProducer messageProducer;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void sendMessageTo(ChatMessage chatMessage) {

        Account recipent = accountRepository.findOne(chatMessage.getRecipient());

        if (recipent == null)
            return;
        chatMessage.setRecipient(recipent.getName());
        messageProducer.sendMessageToRecipient(chatMessage);
        Conversation conversation =
                conversationRepository.findConversationBySenderAndRecipient(
                        chatMessage.getSender(), chatMessage.getRecipient());
        if (conversation == null) {
            conversation =
                    conversationRepository.save(
                            new Conversation(chatMessage.getSender(), chatMessage.getRecipient()));
        }
        messageRepository.save(
                new Message(
                        conversation.getId(), chatMessage.getSender(),chatMessage.getRecipient(), chatMessage.getMessage(), new Date()));
    }
}
