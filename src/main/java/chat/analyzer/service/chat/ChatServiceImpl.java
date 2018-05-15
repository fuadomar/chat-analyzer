package chat.analyzer.service.chat;

import chat.analyzer.domain.entity.Conversation;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.repository.ChatMessageRepository;
import chat.analyzer.domain.repository.ConversationRepository;
import chat.analyzer.domain.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import chat.analyzer.websocket.ChatMessageProducer;

import java.util.Date;

/** Created by mozammal on 4/11/17. */
@Component
public class ChatServiceImpl implements ChatService {

  @Autowired private ChatMessageProducer messageProducer;

  @Autowired private ConversationRepository conversationRepository;

  @Autowired private ChatMessageRepository chatMessageRepository;

  @Autowired private UserAccountRepository userAccountRepository;

  @Override
  public void sendMessageTo(chat.analyzer.domain.model.ChatMessage chatChatMessage) {

    UserAccount recipient = userAccountRepository.findOne(chatChatMessage.getRecipient());
    if (recipient == null) {
      return;
    }
    chatChatMessage.setRecipient(recipient.getName());
    messageProducer.sendMessageToRecipient(chatChatMessage);
    Conversation conversation =
        conversationRepository.findConversationBySenderAndRecipient(
            chatChatMessage.getSender(), chatChatMessage.getRecipient());
    if (conversation == null) {
      conversation =
          conversationRepository.save(
              new Conversation(chatChatMessage.getSender(), chatChatMessage.getRecipient()));
    }
    chatMessageRepository.save(
        new chat.analyzer.domain.entity.ChatMessage(
            conversation.getId(),
            chatChatMessage.getSender(),
            chatChatMessage.getRecipient(),
            chatChatMessage.getMessage(),
            new Date()));
  }
}
