package chat.analyzer.service.chat;

import chat.analyzer.domain.DTO.ChatMessageDTO;

/** Created by mozammal on 4/11/17. */
public interface UserChatService {

  void sendChatMessageToDestination(ChatMessageDTO chatMessageDTO);
}
