package tone.analyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.ChatMessage;
import tone.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import tone.analyzer.gateway.ToneAnalyzerGateway;

/** Created by mozammal on 4/11/17. */
@RestController
public class ToneAnalyzerController {

  @Autowired private ToneAnalyzerGateway toneAnalyzerGateway;

  @RequestMapping(value = "/tone-analyzer", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(
      @RequestParam("userId1") String userId1, @RequestParam("userId2") String userId2) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(userId1);
    chatMessage.setRecipient(userId2);
    return toneAnalyzerGateway.analyzerConversationalTone(chatMessage);
  }
}
