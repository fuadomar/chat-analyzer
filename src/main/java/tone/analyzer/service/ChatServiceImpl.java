package tone.analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.entity.Conversation;
import tone.analyzer.domain.entity.Message;
import tone.analyzer.domain.repository.ConversationRepository;
import tone.analyzer.domain.repository.MessageRepository;
import tone.analyzer.websocket.MessageProducer;

import java.util.Date;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatServiceImpl implements ChatService {

  @Autowired private MessageProducer messageProducer;

  @Autowired private ConversationRepository conversationRepository;

  @Autowired private MessageRepository messageRepository;

  @Override
  public void sendMessageTo(ChatMessage chatMessage) {
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
            conversation.getId(), chatMessage.getSender(), chatMessage.getMessage(), new Date()));
  }
}
