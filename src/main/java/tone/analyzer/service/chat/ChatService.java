package tone.analyzer.service.chat;

import tone.analyzer.domain.model.ChatMessage;

import java.security.Principal;

/** Created by mozammal on 4/11/17. */
public interface ChatService {

  public void sendMessageTo(ChatMessage chatMessage, Principal principal);
}
