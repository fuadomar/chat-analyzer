package chat.analyzer.controller;

import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import chat.analyzer.domain.model.ChatMessage;
import chat.analyzer.gateway.ChatAnalyzerGateway;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Created by mozammal on 4/11/17. */
@RestController
public class ChatAnalyzerRESTController {

  @Autowired private ChatAnalyzerGateway chatAnalyzerGateway;

  private static final Logger LOG = LoggerFactory.getLogger(ChatAnalyzerRESTController.class);

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/chat-analyzer-between-users", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(
      @RequestParam("sender") String sender, @RequestParam("recipient") String recipient) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    chatMessage.setRecipient(recipient);
    return chatAnalyzerGateway.analyzeChatToneBetweenSenderAndReceiver(chatMessage);
  }
}
