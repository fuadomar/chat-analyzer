package chat.analyzer.controller;

import chat.analyzer.domain.model.ChatMessage;
import chat.analyzer.domain.model.LoginEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;

/** Created by mozammal on 4/27/17. */
public interface IInstantMessaging {

  @MessageMapping("/chat-message/message")
  @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
  public String sendChatMessageToDestination(ChatMessage chatMessage);

  @SubscribeMapping("/chat.participants")
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public Collection<LoginEvent> retrieveParticipants();
}
