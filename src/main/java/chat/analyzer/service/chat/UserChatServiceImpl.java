package chat.analyzer.service.chat;

import chat.analyzer.domain.entity.Conversation;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.DTO.ChatMessageDTO;
import chat.analyzer.domain.repository.ChatMessageRepository;
import chat.analyzer.domain.repository.ConversationRepository;
import chat.analyzer.domain.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import chat.analyzer.websocket.ChatMessageRelay;

import java.util.Date;

/** Created by mozammal on 4/11/17. */
@Component
public class UserChatServiceImpl implements UserChatService {

  @Autowired private ChatMessageRelay messageProducer;

  @Autowired private ConversationRepository conversationRepository;

  @Autowired private ChatMessageRepository chatMessageRepository;

  @Autowired private UserAccountRepository userAccountRepository;

  @Override
  public void sendChatMessageToDestination(ChatMessageDTO chatChatMessageDTO) {

    UserAccount recipient = userAccountRepository.findOne(chatChatMessageDTO.getRecipient());
    if (recipient == null) {
      return;
    }
    chatChatMessageDTO.setRecipient(recipient.getName());
    messageProducer.sendMessageToRecipient(chatChatMessageDTO);
    Conversation conversation =
        conversationRepository.findConversationBySenderAndRecipient(
            chatChatMessageDTO.getSender(), chatChatMessageDTO.getRecipient());
    if (conversation == null) {
      conversation =
          conversationRepository.save(
              new Conversation(chatChatMessageDTO.getSender(), chatChatMessageDTO.getRecipient()));
    }
    chatMessageRepository.save(
        new chat.analyzer.domain.entity.ChatMessage(
            conversation.getId(),
            chatChatMessageDTO.getSender(),
            chatChatMessageDTO.getRecipient(),
            chatChatMessageDTO.getMessage(),
            new Date()));
  }
}
