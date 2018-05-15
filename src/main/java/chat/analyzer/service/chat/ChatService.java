package chat.analyzer.service.chat;

import chat.analyzer.domain.model.ChatMessage;

/** Created by mozammal on 4/11/17. */
public interface ChatService {

  public void sendMessageTo(ChatMessage chatMessage);
}
