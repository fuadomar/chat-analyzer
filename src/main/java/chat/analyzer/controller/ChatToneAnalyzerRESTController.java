package chat.analyzer.controller;

import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import chat.analyzer.domain.DTO.ChatMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import chat.analyzer.gateway.ChatAnalyzerGateway;

/** Created by mozammal on 4/11/17. */
@RestController
public class ChatToneAnalyzerRESTController {

  @Autowired private ChatAnalyzerGateway chatAnalyzerGateway;

  private static final Logger LOG = LoggerFactory.getLogger(ChatToneAnalyzerRESTController.class);

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/chat-analyzer-between-users", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(
      @RequestParam("sender") String sender, @RequestParam("recipient") String recipient) {

    ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
    chatMessageDTO.setSender(sender);
    chatMessageDTO.setRecipient(recipient);
    return chatAnalyzerGateway.analyzeChatToneBetweenSenderAndReceiver(chatMessageDTO);
  }
}
