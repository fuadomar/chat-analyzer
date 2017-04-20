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
      @RequestParam("firstUser") String firstUser, @RequestParam("secondUser") String secondUser) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(firstUser);
    chatMessage.setRecipient(secondUser);
    return toneAnalyzerGateway.analyzerConversationalTone(chatMessage);
  }
}
